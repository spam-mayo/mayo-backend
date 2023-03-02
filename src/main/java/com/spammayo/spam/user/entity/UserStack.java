package com.spammayo.spam.user.entity;

import com.spammayo.spam.stack.entity.Stack;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userStackId;

    @ManyToOne
    @JoinColumn(name = "STACK_ID")
    private Stack stack;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}
