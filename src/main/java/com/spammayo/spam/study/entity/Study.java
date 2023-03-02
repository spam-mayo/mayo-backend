package com.spammayo.spam.study.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyId;

    private String studyName;

    private String title;

    private String startDate;

    private String endDate;

    private String personnel;

    private String place;

    private String placeDetails;

    private String address;

    private String activity;

    private String period;

    private StudyStatus studyStatus;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyStack> studyStacks = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyUser> studyUsers = new ArrayList<>();

    public void addStudyStack(StudyStack studyStack) {
        studyStacks.add(studyStack);
        studyStack.setStudy(this);
    }

    public void addStudyUser(StudyUser studyUser) {
        studyUsers.add(studyUser);
        studyUser.setStudy(this);
    }

    public enum StudyStatus {

        BEFORE_RECRUITMENT("모집전"),
        RECRUITING("모집중"),
        PROCEEDING("진행중"),
        END("종료"),
        CLOSED("폐쇄");

        @Getter
        @Setter
        private String status;

        StudyStatus(String status) {
            this.status = status;
        }
    }


}
