package com.spammayo.spam.offercomment.repository;

import com.spammayo.spam.offercomment.entity.OfferComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferCommentRepository extends JpaRepository<OfferComment, Long> {
}
