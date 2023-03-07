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
import com.spammayo.spam.user.repository.UserRepository;
import com.spammayo.spam.user.service.UserService;
import lombok.RequiredArgsConstructor;
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
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyUserRepository studyUserRepository;
    private final UserService userService;
    //TODO: 추후 제거예
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public Study createStudy(Study study) {
        //TODO:추후 주석 해제예
        User user = userRepository.findById(1L).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
//        User user = userService.getLoginUser();
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
        return existStudy(studyId);
    }

    public void deleteStudy(long studyId) {
        Study study = existStudy(studyId);
        //TODO : 추후 주석 해제예
//        accessResource(study);
        studyRepository.delete(study);
    }

    //tab : 참여중(approval), 생성(admin), 관심(likes)
    //status : 전체, 진행중, 모집중, 모집전, 완료, 폐쇄
    public List<Study> getUserStudy(String tab, String status) {

        List<StudyUser> studyUsers = userService.getLoginUser().getStudyUsers();
        List<Study> studies;
        if (tab != null) {
            if (tab.equals("approval")) {
                studyUsers = studyUsers.stream().filter(studyUser -> studyUser.getApprovalStatus() == StudyUser.ApprovalStatus.APPROVAL).collect(Collectors.toList());
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
    * sort : 최신순(studyId), 좋아요순(like), 마감기한순(deadline)
    * area : 오프라인일 때 지역
    * */
    public Page<Study> findStudies(int page, int size, String field, String stack, String sort, String area) {
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
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), optionStudies.size());
        return new PageImpl<>(optionStudies.subList(start, end), pageable, optionStudies.size());
    }

    private List<Study> getOptionStudies(String sort) {
        List<Study> studies = studyRepository.findAll();
        if (sort == null || sort.equals("studyId")) {
            //최신순
            studies = studyRepository.findAll(Sort.by("studyId").descending());
        } else if (sort.equals("like")) {
            studies.sort(Collections.reverseOrder(Comparator.comparing(study -> study.getLikes().size())));
        } else if (sort.equals("deadline")) {
            LocalDate today = LocalDate.now();
            studies = studies
                    .stream()
                    .filter(study -> today.isBefore(LocalDate.parse(study.getStartDate())) || today.isEqual(LocalDate.parse(study.getStartDate())))
                    .collect(Collectors.toList());
            studies.sort(Comparator.comparing(Study::getStartDate));
        }
        return studies;
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

    @Scheduled(cron = "* * 00 * * *", zone = "Asia/Seoul")
    public void changeStudyStatus() {
        //모집중 -> 진행중
        List<Study> recruitingStudies = studyRepository.findAllByStudyStatus(Study.StudyStatus.RECRUITING);
        LocalDate today = LocalDate.now();
        recruitingStudies.forEach(study -> {
            if (study.getStartDate().equals(today.toString())) {
                study.setStudyStatus(Study.StudyStatus.PROCEEDING);
                studyRepository.save(study);
            }
        });
        //진행중 -> 종료
        List<Study> proceedingStudies = studyRepository.findAllByStudyStatus(Study.StudyStatus.PROCEEDING);
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
        if (study.getNotice().trim().isEmpty()) {
            findStudy.setNotice(null);
        } else {
            Optional.ofNullable(study.getNotice())
                    .ifPresent(findStudy::setNotice);
        }
        studyRepository.save(findStudy);
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
}
