package com.spammayo.spam.stack.entity;

import com.spammayo.spam.user.entity.UserStack;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    private List<UserStack> userStacks;

    public void addUserStack(UserStack userStack) {
        userStacks.add(userStack);
        userStack.setStack(this);
    }
}
