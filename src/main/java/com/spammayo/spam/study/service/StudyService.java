package com.spammayo.spam.study.service;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.likes.Like;
import com.spammayo.spam.likes.LikeRepository;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.entity.StudyUser;
import com.spammayo.spam.study.repository.StudyRepository;
import com.spammayo.spam.study.repository.StudyUserRepository;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyUserRepository studyUserRepository;
    private final UserService userService;
    private final LikeRepository likeRepository;

    public Study createStudy(Study study) {
        User user = userService.getLoginUser();
        StudyUser studyUser = new StudyUser();
        studyUser.setUser(user);
        studyUser.setAdmin(true);
        studyUser.setApprovalStatus(StudyUser.ApprovalStatus.APPROVAL);
        study.addStudyUser(studyUser);

        return studyRepository.save(study);
    }

    public Study updateStudy(Study study) {
        Study findStudy = existStudy(study.getStudyId());
        //관리자만 접근 허용
        accessResource(findStudy);

        findStudy.setStudyName(study.getStudyName());
        findStudy.setStartDate(study.getStartDate());
        findStudy.setEndDate(study.getStartDate());
        findStudy.setPersonnel(study.getPersonnel());
        findStudy.setOnline(study.isOnline());
        findStudy.setPlace(study.getPlace());
        findStudy.setPlaceDetails(study.getPlaceDetails());
        findStudy.setAddress(study.getAddress());
        findStudy.setActivity(study.getActivity());
        findStudy.setPeriod(study.getPeriod());
        findStudy.setStudyStacks(study.getStudyStacks());

        return studyRepository.save(findStudy);
    }

    public Study findStudy(long studyId) {
        Study findStudy = existStudy(studyId);
        findStudy.setViews(findStudy.getViews() + 1);
        return studyRepository.save(findStudy);
    }

    /*
    * 스터디 폐쇄시
    * 1. 상태값 : CLOSED
    * 2. 신청 유저 삭제
    * 3. 좋아요, 신청, 조회, 수정 등등.. 다 불가
    * 4. 오직 마이페이지에서만 조회 가능 (?)
    * */
    public void deleteStudy(long studyId) {
        Study study = existStudy(studyId);
        accessResource(study);
        //TODO : 스터디 폐쇄시 처리로직 논의하기
        studyRepository.delete(study);
    }

    //tab : 참여중(crew), 생성(admin), 관심(likes) + 신청한(apply)
    //status : 전체, 진행중, 모집중, 모집전, 완료, 폐쇄
    public List<Study> getUserStudy(String tab, String status) {

        List<StudyUser> studyUsers = userService.getLoginUser().getStudyUsers();
        List<Study> studies;
        if (tab != null) {
            if (tab.equals("crew")) {
                studyUsers = studyUsers.stream().filter(studyUser -> studyUser.getApprovalStatus().equals(StudyUser.ApprovalStatus.APPROVAL)).collect(Collectors.toList());
            } else if (tab.equals("apply")) {
                studyUsers = studyUsers.stream().filter(studyUser -> !(studyUser.getApprovalStatus().equals(StudyUser.ApprovalStatus.APPROVAL))).collect(Collectors.toList());
            } else if (tab.equals("admin")) {
                studyUsers = studyUsers.stream().filter(StudyUser::isAdmin).collect(Collectors.toList());
            } else if (tab.equals("likes")) {
                studies = userService.getLoginUser().getLikes().stream().map(Like::getStudy).collect(Collectors.toList());
                if (status != null) {
                    return studies.stream().filter(study -> study.getStudyStatus().getStatus().equals(status))
                            .collect(Collectors.toList());
                }
                return studies;
            }
        }

        if (status == null) {
            return studyUsers.stream().map(StudyUser::getStudy).collect(Collectors.toList());
        }

        return studyUsers.stream().map(StudyUser::getStudy)
                .filter(study -> study.getStudyStatus().getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public void checkLikes(long studyId) {
        User user = userService.getLoginUser();
        Study study = existStudy(studyId);
        Optional<Like> optionalLike = user.getLikes().stream().filter(like -> like.getStudy().getStudyId() == studyId).findAny();

        if (optionalLike.isPresent()) {
            likeRepository.delete(optionalLike.get());
        } else {
            Like like = new Like(user, study);
            study.addLike(like);
            studyRepository.save(study);
        }
    }

    /*
    * field : 백엔드, 프론트엔드, 디자인, 기타
    * stack : 기술스택 중 택 1
    * sort : 최신순(latest), 좋아요순(like), 마감기한순(deadline), 오래된순(old), 조회수순(views)
    * area : 오프라인일 때 지역
    * status : RECRUITING("모집중"), ONGOING("진행중"), END("종료")
    * */
    public Page<Study> findStudies(int page, int size, String field, String stack, String sort, String area, Study.StudyStatus status) {
        List<Study> optionStudies = getOptionStudies(sort);
        if (field != null) {
            optionStudies = optionStudies
                    .stream()
                    .filter(study -> study.getActivity().equals(field))
                    .collect(Collectors.toList());
        }
        if (stack != null) {
            List<Study> list = new ArrayList<>();
            optionStudies.forEach(study -> study.getStudyStacks()
                    .forEach(ss -> {
                        if (ss.getStack().getStackName().equals(stack)) {
                            list.add(study);
                        }
                    }));
            optionStudies = list;
        }
        if (area != null) {
            optionStudies = optionStudies
                    .stream()
                    .filter(study -> !study.isOnline() && study.getPlace().contains(area))
                    .collect(Collectors.toList());
        }
        if (status != null) {
            optionStudies = optionStudies
                    .stream()
                    .filter(study -> study.getStudyStatus().equals(status))
                    .collect(Collectors.toList());
        }

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), optionStudies.size());
        return new PageImpl<>(optionStudies.subList(start, end), pageable, optionStudies.size());
    }

    //sort 없을 경우 최신순
    private List<Study> getOptionStudies(String sort) {
        List<Study> studies = studyRepository.findAll();
        if (sort == null || sort.equals("latest")) {
            studies.sort(Collections.reverseOrder(Comparator.comparing(Study::getStudyId)));
        } else if (sort.equals("like")) {
            studies.sort(Collections.reverseOrder(Comparator.comparing(study -> study.getLikes().size())));
        } else if (sort.equals("deadline")) {
            LocalDate today = LocalDate.now();
            studies = studies
                    .stream()
                    .filter(study -> today.isBefore(LocalDate.parse(study.getStartDate())) || today.isEqual(LocalDate.parse(study.getStartDate())))
                    .collect(Collectors.toList());
            studies.sort(Comparator.comparing(Study::getStartDate));
        } else if (sort.equals("old")) {
            studies.sort(Comparator.comparing(Study::getStudyId));
        } else if (sort.equals("views")) {
            studies.sort(Collections.reverseOrder(Comparator.comparing(Study::getViews)));
        }

        return studies.stream()
                .filter(study -> study.getStudyStatus().equals(Study.StudyStatus.RECRUITING)
                        || study.getStudyStatus().equals(Study.StudyStatus.ONGOING)
                        || study.getStudyStatus().equals(Study.StudyStatus.END))
                .collect(Collectors.toList());
    }

    public Study existStudy(long studyId) {
        return studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.STUDY_NOT_FOUND));
    }

    //관리자만 접근 가능
    public void accessResource(Study study) {
        User user = userService.getLoginUser();
        StudyUser studyUser = studyUserRepository.findByStudyAndUser(study, user)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.UNAUTHORIZED));

        if (!studyUser.isAdmin()) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
        }
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void changeStudyStatus() {
        log.info("시스템: 스터디 상태 업데이트 완료");
        //모집중 -> 진행중
        List<Study> recruitingStudies = studyRepository.findAllByStudyStatus(Study.StudyStatus.RECRUITING);
        LocalDate today = LocalDate.now();
        recruitingStudies.forEach(study -> {
            if (study.getStartDate().equals(today.toString())) {
                study.setStudyStatus(Study.StudyStatus.ONGOING);
                studyRepository.save(study);
            }
        });
        //진행중 -> 종료
        List<Study> proceedingStudies = studyRepository.findAllByStudyStatus(Study.StudyStatus.ONGOING);
        proceedingStudies.forEach(study -> {
            if (study.getEndDate().equals(today.toString())) {
                study.setStudyStatus(Study.StudyStatus.END);
                studyRepository.save(study);
            }
        });
    }

    //스터디 내부 (notice == null 이면 공지사항 없음)
    public void updateNotice(Study study) {
        Study findStudy = existStudy(study.getStudyId());
        accessResource(findStudy);
        if (study.getNoticeTitle() == null) {
            findStudy.setNoticeTitle(null);
            findStudy.setNoticeContent(null);
        } else {
            findStudy.setNoticeTitle(study.getNoticeTitle());
            findStudy.setNoticeContent(study.getNoticeContent());
        }
        studyRepository.save(findStudy);
    }

    public Study findNotice(long studyId) {
        Study findStudy = existStudy(studyId);
        verifiedCrew(findStudy);
        return findStudy;
    }

    public void deleteNotice(long studyId) {
        Study findStudy = existStudy(studyId);
        accessResource(findStudy);
        findStudy.setNoticeTitle(null);
        findStudy.setNoticeContent(null);
    }

    //참가 신청
    public void userRequestForStudy(long studyId) {
        Study study = existStudy(studyId);
        User user = userService.getLoginUser();
        user.getStudyUsers().forEach(studyUser -> {
            if (studyUser.getStudy() == study) {
                throw new BusinessLogicException(ExceptionCode.STUDY_REQUEST_EXISTS);
            }
        });
        StudyUser studyUser = new StudyUser();
        studyUser.setStudy(study);
        studyUser.setUser(user);
        studyUser.setApprovalStatus(StudyUser.ApprovalStatus.WAITING);
        study.addStudyUser(studyUser);
        studyRepository.save(study);
    }

    //admin - 참가신청자 조회
    //status : 승인, 대기중, 거절
    public Page<User> getStudyUser(long studyId, String status, int page, int size) {
        Study study = existStudy(studyId);
        accessResource(study);
        List<User> users = study.getStudyUsers().stream()
                .filter(studyUser -> !studyUser.isAdmin())
                .filter(studyUser -> studyUser.getApprovalStatus().getStatus().equals(status))
                .map(StudyUser::getUser).collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), users.size());
        return new PageImpl<>(users.subList(start, end), pageable, users.size());
    }

    /*
    * assign
    * 1. 승인상태가 waiting 일 경우 approval, reject
    * 2. 승인상태가 approval 일 경우 expulsion, delegation
    * */
    public void assignStudyUser(long studyId, long userId, String assign) {
        Study study = existStudy(studyId);
        accessResource(study);
        User user = userService.getUser(userId);
        StudyUser studyUser = studyUserRepository.findByStudyAndUser(study, user)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        //admin 본인은 처리 불가
        StudyUser originalAdmin = studyUserRepository.findByStudyAndUser(study, userService.getLoginUser()).orElseThrow();
        if (userId == originalAdmin.getUser().getUserId()) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }

        //스터디 참여신청 승인/거절
        if (studyUser.getApprovalStatus() == StudyUser.ApprovalStatus.WAITING
                && (assign.equals("approval") || assign.equals("reject"))) {
            if (assign.equals("approval")) {
                studyUser.setApprovalStatus(StudyUser.ApprovalStatus.APPROVAL);
            } else studyUser.setApprovalStatus(StudyUser.ApprovalStatus.REJECT);
        }
        //스터디원 추방/방장권한위임
        else if (studyUser.getApprovalStatus() == StudyUser.ApprovalStatus.APPROVAL
                && (assign.equals("expulsion") || assign.equals("delegation"))) {
            if (assign.equals("expulsion")) {
                studyUser.setApprovalStatus(StudyUser.ApprovalStatus.REJECT);
            } else {
                originalAdmin.setAdmin(false);
                studyUser.setAdmin(true);
            }
        }
        else {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }
        studyRepository.save(study);
    }

    //스터디 승인 회원만 접근 허용
    public void verifiedCrew(Study study) {
        User user = userService.getLoginUser();
        Optional<StudyUser> optionalStudyUser = studyUserRepository.findByStudyAndUser(study, user);
        StudyUser studyUser = optionalStudyUser.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN));
        if (studyUser.getApprovalStatus() != StudyUser.ApprovalStatus.APPROVAL) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }
    }
}
