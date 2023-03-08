package com.spammayo.spam.offer.service;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.offer.entity.Offer;
import com.spammayo.spam.offer.repository.OfferRepository;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final StudyService studyService;

    public Offer createOffer(Offer offer, long studyId) {
        Study study = studyService.findStudy(studyId);
        if (study.getOffer() != null) {
            throw new BusinessLogicException(ExceptionCode.OFFER_EXISTS);
        }
        studyService.accessResource(study);
        offer.setStudy(study);
        return offerRepository.save(offer);
    }

    public Offer updateOffer(Offer offer) {
        Offer findOffer = existOffer(offer.getOfferId());
        studyService.accessResource(findOffer.getStudy());

        Optional.ofNullable(offer.getOfferIntro())
                .ifPresent(findOffer::setOfferIntro);
        Optional.ofNullable(offer.getOfferRule())
                .ifPresent(findOffer::setOfferRule);
        return offerRepository.save(findOffer);
    }

    @Transactional(readOnly = true)
    public Offer findOffer(long studyId) {
        Study study = studyService.findStudy(studyId);
        return existOffer(study.getOffer().getOfferId());
    }

    public void deleteOffer(long offerId) {
        Offer offer = existOffer(offerId);
        studyService.accessResource(offer.getStudy());
        offerRepository.delete(offer);
    }

    private Offer existOffer(long offerId) {
        return offerRepository.findById(offerId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.OFFER_NOT_FOUND));
    }

}
