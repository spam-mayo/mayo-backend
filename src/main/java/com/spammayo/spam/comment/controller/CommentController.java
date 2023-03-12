package com.spammayo.spam.comment.controller;

import com.spammayo.spam.comment.dto.CommentDto;
import com.spammayo.spam.comment.entity.Comment;
import com.spammayo.spam.comment.mapper.CommentMapper;
import com.spammayo.spam.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Validated
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping("/offer/{offerId}")
    public ResponseEntity postComment(@PathVariable("offerId") @Positive Long offerId,
                                      @RequestBody @Valid CommentDto.PostDto requestBody) {

        Comment comment = commentService.createComment(
                commentMapper.commentPostDtoToComment(requestBody), offerId);

        return new ResponseEntity<>(commentMapper.commentToCommentResponseDto(comment), HttpStatus.CREATED);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity patchComment(@PathVariable("commentId") @Positive Long commentId,
                                       @RequestBody @Valid CommentDto.PatchDto requestBody) {

        Comment comment = commentService.updateComment(
                commentMapper.commentPatchDtoToComment(requestBody));

        comment.setCommentId(commentId);

        return new ResponseEntity<>(commentMapper.commentToCommentResponseDto(comment), HttpStatus.OK);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity getComment(@PathVariable("commentId") @Positive Long commentId) {
        Comment comment = commentService.findComment(commentId);

        return new ResponseEntity<>(commentMapper.commentToCommentResponseDto(comment), HttpStatus.OK);
    }

/*
    @GetMapping
    public ResponseEntity getComments(@Positive @RequestParam("page") int page,
                                      @Positive @RequestParam("size") int size) {
        List<Comment> comments = pageComments.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(commentMapper.commentsToCommentResponseDtos(comments),
                        pageComments),
                HttpStatus.OK);
    }
*/

    @DeleteMapping("/{commentId}")
    public ResponseEntity deleteComment(@PathVariable("commentId") @Positive Long commentId) {

        commentService.deleteComment(commentId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
