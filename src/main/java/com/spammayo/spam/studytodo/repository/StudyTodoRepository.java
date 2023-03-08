package com.spammayo.spam.studytodo.repository;

import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.studytodo.entity.StudyTodo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyTodoRepository extends JpaRepository<StudyTodo, Long> {

    Optional<StudyTodo> findByStudyAndTodoDate(Study study, String todoDate);
}
