package com.spammayo.spam.study.mapper;

import com.spammayo.spam.stack.dto.StackDto;
import com.spammayo.spam.stack.entity.Stack;
import com.spammayo.spam.status.StudyStatus;
import com.spammayo.spam.study.dto.StudyDto;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.entity.StudyStack;
import com.spammayo.spam.study.entity.StudyUser;
import com.spammayo.spam.user.entity.User;
import org.mapstruct.Mapper;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface StudyMapper {

    default Study inputDtoToStudy(StudyDto.InputDto postDto) {
        if ( postDto == null ) {
            return null;
        }

        Study study = new Study();

        study.setStudyName( postDto.getStudyName() );
        study.setTitle( postDto.getTitle() );
        study.setStartDate(postDto.getStartDate() );
        study.setEndDate( postDto.getEndDate() );
        study.setPersonnel( postDto.getPersonnel() );

        //장소없음 check시
        if (postDto.isOnline()) {
            study.setOnline(true);
        } else {
            study.setPlace( postDto.getPlace() );
            study.setLatitude( postDto.getLatitude() );
            study.setLongitude( postDto.getLongitude() );
        }

        study.setActivity( postDto.getActivity() );
        study.setPeriod( postDto.getPeriod() );
        study.setStudyStatus(StudyStatus.BEFORE_RECRUITMENT);

        List<StackDto> studyStacks = postDto.getStudyStacks();

        if (studyStacks != null) {
            studyStacks.forEach(stacks -> {
                Stack stack = new Stack();
                stack.setStackId(stacks.getStackId());
                StudyStack studyStack = new StudyStack();
                studyStack.setStack(stack);
                study.addStudyStack(studyStack);
            });
        }
        return study;
    }

    default Study patchDtoToStudy(StudyDto.PatchDto patchDto) {
        if ( patchDto == null ) {
            return null;
        }
        //상태, 제목 - x
        Study study = new Study();

        study.setStudyId(patchDto.getStudyId());
        study.setStudyName( patchDto.getStudyName() );
        study.setStartDate(patchDto.getStartDate() );
        study.setEndDate( patchDto.getEndDate() );
        study.setPersonnel( patchDto.getPersonnel() );

        //장소없음 check시
        if (patchDto.isOnline()) {
            study.setOnline(true);
            study.setPlace(null);
            study.setLongitude(null);
            study.setLatitude(null);
        } else {
            study.setOnline(false);
            study.setPlace( patchDto.getPlace() );
            study.setLongitude( patchDto.getLongitude() );
            study.setLatitude( patchDto.getLatitude() );
        }

        study.setActivity( patchDto.getActivity() );
        study.setPeriod( patchDto.getPeriod() );

        List<StackDto> studyStacks = patchDto.getStudyStacks();

        if (studyStacks != null) {
            studyStacks.forEach(stacks -> {
                Stack stack = new Stack();
                stack.setStackId(stacks.getStackId());
                StudyStack studyStack = new StudyStack();
                studyStack.setStack(stack);
                study.addStudyStack(studyStack);
            });
        }
        return study;
    }

    StudyDto.SimpleResponseDto studyToSimpleResponseDto(Study study);

    default StudyDto.ResponseDto studyToResponseDto(Study study) {
        if ( study == null ) {
            return null;
        }

        StudyDto.ResponseDto responseDto = new StudyDto.ResponseDto();
        List<StackDto> stackList = new ArrayList<>();

        responseDto.setStudyId( study.getStudyId() );
        responseDto.setStudyName( study.getStudyName() );
        responseDto.setTitle( study.getTitle() );
        responseDto.setStartDate( study.getStartDate() );
        responseDto.setEndDate( study.getEndDate() );
        responseDto.setPersonnel( study.getPersonnel() );
        responseDto.setPlace( study.getPlace() );
        responseDto.setLatitude( study.getLatitude() );
        responseDto.setLongitude( study.getLongitude() );
        responseDto.setActivity( study.getActivity() );
        responseDto.setPeriod( study.getPeriod() );
        responseDto.setStudyStatus( study.getStudyStatus() );
        responseDto.setOnline( study.isOnline() );
        responseDto.setCreatedAt( study.getCreatedAt().toLocalDate() );

        StudyUser studyUser = study.getStudyUsers().stream().filter(StudyUser::isAdmin).findFirst().orElseThrow();
        User admin = studyUser.getUser();
        StudyDto.OwnerDto ownerDto = new StudyDto.OwnerDto(admin.getUserId(), admin.getUserName(), admin.getEmail(), admin.getField(), admin.getProfileUrl());
        responseDto.setOwner(ownerDto);

        Optional.ofNullable(study.getStudyStacks())
                .ifPresent(ss -> ss.forEach(userStack -> {
                    Stack stack = userStack.getStack();
                    stackList.add(new StackDto(stack.getStackId(), stack.getStackName()));
                }));
        responseDto.setStack(stackList);

        boolean check = isCheck(study);
        responseDto.setCheckLikes(check);

        return responseDto;
    }

    default List<StudyDto.ListResponseDto> studiesToListResponseDto(List<Study> studies) {
        if ( studies == null ) {
            return null;
        }

        List<StudyDto.ListResponseDto> list = new ArrayList<>(studies.size());
        for ( Study study : studies ) {
            List<StackDto> stackList = new ArrayList<>();
            StudyDto.ListResponseDto listResponseDto = new StudyDto.ListResponseDto();

            listResponseDto.setStudyId( study.getStudyId() );
            listResponseDto.setTitle( study.getTitle() );
            listResponseDto.setStartDate( study.getStartDate() );
            listResponseDto.setEndDate( study.getEndDate() );
            listResponseDto.setStudyStatus( study.getStudyStatus() );
            listResponseDto.setOnline( study.isOnline() );
            boolean check = isCheck(study);
            listResponseDto.setCheckLikes(check);

            //작성자
            StudyUser studyUser = study.getStudyUsers().stream().filter(StudyUser::isAdmin).findFirst().orElseThrow();
            User admin = studyUser.getUser();
            StudyDto.SimpleOwnerDto ownerDto = new StudyDto.SimpleOwnerDto(admin.getUserId(), admin.getUserName(), admin.getProfileUrl());
            listResponseDto.setOwner(ownerDto);

            study.getStudyStacks().forEach(ss -> {
                Stack stack = ss.getStack();
                StackDto stackDto = new StackDto(stack.getStackId(), stack.getStackName());
                stackList.add(stackDto);
            });
            listResponseDto.setStack(stackList);
            list.add(listResponseDto);
        }

        return list;
    }

    Study noticeDtoToStudy(StudyDto.NoticeDto noticeDto);

    StudyDto.NoticeDto studyToNoticeDto(Study study);

    default List<StudyDto.MyPageResponseDto> studiesToMyPageResponseDto(List<Study> studies) {
        if ( studies == null ) {
            return null;
        }

        List<StudyDto.MyPageResponseDto> list = new ArrayList<>(studies.size());
        for ( Study study : studies ) {
            List<StackDto> stackList = new ArrayList<>();
            StudyDto.MyPageResponseDto myPageResponseDto = new StudyDto.MyPageResponseDto();

            myPageResponseDto.setStudyId( study.getStudyId() );
            myPageResponseDto.setTitle( study.getTitle() );
            myPageResponseDto.setStartDate( study.getStartDate() );
            myPageResponseDto.setEndDate( study.getEndDate() );
            myPageResponseDto.setStudyStatus( study.getStudyStatus() );

            String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            StudyUser findStudyUser = study.getStudyUsers().stream().filter(studyUser -> studyUser.getUser().getEmail().equals(email)).findFirst().orElse(null);

            if (findStudyUser == null) {
                myPageResponseDto.setAdmin(false);
                myPageResponseDto.setApprovalStatus(null);
            } else {
                myPageResponseDto.setAdmin(findStudyUser.isAdmin());
                myPageResponseDto.setApprovalStatus(findStudyUser.getApprovalStatus());
            }

            study.getStudyStacks().forEach(ss -> {
                Stack stack = ss.getStack();
                StackDto stackDto = new StackDto(stack.getStackId(), stack.getStackName());
                stackList.add(stackDto);
            });
            myPageResponseDto.setStack(stackList);
            list.add(myPageResponseDto);
        }

        return list;
    }

    private static boolean isCheck(Study study) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return study.getLikes().stream().anyMatch(like -> like.getUser().getEmail().equals(email));
    }

}
