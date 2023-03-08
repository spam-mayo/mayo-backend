package com.spammayo.spam.studytodo.service;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.entity.StudyUser;
import com.spammayo.spam.study.service.StudyService;
import com.spammayo.spam.studytodo.entity.StudyTodo;
import com.spammayo.spam.studytodo.repository.StudyTodoRepository;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyTodoService {

    private final StudyTodoRepository studyTodoRepository;
    private final StudyService studyService;
    private final UserService userService;

    public StudyTodo createStudyTodo(StudyTodo studyTodo, long studyId) {
        Study study = studyService.existStudy(studyId);
        accessStudyTodo(study);
        verifiedStudyTodo(studyTodo.getTodoDate(), studyId);
        studyTodo.setStudy(study);
        return studyTodoRepository.save(studyTodo);
    }

    public StudyTodo updateStudyTodo(StudyTodo studyTodo) {
        StudyTodo findStudyTodo = existStudyTodo(studyTodo.getStudyTodoId());
        accessStudyTodo(findStudyTodo.getStudy());
        findStudyTodo.setTodo(studyTodo.getTodo());
        return studyTodoRepository.save(findStudyTodo);
    }

    public void deleteStudyTodo(long studyTodoId) {
        StudyTodo studyTodo = existStudyTodo(studyTodoId);
        accessStudyTodo(studyTodo.getStudy());
        studyTodoRepository.delete(studyTodo);
    }

    public StudyTodo findStudyTodo(long studyId, String todoDate) {
        Study study = studyService.existStudy(studyId);
        accessStudyTodo(study);
        return studyTodoRepository.findByStudyAndTodoDate(study, todoDate)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN));
    }

    //스터디 승인 회원만 접근 허용
    private void accessStudyTodo(Study study) {
        User user = userService.getLoginUser();
        Optional<StudyUser> optionalStudyUser = user.getStudyUsers().stream().filter(studyUser -> studyUser.getStudy() == study).findFirst();
        StudyUser studyUser = optionalStudyUser.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN));
        if (studyUser.getApprovalStatus() != StudyUser.ApprovalStatus.APPROVAL) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }
    }

    private void verifiedStudyTodo(String todoDate, long studyId) {
        Study study = studyService.existStudy(studyId);
        Optional<StudyTodo> studyTodo = studyTodoRepository.findByStudyAndTodoDate(study, todoDate);
        if (studyTodo.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.TODO_EXISTS);
        }
    }

    private StudyTodo existStudyTodo(long studyTodoId) {
        return studyTodoRepository.findById(studyTodoId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.TODO_NOT_FOUND));
    }

}
