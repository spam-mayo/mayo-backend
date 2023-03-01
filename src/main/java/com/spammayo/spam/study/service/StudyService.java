package com.spammayo.spam.study.service;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.entity.StudyUser;
import com.spammayo.spam.study.repository.StudyRepository;
import com.spammayo.spam.study.repository.StudyUserRepository;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.repository.UserRepository;
import com.spammayo.spam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyUserRepository studyUserRepository;
    private final UserService userService;
    //TODO: 추후 제거예
    private final UserRepository userRepository;

    public Study createStudy(Study study) {
        //TODO:추후 주석 해제예
        User user = userRepository.findById(1L).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
//        User user = userService.getLoginUser();
        StudyUser studyUser = new StudyUser();
        studyUser.setUser(user);
        studyUser.setAdmin(true);
        studyUser.setApproval(true);
        study.addStudyUser(studyUser);
        return studyRepository.save(study);
    }

//    public Study updateStudy(Study study) {
//        Study findStudy = existStudy(study.getStudyId());
//        accessResource(findStudy);
//        findStudy.set
//    }

    public Study findStudy(long studyId) {
        return existStudy(studyId);
    }

    public void deleteStudy(long studyId) {
        Study study = existStudy(studyId);
        //TODO : 추후 주석 해제예
//        accessResource(study);
        studyRepository.delete(study);
    }

    private Study existStudy(long studyId) {
        return studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.STUDY_NOT_FOUND));
    }

    //관리자만 접근 가능
    private void accessResource(Study study) {
        User user = userService.getLoginUser();
        StudyUser studyUser = studyUserRepository.findByStudyAndUser(study, user)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.UNAUTHORIZED));

        if (!studyUser.isAdmin()) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
        }
    }
}
