package com.spammayo.spam.studytodo.controller;

import com.spammayo.spam.studytodo.dto.StudyTodoDto;
import com.spammayo.spam.studytodo.entity.StudyTodo;
import com.spammayo.spam.studytodo.mapper.StudyTodoMapper;
import com.spammayo.spam.studytodo.service.StudyTodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
@Validated
public class StudyTodoController {

    private final StudyTodoService studyTodoService;
    private final StudyTodoMapper mapper;

    @PostMapping("/study/{study-id}")
    public ResponseEntity postStudyTodo(@PathVariable("study-id") @Positive long studyId,
                                        @RequestBody @Valid StudyTodoDto.PostDto postDto) {
        StudyTodo studyTodo = studyTodoService.createStudyTodo(mapper.postDtoToStudyTodo(postDto), studyId);
        return new ResponseEntity<>(mapper.studyTodoToSimpleResponseDto(studyTodo), HttpStatus.CREATED);
    }

    @PatchMapping("/{todo-id}")
    public ResponseEntity patchStudyTodo(@PathVariable("todo-id") long todoId,
                                         @RequestBody @Valid StudyTodoDto.PatchDto patchDto) {
        patchDto.setStudyTodoId(todoId);
        StudyTodo studyTodo = studyTodoService.updateStudyTodo(mapper.patchDtoToStudyTodo(patchDto));
        return new ResponseEntity<>(mapper.studyTodoToSimpleResponseDto(studyTodo), HttpStatus.OK);
    }

    @GetMapping("/study/{study-id}")
    public ResponseEntity getStudyTodo(@PathVariable("study-id") @Positive long studyId,
                                       @RequestParam @NotBlank String todoDate) {
        StudyTodo studyTodo = studyTodoService.findStudyTodo(studyId, todoDate);
        return new ResponseEntity<>(mapper.studyTodoToResponseDto(studyTodo), HttpStatus.OK);
    }

    @DeleteMapping("/{todo-id}")
    public ResponseEntity deleteStudyTodo(@PathVariable("todo-id") @Positive long todoId) {
        studyTodoService.deleteStudyTodo(todoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
