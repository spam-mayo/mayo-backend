package com.spammayo.spam.offerreply.controller;

import com.spammayo.spam.offerreply.dto.OfferReplyDto;
import com.spammayo.spam.offerreply.entity.OfferReply;
import com.spammayo.spam.offerreply.mapper.OfferReplyMapper;
import com.spammayo.spam.offerreply.service.OfferReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/offer-reply")
@RequiredArgsConstructor
public class OfferReplyController {

    private final OfferReplyService offerReplyService;
    private final OfferReplyMapper offerReplyMapper;

    @PostMapping("/offer-comment/{offerCommentId}")
    public ResponseEntity postReply(@PathVariable("offerCommentId") @Positive Long offerCommentId,
                                    @RequestBody @Valid OfferReplyDto.PostDto requestBody) {

        OfferReply offerReply = offerReplyService.createReply(
                offerReplyMapper.replyPostDtoToOfferReply(requestBody), offerCommentId);

        return new ResponseEntity<>(offerReplyMapper.offerReplyToOfferReplyResponseDto(offerReply), HttpStatus.CREATED);
    }

    @PatchMapping("/{replyId}")
    public ResponseEntity patchReply(@PathVariable("replyId") @Positive Long replyId,
                                     @RequestBody @Valid OfferReplyDto.PatchDto requestBody) {

        requestBody.setReplyId(replyId);

        OfferReply offerReply = offerReplyService.updateReply(
                offerReplyMapper.replyPatchDtoToOfferReply(requestBody),
                replyId);

        offerReply.setReplyId(replyId);

        return new ResponseEntity<>(offerReplyMapper.offerReplyToOfferReplyResponseDto(offerReply), HttpStatus.OK);
    }

    @GetMapping("/{replyId}")
    public ResponseEntity getComment(@PathVariable("replyId") @Positive Long replyId) {

        OfferReply offerReply = offerReplyService.findReply(replyId);

        return new ResponseEntity<>(offerReplyMapper.offerReplyToOfferReplyResponseDto(offerReply), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getComments() {

        List<OfferReply> offerReplies = offerReplyService.findReplies();

        return new ResponseEntity<>(offerReplyMapper.offerRepliesToOfferReplyResponseDto(offerReplies), HttpStatus.OK);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity deleteComment(@PathVariable("replyId") @Positive Long replyId) {

        offerReplyService.deleteReply(replyId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
