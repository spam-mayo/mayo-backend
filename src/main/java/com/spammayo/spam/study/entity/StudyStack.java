package com.spammayo.spam.study.entity;

import com.spammayo.spam.stack.entity.Stack;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class StudyStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyStackId;

    @ManyToOne
    @JoinColumn(name = "STUDY_ID")
    private Study study;

    @ManyToOne
    @JoinColumn(name = "STACK_ID")
    private Stack stack;
}
