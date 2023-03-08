package com.spammayo.spam.offer.controller;

import com.spammayo.spam.offer.dto.OfferDto;
import com.spammayo.spam.offer.entity.Offer;
import com.spammayo.spam.offer.mapper.OfferMapper;
import com.spammayo.spam.offer.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/offer")
@RequiredArgsConstructor
@Validated
public class OfferController {
    private final OfferService offerService;
    private final OfferMapper mapper;

    @PostMapping("/study/{study-id}")
    public ResponseEntity postOffer(@PathVariable("study-id") @Positive long studyId,
                                    @RequestBody @Valid OfferDto.PostDto postDto) {
        Offer offer = offerService.createOffer(mapper.postDtoToOffer(postDto), studyId);
        return new ResponseEntity<>(mapper.offerToSimpleResponseDto(offer), HttpStatus.CREATED);
    }

    @PatchMapping("/{offer-id}")
    public ResponseEntity patchOffer(@PathVariable("offer-id") @Positive long offerId,
                                     @RequestBody @Valid OfferDto.PatchDto patchDto) {
        patchDto.setOfferId(offerId);
        Offer offer = offerService.updateOffer(mapper.patchDtoToOffer(patchDto));
        return new ResponseEntity<>(mapper.offerToSimpleResponseDto(offer), HttpStatus.OK);
    }

    @GetMapping("/study/{study-id}")
    public ResponseEntity getOffer(@PathVariable("study-id") @Positive long studyId) {
        Offer offer = offerService.findOffer(studyId);
        return new ResponseEntity<>(mapper.offerToResponseDto(offer), HttpStatus.OK);
    }

    @DeleteMapping("/{offer-id}")
    public ResponseEntity deleteOffer(@PathVariable("offer-id") @Positive long offerId) {
        offerService.deleteOffer(offerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
