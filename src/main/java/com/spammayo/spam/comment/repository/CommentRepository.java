package com.spammayo.spam.comment.repository;

import com.spammayo.spam.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
