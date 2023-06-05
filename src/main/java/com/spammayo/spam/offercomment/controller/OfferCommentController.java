package com.spammayo.spam.offercomment.controller;

import com.spammayo.spam.offer.entity.Offer;
import com.spammayo.spam.offer.service.OfferService;
import com.spammayo.spam.offercomment.dto.OfferCommentDto;
import com.spammayo.spam.offercomment.entity.OfferComment;
import com.spammayo.spam.offercomment.mapper.OfferCommentMapper;
import com.spammayo.spam.offercomment.repository.OfferCommentRepository;
import com.spammayo.spam.offercomment.service.OfferCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/offer-comment")
@RequiredArgsConstructor
public class OfferCommentController {

    private final OfferService offerService;
    private final OfferCommentService offerCommentService;
    private final OfferCommentMapper offerCommentMapper;
    private final OfferCommentRepository offerCommentRepository;

    @PostMapping("/study/{studyId}")
    public ResponseEntity postComment(@PathVariable("studyId") @Positive Long studyId,
                                      @RequestBody @Valid OfferCommentDto.PostDto requestBody) {

        OfferComment offerComment = offerCommentService.createComment(
                offerCommentMapper.commentPostDtoToComment(requestBody), studyId);

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

    @GetMapping("/study/{studyId}")
    public ResponseEntity getComments(@PathVariable("studyId") @Positive Long studyId) {

        Offer offer = offerService.findOffer(studyId);

        List<OfferComment> offerComment = offerCommentRepository.findByOffer(offer);

        return offerCommentService.findAll(offerComment, offer);
    }

    @DeleteMapping("/{offerCommentId}")
    public ResponseEntity deleteComment(@PathVariable("offerCommentId") @Positive Long offerCommentId) {

        offerCommentService.deleteComment(offerCommentId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
