package com.spammayo.spam.study.repository;

import com.spammayo.spam.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {
    List<Study> findAllByStudyStatus(Study.StudyStatus studyStatus);
}
