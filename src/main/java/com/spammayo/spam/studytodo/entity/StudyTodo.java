package com.spammayo.spam.studytodo.entity;

import com.spammayo.spam.study.entity.Study;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class StudyTodo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyTodoId;

    @Column(nullable = false)
    private String todo;

    @Column(nullable = false)
    private String todoDate;

    @ManyToOne
    @JoinColumn(name = "STUDY_ID")
    private Study study;

}
