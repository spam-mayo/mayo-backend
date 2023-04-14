package com.spammayo.spam.studycomment.entity;

import com.spammayo.spam.audit.Auditable;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.task.entity.Task;
import com.spammayo.spam.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class StudyComment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyCommentId;

    @Column(nullable = false)
    private String comment;
    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "STUDY_ID")
    private Study study;

    public void setTask(Task task) {
        this.task = task;
        task.getStudyComments().add(this);
    }

    public void setStudy(Study study) {
        this.study = study;
        study.getComments().add(this);
    }
}
