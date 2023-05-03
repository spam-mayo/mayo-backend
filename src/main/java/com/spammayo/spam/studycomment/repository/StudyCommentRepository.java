package com.spammayo.spam.studycomment.repository;

import com.spammayo.spam.studycomment.entity.StudyComment;
import com.spammayo.spam.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyCommentRepository extends JpaRepository<StudyComment, Long> {

    List<StudyComment> findByTaskAndTask_TaskDate(Task task, String taskDate);
}
