package com.spammayo.spam.offercomment.service;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.offer.entity.Offer;
import com.spammayo.spam.offer.service.OfferService;
import com.spammayo.spam.offercomment.dto.OfferCommentDto;
import com.spammayo.spam.offercomment.entity.OfferComment;
import com.spammayo.spam.offercomment.mapper.OfferCommentMapper;
import com.spammayo.spam.offercomment.repository.OfferCommentRepository;
import com.spammayo.spam.offerreply.repository.OfferReplyRepository;
import com.spammayo.spam.study.entity.StudyUser;
import com.spammayo.spam.study.repository.StudyUserRepository;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final StudyUserRepository studyUserRepository;
    private final OfferCommentMapper offerCommentMapper;
    private final OfferReplyRepository offerReplyRepository;

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

    @Transactional(readOnly = true)
    public ResponseEntity findAll(List<OfferComment> offerComment, Offer offer) {

        if (!userService.getLoginUserEmail().equals("anonymousUser")) {
            User user = userService.getLoginUser();
            User admin = offer.getStudy().getStudyUsers().stream().filter(StudyUser::isAdmin).findFirst().orElseThrow().getUser();

            offerComment.forEach(comment -> {
                if (comment.getSecret()) {
                    User commentUser = comment.getUser();
                    if (user != commentUser && user != admin) {
                        comment.setComment(null);
                    }
                }
            });
        }
        else {
            offerComment.forEach(comment -> {
                if (comment.getSecret()) {
                    comment.setComment(null);
                }
            });
        }

        List<OfferCommentDto.AllResponseDto> response = offerCommentMapper.commentsToCommentAllResponseDto(offerComment, offerReplyRepository);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public void deleteComment(Long offerCommentId, Long studyUserId) {

        OfferComment findComment = verifiedComment(offerCommentId);

        StudyUser studyUser = studyUserRepository.findById(studyUserId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        if (!findComment.getUser().getEmail().equals(userService.getLoginUser().getEmail()) && !studyUser.isAdmin()) {
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
