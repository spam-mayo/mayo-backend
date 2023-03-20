package com.spammayo.spam.offer.entity;

import com.spammayo.spam.audit.Auditable;
import com.spammayo.spam.offercomment.entity.OfferComment;
import com.spammayo.spam.study.entity.Study;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Offer extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long offerId;

    private String offerIntro;

    private String offerRule;

    @OneToOne
    @JoinColumn(name = "STUDY_ID")
    private Study study;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.REMOVE)
    private List<OfferComment> comments = new ArrayList<>();

    public void addComment(OfferComment comment) {
        comments.add(comment);
        comment.setOffer(this);
    }
}
