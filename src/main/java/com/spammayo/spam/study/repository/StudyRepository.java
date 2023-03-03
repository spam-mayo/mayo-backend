package com.spammayo.spam.study.repository;

import com.spammayo.spam.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {
}
