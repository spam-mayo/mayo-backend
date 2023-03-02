package com.spammayo.spam.stack.entity;

import com.spammayo.spam.study.entity.StudyStack;
import com.spammayo.spam.user.entity.UserStack;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Stack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stackId;

    private String stackName;

    @OneToMany(mappedBy = "stack", cascade = CascadeType.REMOVE)
    private List<UserStack> userStacks = new ArrayList<>();

    @OneToMany(mappedBy = "stack", cascade = CascadeType.REMOVE)
    private List<StudyStack> studyStacks = new ArrayList<>();

    public void addUserStack(UserStack userStack) {
        userStacks.add(userStack);
        userStack.setStack(this);
    }

    public void addStudyStack(StudyStack studyStack) {
        studyStacks.add(studyStack);
        studyStack.setStack(this);
    }
}
