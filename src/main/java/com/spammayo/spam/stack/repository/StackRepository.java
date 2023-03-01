package com.spammayo.spam.stack.repository;

import com.spammayo.spam.stack.entity.Stack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StackRepository extends JpaRepository<Stack, Long> {
}
