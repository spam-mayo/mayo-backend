package com.spammayo.spam.offercomment.repository;

import com.spammayo.spam.offer.entity.Offer;
import com.spammayo.spam.offercomment.entity.OfferComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferCommentRepository extends JpaRepository<OfferComment, Long> {

    List<OfferComment> findByOffer(Offer offer);
}
