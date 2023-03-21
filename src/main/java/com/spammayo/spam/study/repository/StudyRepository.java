package com.spammayo.spam.study.repository;

import com.spammayo.spam.status.StudyStatus;
import com.spammayo.spam.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {
    List<Study> findAllByStudyStatus(StudyStatus studyStatus);

    List<Study> findAllByTitleContaining(String keyword);
}
