package com.spammayo.spam.study.repository;

import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.entity.StudyUser;
import com.spammayo.spam.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyUserRepository extends JpaRepository<StudyUser, Long> {

    Optional<StudyUser> findByStudyAndUser(Study study, User user);
}
