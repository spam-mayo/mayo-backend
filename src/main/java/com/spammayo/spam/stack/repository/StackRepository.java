package com.spammayo.spam.stack.repository;

import com.spammayo.spam.stack.entity.Stack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StackRepository extends JpaRepository<Stack, Long> {

    Optional<Stack> findByStackName(String stackName);
}
