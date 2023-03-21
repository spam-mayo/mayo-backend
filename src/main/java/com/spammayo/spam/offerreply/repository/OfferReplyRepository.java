package com.spammayo.spam.offerreply.repository;

import com.spammayo.spam.offerreply.entity.OfferReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferReplyRepository extends JpaRepository<OfferReply, Long> {

    List<OfferReply> findAllByOfferComment_OfferCommentId(Long offerCommentId);
}
