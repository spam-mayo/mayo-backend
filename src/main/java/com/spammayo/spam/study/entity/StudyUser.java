package com.spammayo.spam.study.entity;

import com.spammayo.spam.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class StudyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyUserId;

    private boolean isAdmin;

    private boolean approval;

    @ManyToOne
    @JoinColumn(name = "STUDY_ID")
    private Study study;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

}
