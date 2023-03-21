package com.spammayo.spam.offer.service;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.offer.entity.Offer;
import com.spammayo.spam.offer.repository.OfferRepository;
import com.spammayo.spam.status.StudyStatus;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.repository.StudyRepository;
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
    private final StudyRepository studyRepository;

    public Offer createOffer(Offer offer, long studyId) {
        Study study = studyService.findStudy(studyId);
        StudyStatus studyStatus = study.getStudyStatus();

        //구인글은 1개만 허용
        if (study.getOffer() != null) {
            throw new BusinessLogicException(ExceptionCode.OFFER_EXISTS);
        }
        studyService.accessResource(study);

        //종료, 폐쇄된 스터디는 구인글 작성 불가
        if (studyStatus == StudyStatus.CLOSED || studyStatus == StudyStatus.END) {
            throw new BusinessLogicException(ExceptionCode.INVALID_STUDY_STATUS);
        }

        //상태 - 진행중이 아니면 모집중으로 설정
        if (studyStatus != StudyStatus.ONGOING) {
            study.setStudyStatus(StudyStatus.RECRUITING);
        }

        offer.setStudy(study);
        return offerRepository.save(offer);
    }

    public Offer updateOffer(Offer offer) {
        Offer findOffer = existOffer(offer.getOfferId());
        Study findStudy = findOffer.getStudy();
        studyService.accessResource(findStudy);
        studyService.checkRecruitingAndOngoingStudy(findStudy);

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
        Study findStudy = offer.getStudy();
        studyService.checkRecruitingAndOngoingStudy(findStudy);

        studyService.accessResource(findStudy);
        findStudy.setStudyStatus(StudyStatus.BEFORE_RECRUITMENT);
        studyRepository.save(findStudy);
        offerRepository.delete(offer);
    }

    private Offer existOffer(long offerId) {
        return offerRepository.findById(offerId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.OFFER_NOT_FOUND));
    }

}
