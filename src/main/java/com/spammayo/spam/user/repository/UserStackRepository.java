package com.spammayo.spam.user.repository;

import com.spammayo.spam.stack.entity.Stack;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.entity.UserStack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStackRepository extends JpaRepository<UserStack, Long> {
    UserStack findByUserAndStack(User user, Stack stack);
}
