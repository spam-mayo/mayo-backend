package com.spammayo.spam.task.repository;

import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByStudyAndTaskDate(Study study, String taskDate);
}
