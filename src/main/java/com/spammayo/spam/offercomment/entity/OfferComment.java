package com.spammayo.spam.offercomment.entity;

import com.spammayo.spam.audit.Auditable;
import com.spammayo.spam.offer.entity.Offer;
import com.spammayo.spam.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class OfferComment extends Auditable {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long offerCommentId;

    @Column(nullable = false)
    private String comment;

    @Column
    private Boolean secret;

//    @OneToMany(mappedBy = "reply", cascade = { CascadeType.ALL })
//    private List<Reply> replies = new ArrayList();

    @ManyToOne
    @JoinColumn(name = "OFFER_ID")
    private Offer offer;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    public void setOffer(Offer offer) {
        this.offer = offer;
        offer.getComments().add(this);
    }
}
