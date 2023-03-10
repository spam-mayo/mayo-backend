package com.spammayo.spam.task.controller;

import com.spammayo.spam.task.dto.TaskDto;
import com.spammayo.spam.task.entity.Task;
import com.spammayo.spam.task.mapper.TaskMapper;
import com.spammayo.spam.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper mapper;

    @PostMapping("/study/{study-id}")
    public ResponseEntity postTask(@PathVariable("study-id") @Positive long studyId,
                                        @RequestBody @Valid TaskDto.PostDto postDto) {
        Task task = taskService.createTask(mapper.postDtoToTask(postDto), studyId);
        return new ResponseEntity<>(mapper.taskToSimpleResponseDto(task), HttpStatus.CREATED);
    }

    @PatchMapping("/{task-id}")
    public ResponseEntity patchTask(@PathVariable("task-id") long taskId,
                                    @RequestBody @Valid TaskDto.PatchDto patchDto) {
        patchDto.setTaskId(taskId);
        Task task = taskService.updateTask(mapper.patchDtoToTask(patchDto));
        return new ResponseEntity<>(mapper.taskToSimpleResponseDto(task), HttpStatus.OK);
    }

    @GetMapping("/study/{study-id}")
    public ResponseEntity getTask(@PathVariable("study-id") @Positive long studyId,
                                       @RequestParam @NotBlank String taskDate) {
        Task task = taskService.findTask(studyId, taskDate);
        return new ResponseEntity<>(mapper.taskToResponseDto(task), HttpStatus.OK);
    }

    @DeleteMapping("/{task-id}")
    public ResponseEntity deleteTask(@PathVariable("task-id") @Positive long taskId) {
        taskService.deleteTask(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
