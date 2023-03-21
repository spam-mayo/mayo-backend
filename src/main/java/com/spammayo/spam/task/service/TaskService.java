package com.spammayo.spam.task.service;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.service.StudyService;
import com.spammayo.spam.task.entity.Task;
import com.spammayo.spam.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final StudyService studyService;

    public Task createTask(Task task, long studyId) {
        Study study = studyService.existStudy(studyId);
        studyService.verifiedCrew(study);

        LocalDate start = LocalDate.parse(study.getStartDate());
        LocalDate end = LocalDate.parse(study.getEndDate());
        LocalDate taskDate = LocalDate.parse(task.getTaskDate());

        if (taskDate.isBefore(start) || taskDate.isAfter(end)) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }
        verifiedTask(task.getTaskDate(), studyId);
        task.setStudy(study);
        return taskRepository.save(task);
    }

    public Task updateTask(Task task) {
        Task findTask = existTask(task.getTaskId());
        studyService.verifiedCrew(findTask.getStudy());
        findTask.setTask(task.getTask());
        return taskRepository.save(findTask);
    }

    public void deleteTask(long taskId) {
        Task task = existTask(taskId);
        studyService.verifiedCrew(task.getStudy());
        taskRepository.delete(task);
    }

    public Task findTask(long studyId, String taskDate) {
        Study study = studyService.existStudy(studyId);
        studyService.verifiedCrew(study);
        return taskRepository.findByStudyAndTaskDate(study, taskDate)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN));
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
