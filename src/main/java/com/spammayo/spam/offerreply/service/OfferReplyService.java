package com.spammayo.spam.offerreply.service;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.offercomment.entity.OfferComment;
import com.spammayo.spam.offercomment.service.OfferCommentService;
import com.spammayo.spam.offerreply.entity.OfferReply;
import com.spammayo.spam.offerreply.repository.OfferReplyRepository;
import com.spammayo.spam.study.entity.StudyUser;
import com.spammayo.spam.study.repository.StudyUserRepository;
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
public class OfferReplyService {

    private final OfferReplyRepository offerReplyRepository;
    private final OfferCommentService offerCommentService;
    private final UserService userService;
    private final StudyUserRepository studyUserRepository;

    public OfferReply createReply(OfferReply offerReply,
                                  Long offerCommentId) {

        OfferComment offerComment = offerCommentService.findComment(offerCommentId);
        User user = userService.getLoginUser();

        offerComment.addOfferReply(offerReply);
        offerReply.setUser(user);
        offerReply.setOfferComment(offerComment);

        return offerReplyRepository.save(offerReply);
    }

    public OfferReply updateReply(OfferReply offerReply,
                                  Long replyId) {

        OfferReply findReply = verifiedReply(replyId);

        if (!findReply.getUser().getEmail().equals(userService.getLoginUser().getEmail())) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }

        Optional.ofNullable(offerReply.getOfferReply())
                .ifPresent(findReply::setOfferReply);

        Optional.ofNullable(offerReply.getSecret())
                .ifPresent(findReply::setSecret);

        return offerReplyRepository.save(findReply);
    }

    public OfferReply findReply(Long replyId) { return verifiedReply(replyId); }

    public List<OfferReply> findReplies() { return offerReplyRepository.findAll(); }

    public void deleteReply(Long replyId, Long studyUserId) {

        OfferReply findReply = verifiedReply(replyId);

        StudyUser studyUser = studyUserRepository.findById(studyUserId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        if (!findReply.getUser().getEmail().equals(userService.getLoginUser().getEmail()) && !studyUser.isAdmin()) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }

        offerReplyRepository.delete(findReply);
    }

    private OfferReply verifiedReply(Long replyId) {

        return offerReplyRepository.findById(replyId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
    }
}
