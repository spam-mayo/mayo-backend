package com.spammayo.spam.offer.repository;

import com.spammayo.spam.offer.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, Long> {
}
