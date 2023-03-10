package com.spammayo.spam.study.entity;

import com.spammayo.spam.audit.Auditable;
import com.spammayo.spam.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class StudyUser extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyUserId;

    private boolean isAdmin;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @ManyToOne
    @JoinColumn(name = "STUDY_ID")
    private Study study;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    public enum ApprovalStatus {
        APPROVAL("승인"),
        REJECT("거절"),
        WAITING("대기중");

        @Getter
        private String status;

        ApprovalStatus(String status) {
            this.status = status;
        }
    }
}
