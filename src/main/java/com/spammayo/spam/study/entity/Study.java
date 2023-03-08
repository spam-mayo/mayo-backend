package com.spammayo.spam.study.entity;

import com.spammayo.spam.likes.Like;
import com.spammayo.spam.offer.entity.Offer;
import com.spammayo.spam.task.entity.Task;
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

    @Column(nullable = false)
    private String studyName;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String startDate;

    @Column(nullable = false)
    private String endDate;

    @Column(nullable = false)
    private String personnel;

    private String place;

    private String placeDetails;

    private String address;

    private String activity;

    private String period;

    @Enumerated(EnumType.STRING)
    private StudyStatus studyStatus;

    @Column(nullable = false)
    private boolean online;

    private String notice;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyStack> studyStacks = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyUser> studyUsers = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();

    @OneToOne(mappedBy = "study", cascade = CascadeType.REMOVE)
    private Offer offer;

    @OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE)
    private List<Task> tasks = new ArrayList<>();

    public void addStudyStack(StudyStack studyStack) {
        studyStacks.add(studyStack);
        studyStack.setStudy(this);
    }

    public void addStudyUser(StudyUser studyUser) {
        studyUsers.add(studyUser);
        studyUser.setStudy(this);
    }

    public void addLike(Like like) {
        likes.add(like);
        like.setStudy(this);
    }

    public void addTask(Task Task) {
        tasks.add(Task);
        Task.setStudy(this);
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
