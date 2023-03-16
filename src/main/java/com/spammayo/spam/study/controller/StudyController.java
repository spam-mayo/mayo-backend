package com.spammayo.spam.study.controller;

import com.spammayo.spam.dto.MultiResponseDto;
import com.spammayo.spam.study.dto.StudyDto;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.mapper.StudyMapper;
import com.spammayo.spam.study.service.StudyService;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
@Validated
public class StudyController {

    private final StudyService studyService;
    private final StudyMapper mapper;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity postStudy(@RequestBody @Valid StudyDto.InputDto postDto) {
        Study study = studyService.createStudy(mapper.inputDtoToStudy(postDto));
        return new ResponseEntity<>(mapper.studyToSimpleResponseDto(study), HttpStatus.CREATED);
    }

    @PatchMapping("/{study-id}")
    public ResponseEntity patchStudy(@PathVariable("study-id") @Positive long studyId,
                                     @RequestBody @Validated StudyDto.PatchDto patchDto) {
        patchDto.setStudyId(studyId);
        Study study = studyService.updateStudy(mapper.patchDtoToStudy(patchDto));
        return new ResponseEntity<>(mapper.studyToSimpleResponseDto(study), HttpStatus.OK);
    }

    @GetMapping("/{study-id}")
    public ResponseEntity getStudy(@PathVariable("study-id") @Positive long studyId) {
        Study study = studyService.findStudy(studyId);
        return new ResponseEntity<>(mapper.studyToResponseDto(study), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getStudies(@RequestParam @Positive int page,
                                     @RequestParam @Positive int size,
                                     @RequestParam(required = false) String field,
                                     @RequestParam(required = false) String stack,
                                     @RequestParam(required = false) String sort,
                                     @RequestParam(required = false) String area,
                                     @RequestParam(required = false) Study.StudyStatus status) {
        Page<Study> pages = studyService.findStudies(page - 1, size, field, stack, sort, area, status);
        List<Study> studies = pages.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(mapper.studiesToListResponseDto(studies), pages), HttpStatus.OK);
    }

    //스터디 폐쇄
    @DeleteMapping("/{study-id}")
    public ResponseEntity deleteStudy(@PathVariable("study-id") @Positive long studyId) {
        studyService.deleteStudy(studyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //좋아요
    @PostMapping("/{study-id}/likes")
    public ResponseEntity postLikes(@PathVariable("study-id") @Positive long studyId) {
        studyService.checkLikes(studyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //마이페아지 - 회원의 스터디
    @GetMapping("/my-page")
    public ResponseEntity getUserStudy(@RequestParam(required = false) String tab,
                                       @RequestParam(required = false) String status) {
        List<Study> userStudy = studyService.getUserStudy(tab, status);
        return new ResponseEntity<>(mapper.studiesToMyPageResponseDto(userStudy), HttpStatus.OK);
    }

    //관리자 - 공지사항
    @PatchMapping("/{study-id}/notice")
    public ResponseEntity patchNotice(@PathVariable("study-id") @Positive long studyId,
                                      @RequestBody @Valid StudyDto.NoticeDto noticeDto) {
        noticeDto.setStudyId(studyId);
        studyService.updateNotice(mapper.noticeDtoToStudy(noticeDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{study-id}/notice")
    public ResponseEntity deleteStudyNotice(@PathVariable("study-id") @Positive long studyId) {
        studyService.deleteNotice(studyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{study-id}/notice")
    public ResponseEntity getStudyNotice(@PathVariable("study-id") @Positive long studyId) {
        Study study = studyService.findNotice(studyId);
        return new ResponseEntity<>(mapper.studyToNoticeDto(study), HttpStatus.OK);
    }

    //신청
    @PostMapping("/{study-id}/group")
    public ResponseEntity postUserRequest(@PathVariable("study-id") @Positive long studyId) {
        studyService.userRequestForStudy(studyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //신청 취소
    @DeleteMapping("{study-id}/group")
    public ResponseEntity deleteUserRequest(@PathVariable("study-id") @Positive long studyId) {
        studyService.userCancelForStudy(studyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{study-id}/users")
    public ResponseEntity getStudyUsers(@PathVariable("study-id") @Positive long studyId,
                                        @RequestParam @NotBlank String status,
                                        @RequestParam @Positive int page,
                                        @RequestParam @Positive int size) {
        Page<User> pages = studyService.getStudyUser(studyId, status, page - 1, size);
        List<User> users = pages.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(userMapper.userToListResponseDto(users, studyId), pages), HttpStatus.OK);
    }

    @PutMapping("/{study-id}/users/{user-id}/{assign}")
    public ResponseEntity postStudyUser(@PathVariable("study-id") @Positive long studyId,
                                        @PathVariable("user-id") @Positive long userId,
                                        @PathVariable("assign") @NotBlank String assign) {
        studyService.assignStudyUser(studyId, userId, assign);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
