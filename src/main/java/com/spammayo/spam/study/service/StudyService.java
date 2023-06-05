package com.spammayo.spam.study.service;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.likes.Like;
import com.spammayo.spam.likes.LikeRepository;
import com.spammayo.spam.offer.repository.OfferRepository;
import com.spammayo.spam.status.ApprovalStatus;
import com.spammayo.spam.status.Area;
import com.spammayo.spam.status.Field;
import com.spammayo.spam.status.StudyStatus;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.entity.StudyUser;
import com.spammayo.spam.study.repository.StudyRepository;
import com.spammayo.spam.study.repository.StudyStackRepository;
import com.spammayo.spam.study.repository.StudyUserRepository;
import com.spammayo.spam.task.repository.TaskRepository;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.repository.UserRepository;
import com.spammayo.spam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final StudyStackRepository studyStackRepository;

    public Study createStudy(Study study) {
        StudyUser studyUser = new StudyUser();
        studyUser.setUser(userService.getLoginUser());
        studyUser.setAdmin(true);
        studyUser.setApprovalStatus(ApprovalStatus.APPROVAL);
        study.addStudyUser(studyUser);

        LocalDate startDate = LocalDate.parse(study.getStartDate());
        LocalDate endDate = LocalDate.parse(study.getEndDate());
        LocalDate today = LocalDate.now();

        checkCorrectDates(startDate, endDate);

        if (endDate.isBefore(today) || startDate.isBefore(today)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_DATE);
        } else if (startDate.isEqual(today)) {
            study.setStudyStatus(StudyStatus.ONGOING);
        }

        return studyRepository.save(study);
    }

    private void checkCorrectDates(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_DATE);
        }
    }

    /*
    ** 스터디 기간 수정 **
    * 모집전/모집중 - 시작일, 종료일 모두 허용 (하지만 스터디 생성일 이전일 수 없음)
    * 진행중 - 종료일만 허용
    * 종료/폐쇄 - 스터디 수정 불가
    * */
    public Study updateStudy(Study study) {
        Study findStudy = existStudy(study.getStudyId());
        //관리자만 접근 허용
        allowedResourceForAdmin(findStudy);
        forbiddenStudy(findStudy, StudyStatus.CLOSED, StudyStatus.END);
        StudyStatus studyStatus = study.getStudyStatus();

        String originalStartDate = findStudy.getStartDate();
        String originalEndDate = findStudy.getEndDate();
        String changedStartDate = study.getStartDate();
        String changedEndDate = study.getEndDate();

        if (!originalStartDate.equals(changedStartDate) || !originalEndDate.equals(changedEndDate)) {
            LocalDate today = LocalDate.now();
            LocalDate startDate = LocalDate.parse(changedStartDate);
            LocalDate endDate = LocalDate.parse(changedEndDate);
            checkCorrectDates(startDate, endDate);

            //공통 - 시작일은 생성일 이전일 수 없음
            if (LocalDate.parse(findStudy.getCreatedAt().toLocalDate().toString()).isAfter(startDate)) {
                throw new BusinessLogicException(ExceptionCode.INVALID_DATE);
            }

            //시작일 종료일 모두 수정하는 경우 (모집전, 모집중)
            if (!originalStartDate.equals(changedStartDate) && !originalEndDate.equals(changedEndDate)) {

                if (studyStatus == StudyStatus.ONGOING) {
                    throw new BusinessLogicException(ExceptionCode.INVALID_DATE);
                }
                findStudy.setStartDate(study.getStartDate());
                findStudy.setEndDate(study.getEndDate());

                if (startDate.isBefore(today)) {
                    findStudy.setStudyStatus(StudyStatus.ONGOING);
                }
                if (endDate.plusDays(1).isBefore(today)) {
                    findStudy.setStudyStatus(StudyStatus.END);
                }
            }
            //시작일만 수정하는 경우 (모집전, 모집중)
            else if (!study.getStartDate().equals(originalStartDate)) {
                if (studyStatus == StudyStatus.ONGOING) {
                    throw new BusinessLogicException(ExceptionCode.INVALID_DATE);
                }
                findStudy.setStartDate(study.getStartDate());
                if (startDate.isBefore(today)) {
                    findStudy.setStudyStatus(StudyStatus.ONGOING);
                }
            }
            //종료일만 수정하는 경우(모집전, 모집중, 진행중)
            else if (!study.getEndDate().equals(changedEndDate)) {
                findStudy.setEndDate(study.getEndDate());

                if (endDate.plusDays(1).isBefore(today)) {
                    findStudy.setStudyStatus(StudyStatus.END);
                }
            }
        }

        Optional.ofNullable(study.getStudyName())
                .ifPresent(findStudy::setStudyName);
        Optional.ofNullable(study.getPersonnel())
                .ifPresent(findStudy::setPersonnel);
        Optional.ofNullable(study.getOnline())
                .ifPresent(findStudy::setOnline);
        Optional.ofNullable(study.getPlace())
                .ifPresent(findStudy::setPlace);
        Optional.ofNullable(study.getLatitude())
                .ifPresent(findStudy::setLatitude);
        Optional.ofNullable(study.getLongitude())
                .ifPresent(findStudy::setLongitude);
        Optional.ofNullable(study.getActivity())
                .ifPresent(findStudy::setActivity);
        Optional.ofNullable(study.getPeriod())
                .ifPresent(findStudy::setPeriod);
        Optional.ofNullable(study.getStudyStacks())
                .ifPresent(stacks -> {
                    studyStackRepository.deleteAll(findStudy.getStudyStacks());
                    findStudy.setStudyStacks(stacks);
                });

        return studyRepository.save(findStudy);
    }

    public Study findStudy(long studyId) {
        Study findStudy = existStudy(studyId);
        forbiddenStudy(findStudy, StudyStatus.CLOSED);

        //모집전인 상태일 경우 작성자만 조회 가능
        //진행중이지만 구인글 없을 경우 작성자만 조회 가능
        StudyStatus studyStatus = findStudy.getStudyStatus();
        if (studyStatus.equals(StudyStatus.BEFORE_RECRUITMENT) || (studyStatus.equals(StudyStatus.ONGOING) && findStudy.getOffer() == null)) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN));
            studyUserRepository.findByStudyAndUser(findStudy, user)
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN));
        }

        return findStudy;
    }

    /*
    * 스터디 폐쇄시
    * 1. 상태값 : CLOSED
    * 2. 승인 유저 추방 후 폐쇄 가능
    * 3. 좋아요, 참여 신청, 구인글 CRUD, 한개의 스터디 조회/수정 불가
    * 4. 오직 마이페이지에서만 조회 가능 (?)
    * */
    public void deleteStudy(long studyId) {
        Study study = existStudy(studyId);
        allowedResourceForAdmin(study);

        long member = study.getStudyUsers()
                .stream()
                .filter(studyUser -> studyUser.getApprovalStatus() == ApprovalStatus.APPROVAL)
                .count();

        if (member > 1) {
            throw new BusinessLogicException(ExceptionCode.STUDY_MEMBER_EXISTS);
        }

        //구인글/할일 존재할 경우 삭제
        if (study.getOffer() != null) {
            offerRepository.delete(study.getOffer());
        }
        if (!study.getTasks().isEmpty()) {
            taskRepository.deleteByStudy(study);
        }

        study.setStudyStatus(StudyStatus.CLOSED);
        studyRepository.save(study);
    }

    //tab : 참여중(crew), 생성(admin), 관심(likes) + 신청한(apply)
    //status : 전체, 진행중, 모집중, 모집전, 완료, 폐쇄
    public Page<Study> getUserStudy(String tab, String status, int page, int size, String approvalStatus) {
        List<StudyUser> studyUsers = userService.getLoginUser().getStudyUsers();
        List<Study> studies = new ArrayList<>();
        if (approvalStatus != null) {
            studyUsers = studyUsers.stream()
                    .filter(studyUser -> studyUser.getApprovalStatus().equals(ApprovalStatus.valueOf(approvalStatus.toUpperCase())))
                    .collect(Collectors.toList());
        }
        if (tab != null) {
            if (tab.equals("crew")) {
                studyUsers = studyUsers.stream().filter(studyUser -> studyUser.getApprovalStatus().equals(ApprovalStatus.APPROVAL)).collect(Collectors.toList());
            } else if (tab.equals("apply")) {
                studyUsers = studyUsers.stream().filter(studyUser -> !studyUser.isAdmin()).collect(Collectors.toList());
            } else if (tab.equals("admin")) {
                studyUsers = studyUsers.stream().filter(StudyUser::isAdmin).collect(Collectors.toList());
            } else if (tab.equals("likes")) {
                studies = userService.getLoginUser().getLikes().stream().map(Like::getStudy).collect(Collectors.toList());
                if (status != null) {
                    studies = studies.stream().filter(study -> study.getStudyStatus().equals(StudyStatus.toStudyStatus(status)))
                            .collect(Collectors.toList());
                }
            }
        }

        if (tab == null || !tab.equals("likes")) {
            if (status == null) {
                studies = studyUsers.stream().map(StudyUser::getStudy).collect(Collectors.toList());
            } else {
                studies = studyUsers.stream().map(StudyUser::getStudy)
                        .filter(study -> study.getStudyStatus().equals(StudyStatus.toStudyStatus(status)))
                        .collect(Collectors.toList());
            }
        }

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), studies.size());
        return new PageImpl<>(studies.subList(start, end), pageable, studies.size());
    }

    public void checkLikes(long studyId) {
        User user = userService.getLoginUser();
        Study study = existStudy(studyId);
        forbiddenStudy(study, StudyStatus.BEFORE_RECRUITMENT, StudyStatus.CLOSED);
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
    * field : backend, frontend, design, plan, other
    * stack : 기술스택 중 택 1
    * sort : 최신순(latest), 좋아요순(like), 마감기한순(deadline), 오래된순(old), 조회수순(views)
    * area : online, seoul, busan, keongkido, keonsangdo, jeonrado, kangwondo, jeju
    * status : 모집중(recruiting), 진행중(ongoing), 종료(end)
    * */
    public Page<Study> findStudies(int page, int size, List<String> field, String stack, String sort, String area, String status, String search) {

        List<Study> studies = search == null ? studyRepository.findAll() : studyRepository.findAllByTitleContaining(search);

        if (field != null) {
            List<Study> finalStudies = studies;
            List<Study> list = new ArrayList<>();


            field.forEach(studyField -> finalStudies.forEach(study -> {
                if (study.getActivity().contains(Field.toField(studyField))) {
                    list.add(study);
                }
            }));
            studies = list.stream().distinct().collect(Collectors.toList());
        }
        if (stack != null) {
            List<Study> list = new ArrayList<>();
            studies.forEach(study -> study.getStudyStacks()
                    .forEach(ss -> {
                        if (ss.getStack().getStackName().equals(stack)) {
                            list.add(study);
                        }
                    }));
            studies = list;
        }
        if (area != null) {
            Area findArea = Area.toArea(area);
            studies = studies.stream()
                    .filter(study -> {
                        if (!findArea.equals(Area.ONLINE)) {
                            return !study.getOnline() && study.getPlace().contains(findArea.getArea());
                        } else {
                            return study.getOnline();
                        }
                    })
                    .collect(Collectors.toList());
        }
        if (status != null) {
            studies = studies.stream()
                    .filter(study -> study.getStudyStatus().equals(StudyStatus.toStudyStatus(status)))
                    .collect(Collectors.toList());
        }

        studies = getOptionStudies(studies, sort);

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), studies.size());
        return new PageImpl<>(studies.subList(start, end), pageable, studies.size());
    }

    //sort 없을 경우 최신순
    private List<Study> getOptionStudies(List<Study> studies, String sort) {
        if (sort == null || sort.equals("latest")) {
            studies.sort(Collections.reverseOrder(Comparator.comparing(Study::getStudyId)));
        } else if (sort.equals("likes")) {
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
                .filter(study -> (study.getStudyStatus().equals(StudyStatus.RECRUITING)
                        || study.getStudyStatus().equals(StudyStatus.ONGOING)
                        || study.getStudyStatus().equals(StudyStatus.END))
                        && study.getOffer() != null)
                .collect(Collectors.toList());
    }

    public Study existStudy(long studyId) {
        return studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.STUDY_NOT_FOUND));
    }

    //관리자만 접근 가능
    public void allowedResourceForAdmin(Study study) {
        StudyUser studyUser = studyUserRepository.findByStudyAndUser(study, userService.getLoginUser())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.UNAUTHORIZED));

        if (!studyUser.isAdmin()) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
        }
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void changeStudyStatus() {
        log.info("시스템: 스터디 상태 업데이트 완료");
        //모집중 -> 진행중
        List<Study> recruitingStudies = studyRepository.findAllByStudyStatus(StudyStatus.RECRUITING);
        LocalDate today = LocalDate.now();
        recruitingStudies.forEach(study -> {
            if (study.getStartDate().equals(today.toString())) {
                study.setStudyStatus(StudyStatus.ONGOING);
                studyRepository.save(study);
            }
        });
        //진행중 -> 종료
        List<Study> proceedingStudies = studyRepository.findAllByStudyStatus(StudyStatus.ONGOING);
        proceedingStudies.forEach(study -> {
            LocalDate endDate = LocalDate.parse(study.getEndDate()).plusDays(1);
            if (endDate.isEqual(today)) {
                study.setStudyStatus(StudyStatus.END);
                studyRepository.save(study);
            }
        });
    }

    //스터디 내부 (notice == null 이면 공지사항 없음)
    public void updateNotice(Study study) {
        Study findStudy = existStudy(study.getStudyId());
        forbiddenStudy(findStudy, StudyStatus.CLOSED);
        allowedResourceForAdmin(findStudy);
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
        forbiddenStudy(findStudy, StudyStatus.CLOSED);
        verifiedCrew(findStudy);
        return findStudy;
    }

    public void deleteNotice(long studyId) {
        Study findStudy = existStudy(studyId);
        forbiddenStudy(findStudy, StudyStatus.CLOSED);
        allowedResourceForAdmin(findStudy);
        findStudy.setNoticeTitle(null);
        findStudy.setNoticeContent(null);
    }

    //참가 신청
    public void userRequestForStudy(long studyId) {
        Study study = existStudy(studyId);
        forbiddenStudy(study, StudyStatus.BEFORE_RECRUITMENT, StudyStatus.END, StudyStatus.CLOSED);

        User user = userService.getLoginUser();
        user.getStudyUsers().forEach(studyUser -> {
            if (studyUser.getStudy() == study && studyUser.getApprovalStatus() != ApprovalStatus.REJECT) {
                throw new BusinessLogicException(ExceptionCode.STUDY_REQUEST_EXISTS);
            }
        });
        StudyUser studyUser = new StudyUser();
        studyUser.setStudy(study);
        studyUser.setUser(user);
        studyUser.setApprovalStatus(ApprovalStatus.WAITING);
        study.addStudyUser(studyUser);
        studyRepository.save(study);
    }

    //신청 취소
    public void userCancelForStudy(long studyId) {
        Study study = existStudy(studyId);

        //완료, 폐쇄, 모집전 상태면 불가
        forbiddenStudy(study, StudyStatus.BEFORE_RECRUITMENT, StudyStatus.CLOSED, StudyStatus.END);

        StudyUser studyUser = studyUserRepository.findByStudyAndUser(study, userService.getLoginUser())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN));

        //관리자는 취소 요청 불가
        if (studyUser.isAdmin()) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }

        studyUserRepository.delete(studyUser);
    }


    //admin - 참가신청자 조회
    //status : approval, waiting, reject
    public Page<User> getStudyUser(long studyId, String status, int page, int size) {
        Study study = existStudy(studyId);
        allowedResourceForAdmin(study);
        List<User> users = study.getStudyUsers().stream()
                .filter(studyUser -> {
                    if (status == null) {
                        return !studyUser.isAdmin();
                    } else {
                        return !studyUser.isAdmin() && studyUser.getApprovalStatus().equals(ApprovalStatus.toApprovalStatus(status));
                    }
                })
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

        //접근 권한 확인
        allowedResourceForAdmin(study);
        forbiddenStudy(study, StudyStatus.BEFORE_RECRUITMENT, StudyStatus.END, StudyStatus.CLOSED);

        User user = userService.getUser(userId);
        StudyUser studyUser = studyUserRepository.findByStudyAndUser(study, user)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        //admin 본인은 처리 불가
        StudyUser originalAdmin = studyUserRepository.findByStudyAndUser(study, userService.getLoginUser()).orElseThrow();
        if (userId == originalAdmin.getUser().getUserId()) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }

        //스터디 참여신청 승인/거절
        if (studyUser.getApprovalStatus() == ApprovalStatus.WAITING
                && (assign.equals("approval") || assign.equals("reject"))) {
            if (assign.equals("approval")) {
                studyUser.setApprovalStatus(ApprovalStatus.APPROVAL);
            } else studyUser.setApprovalStatus(ApprovalStatus.REJECT);
        }
        //스터디원 추방/방장권한위임
        else if (studyUser.getApprovalStatus() == ApprovalStatus.APPROVAL
                && (assign.equals("expulsion") || assign.equals("delegation"))) {
            if (assign.equals("expulsion")) {
                studyUser.setApprovalStatus(ApprovalStatus.REJECT);
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
        if (studyUser.getApprovalStatus() != ApprovalStatus.APPROVAL) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }
    }

    public void forbiddenStudy(Study study, StudyStatus ... status) {
        for (StudyStatus studyStatus : status) {
            if (study.getStudyStatus() == studyStatus) {
                throw new BusinessLogicException(ExceptionCode.INVALID_STUDY_STATUS);
            }
        }
    }

}
