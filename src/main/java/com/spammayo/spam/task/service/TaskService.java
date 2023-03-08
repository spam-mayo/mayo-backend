package com.spammayo.spam.task.service;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.entity.StudyUser;
import com.spammayo.spam.study.service.StudyService;
import com.spammayo.spam.task.entity.Task;
import com.spammayo.spam.task.repository.TaskRepository;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final StudyService studyService;
    private final UserService userService;

    public Task createTask(Task task, long studyId) {
        Study study = studyService.existStudy(studyId);
        accessTask(study);
        verifiedTask(task.getTaskDate(), studyId);
        task.setStudy(study);
        return taskRepository.save(task);
    }

    public Task updateTask(Task task) {
        Task findTask = existTask(task.getTaskId());
        accessTask(findTask.getStudy());
        findTask.setTask(task.getTask());
        return taskRepository.save(findTask);
    }

    public void deleteTask(long taskId) {
        Task task = existTask(taskId);
        accessTask(task.getStudy());
        taskRepository.delete(task);
    }

    public Task findTask(long studyId, String taskDate) {
        Study study = studyService.existStudy(studyId);
        accessTask(study);
        return taskRepository.findByStudyAndTaskDate(study, taskDate)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN));
    }

    //스터디 승인 회원만 접근 허용
    private void accessTask(Study study) {
        User user = userService.getLoginUser();
        Optional<StudyUser> optionalStudyUser = user.getStudyUsers().stream().filter(studyUser -> studyUser.getStudy() == study).findFirst();
        StudyUser studyUser = optionalStudyUser.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN));
        if (studyUser.getApprovalStatus() != StudyUser.ApprovalStatus.APPROVAL) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }
    }

    private void verifiedTask(String taskDate, long studyId) {
        Study study = studyService.existStudy(studyId);
        Optional<Task> task = taskRepository.findByStudyAndTaskDate(study, taskDate);
        if (task.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.TASK_EXISTS);
        }
    }

    private Task existTask(long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.TASK_NOT_FOUND));
    }

}
