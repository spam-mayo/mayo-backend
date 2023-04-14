package com.spammayo.spam.studycomment.repository;

import com.spammayo.spam.studycomment.entity.StudyComment;
import com.spammayo.spam.task.entity.Task;
import com.spammayo.spam.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyCommentRepository extends JpaRepository<StudyComment, Long> {

    List<StudyComment> findByTaskAndTask_TaskDate(Task task, String taskDate);

    Optional<StudyComment> findByUser(User user);
}
