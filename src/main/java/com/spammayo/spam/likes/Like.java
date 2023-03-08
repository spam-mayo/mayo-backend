package com.spammayo.spam.likes;

import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "STUDY_ID")
    private Study study;

    public Like(User user, Study study) {
        this.user = user;
        this.study = study;
    }
}
