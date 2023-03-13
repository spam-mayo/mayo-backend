package com.spammayo.spam.offercomment.controller;

import com.spammayo.spam.offercomment.dto.OfferCommentDto;
import com.spammayo.spam.offercomment.entity.OfferComment;
import com.spammayo.spam.offercomment.mapper.OfferCommentMapper;
import com.spammayo.spam.offercomment.service.OfferCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Validated
@RestController
@RequestMapping("/api/offer-comment")
@RequiredArgsConstructor
public class OfferCommentController {

    private final OfferCommentService offerCommentService;
    private final OfferCommentMapper offerCommentMapper;

    @PostMapping("/offer/{offerId}")
    public ResponseEntity postComment(@PathVariable("offerId") @Positive Long offerId,
                                      @RequestBody @Valid OfferCommentDto.PostDto requestBody) {

        OfferComment offerComment = offerCommentService.createComment(
                offerCommentMapper.commentPostDtoToComment(requestBody), offerId);

        return new ResponseEntity<>(offerCommentMapper.commentToCommentResponseDto(offerComment), HttpStatus.CREATED);
    }

    @PatchMapping("/{offerCommentId}")
    public ResponseEntity patchComment(@PathVariable("offerCommentId") @Positive Long offerCommentId,
                                       @RequestBody @Valid OfferCommentDto.PatchDto requestBody) {

        requestBody.setOfferCommentId(offerCommentId);

        OfferComment offerComment = offerCommentService.updateComment(
                offerCommentMapper.commentPatchDtoToComment(requestBody),
                offerCommentId);

        offerComment.setOfferCommentId(offerCommentId);

        return new ResponseEntity<>(offerCommentMapper.commentToCommentResponseDto(offerComment), HttpStatus.OK);
    }

    @GetMapping("/{offerCommentId}")
    public ResponseEntity getComment(@PathVariable("offerCommentId") @Positive Long offerCommentId) {
        OfferComment offerComment = offerCommentService.findComment(offerCommentId);

        return new ResponseEntity<>(offerCommentMapper.commentToCommentResponseDto(offerComment), HttpStatus.OK);
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

    @DeleteMapping("/{offerCommentId}")
    public ResponseEntity deleteComment(@PathVariable("offerCommentId") @Positive Long offerCommentId) {

        offerCommentService.deleteComment(offerCommentId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
