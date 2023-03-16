package com.spammayo.spam.offerreply.entity;

import com.spammayo.spam.audit.Auditable;
import com.spammayo.spam.offer.entity.Offer;
import com.spammayo.spam.offercomment.entity.OfferComment;
import com.spammayo.spam.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class OfferReply extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;

    @Column(nullable = false)
    private String offerReply;

    @Column
    private Boolean secret;

    @ManyToOne
    @JoinColumn(name = "OFFER_COMMENT_ID")
    private OfferComment offerComment;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    public void setOfferComment(OfferComment offerComment) {
        this.offerComment = offerComment;
        offerComment.getOfferReplies().add(this);
    }
}
