package com.spammayo.spam.studycomment.service;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.entity.StudyUser;
import com.spammayo.spam.studycomment.entity.StudyComment;
import com.spammayo.spam.studycomment.repository.StudyCommentRepository;
import com.spammayo.spam.task.entity.Task;
import com.spammayo.spam.task.service.TaskService;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyCommentService {

    private final StudyCommentRepository studyCommentRepository;
    private final TaskService taskService;
    private final UserService userService;

    public StudyComment createComment(StudyComment studyComment,
                                      Long studyId,
                                      String taskDate) {

        Task task = taskService.findTask(studyId, taskDate);
        User user = userService.getLoginUser();
        Study study = task.getStudy();

        task.addStudyComment(studyComment);
        studyComment.setUser(user);
        studyComment.setTask(task);
        studyComment.setStudy(study);

        return studyCommentRepository.save(studyComment);
    }

    public StudyComment updateComment(StudyComment studyComment,
                                      Long studyCommentId) {

        StudyComment findComment = verifiedComment(studyCommentId);

        Optional.ofNullable(studyComment.getComment())
                .ifPresent(findComment::setComment);

        return studyCommentRepository.save(findComment);
    }

    public StudyComment findComment(Long studyCommentId) { return verifiedComment(studyCommentId); }

    public List<StudyComment> findComments(Long studyId, String taskDate) {

        Task task = taskService.findTask(studyId, taskDate);

        return studyCommentRepository.findByTaskAndTask_TaskDate(task, taskDate);
    }

    public void deleteComment(Long studyCommentId) {

        StudyComment findComment = verifiedComment(studyCommentId);
        Study study = findComment.getStudy();
        User user = userService.getLoginUser();
        User admin = study.getStudyUsers().stream().filter(StudyUser::isAdmin).findFirst().orElseThrow().getUser();

        // 댓글 작성자 / 스터디장이 아닐 경우
        if (user != findComment.getUser() && user != admin) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }

        studyCommentRepository.delete(findComment);
    }

    // 댓글 존재 여부
    private StudyComment verifiedComment(Long studyCommentId) {

        return studyCommentRepository.findById(studyCommentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
    }
}
