package com.spammayo.spam.user.entity;

import com.spammayo.spam.audit.Auditable;
import com.spammayo.spam.likes.Like;
import com.spammayo.spam.offercomment.entity.OfferComment;
import com.spammayo.spam.offerreply.entity.OfferReply;
import com.spammayo.spam.status.Field;
import com.spammayo.spam.study.entity.StudyUser;
import com.spammayo.spam.studycomment.entity.StudyComment;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Table(name = "USERS")
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String profileUrl;

    @Column(nullable = false)
    private String profileKey;

    @Enumerated(EnumType.STRING)
    private Field field;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<UserStack> userStacks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<StudyUser> studyUsers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<StudyComment> studyComments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<OfferComment> offerComments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<OfferReply> offerReply = new ArrayList<>();

    public void addUserStack(UserStack userStack) {
        userStacks.add(userStack);
        userStack.setUser(this);
    }
}
