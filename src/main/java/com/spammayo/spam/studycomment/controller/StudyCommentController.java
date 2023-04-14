package com.spammayo.spam.studycomment.controller;

import com.spammayo.spam.studycomment.dto.StudyCommentDto;
import com.spammayo.spam.studycomment.entity.StudyComment;
import com.spammayo.spam.studycomment.mapper.StudyCommentMapper;
import com.spammayo.spam.studycomment.service.StudyCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/study-comment")
@RequiredArgsConstructor
public class StudyCommentController {

    private final StudyCommentService studyCommentService;
    private final StudyCommentMapper studyCommentMapper;

    @PostMapping("/task/{taskId}")
    public ResponseEntity postComment(@PathVariable("taskId") @Positive Long taskId,
                                      @RequestBody @Valid StudyCommentDto.PostDto requestBody) {

        StudyComment studyComment = studyCommentService.createComment(
                studyCommentMapper.postDtoToStudyComment(requestBody), taskId, requestBody.getTodoDate());

        return new ResponseEntity<>(studyCommentMapper.studyCommentToStudyCommentResponseDto(studyComment), HttpStatus.CREATED);
    }

    @PatchMapping("/{studyCommentId}")
    public ResponseEntity patchComment(@PathVariable("studyCommentId") @Positive Long studyCommentId,
                                       @RequestBody @Valid StudyCommentDto.PatchDto requestBody) {

        requestBody.setStudyCommentId(studyCommentId);

        StudyComment studyComment = studyCommentService.updateComment(
                studyCommentMapper.patchDtoToStudyComment(requestBody),
                requestBody.getTaskId());

        studyComment.setStudyCommentId(studyCommentId);

        return new ResponseEntity<>(studyCommentMapper.studyCommentToStudyCommentResponseDto(studyComment), HttpStatus.OK);
    }

    @GetMapping("/{studyCommentId}")
    public ResponseEntity getComment(@PathVariable("studyCommentId") @Positive Long studyCommentId) {

        StudyComment studyComment = studyCommentService.findComment(studyCommentId);

        return new ResponseEntity<>(studyCommentMapper.studyCommentToStudyCommentResponseDto(studyComment), HttpStatus.OK);
    }

    @GetMapping("/study/{studyId}")
    public ResponseEntity getComments(@PathVariable("studyId") @Positive Long studyId,
                                      @RequestParam @NotBlank String taskDate) {

        List<StudyComment> studyComments = studyCommentService.findComments(studyId, taskDate);

        return new ResponseEntity<>(studyCommentMapper.studyCommentsToStudyCommentResponseDto(studyComments), HttpStatus.OK);
    }

    @DeleteMapping("/{studyCommentId}")
    public ResponseEntity deleteComment(@PathVariable("studyCommentId") @Positive Long studyCommentId) {

        studyCommentService.deleteComment(studyCommentId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
