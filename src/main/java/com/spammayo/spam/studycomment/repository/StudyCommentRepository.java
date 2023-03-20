package com.spammayo.spam.studycomment.repository;

import com.spammayo.spam.studycomment.entity.StudyComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCommentRepository extends JpaRepository<StudyComment, Long> {
}
