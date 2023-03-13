package com.spammayo.spam.offercomment.service;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.offer.entity.Offer;
import com.spammayo.spam.offer.service.OfferService;
import com.spammayo.spam.offercomment.entity.OfferComment;
import com.spammayo.spam.offercomment.repository.OfferCommentRepository;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OfferCommentService {
    private final OfferCommentRepository offerCommentRepository;
    private final UserService userService;
    private final OfferService offerService;

    public OfferComment createComment(OfferComment comment,
                                      Long offerId) {

        Offer offer = offerService.findOffer(offerId);
        User user = userService.getLoginUser();

        offer.addComment(comment);
        comment.setUser(user);
        comment.setOffer(offer);

        return offerCommentRepository.save(comment);
    }

    public OfferComment updateComment(OfferComment comment,
                                      Long offerCommentId) {

        OfferComment findComment = verifiedComment(offerCommentId);

        // TODO : 스터디 방장 권한 추가
        if (!findComment.getUser().getEmail().equals(userService.getLoginUser().getEmail())) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }

        Optional.ofNullable(comment.getComment())
                .ifPresent(findComment::setComment);

        Optional.ofNullable(comment.getSecret())
                .ifPresent(findComment::setSecret);

        return offerCommentRepository.save(findComment);
    }

    public OfferComment findComment(Long offerCommentId) {
        return verifiedComment(offerCommentId);
    }

    public List<OfferComment> findComments() {
        return offerCommentRepository.findAll();
    }

    public void deleteComment(Long offerCommentId) {

        OfferComment findComment = verifiedComment(offerCommentId);

        // TODO : 스터디 방장 권한 추가
        if (!findComment.getUser().getEmail().equals(userService.getLoginUser().getEmail())) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }

        offerCommentRepository.delete(findComment);
    }
    // 댓글 존재 여부
    private OfferComment verifiedComment(Long offerCommentId) {

        return offerCommentRepository.findById(offerCommentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
    }
}
