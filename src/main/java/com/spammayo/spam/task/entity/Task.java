package com.spammayo.spam.task.entity;

import com.spammayo.spam.study.entity.Study;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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

    @ManyToOne
    @JoinColumn(name = "STUDY_ID")
    private Study study;

}
