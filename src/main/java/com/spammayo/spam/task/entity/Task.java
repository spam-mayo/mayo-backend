package com.spammayo.spam.task.entity;

import com.spammayo.spam.studycomment.entity.StudyComment;
import com.spammayo.spam.study.entity.Study;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @Column(nullable = false)
    private String task;

    @Column(nullable = false)
    private String taskDate;

    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    private List<StudyComment> studyComments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "STUDY_ID")
    private Study study;

    public void addStudyComment(StudyComment studyComment) {
        studyComments.add(studyComment);
        studyComment.setTask(this);
    }

}
