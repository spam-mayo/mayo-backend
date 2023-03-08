package com.spammayo.spam.study.mapper;

import com.spammayo.spam.stack.dto.StackDto;
import com.spammayo.spam.stack.entity.Stack;
import com.spammayo.spam.study.dto.StudyDto;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.entity.StudyStack;
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
            study.setPlaceDetails( postDto.getPlaceDetails() );
            study.setAddress( postDto.getAddress() );
        }

        study.setActivity( postDto.getActivity() );
        study.setPeriod( postDto.getPeriod() );
        study.setStudyStatus(Study.StudyStatus.BEFORE_RECRUITMENT);

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
            study.setPlaceDetails(null);
            study.setAddress(null);
        } else {
            study.setOnline(false);
            study.setPlace( patchDto.getPlace() );
            study.setPlaceDetails( patchDto.getPlaceDetails() );
            study.setAddress( patchDto.getAddress() );
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
        responseDto.setPlaceDetails( study.getPlaceDetails() );
        responseDto.setAddress( study.getAddress() );
        responseDto.setActivity( study.getActivity() );
        responseDto.setPeriod( study.getPeriod() );
        responseDto.setStudyStatus( study.getStudyStatus() );
        responseDto.setOnline( study.isOnline() );
        study.getStudyUsers().forEach(user -> {
            if (user.isAdmin()) {
                User admin = user.getUser();
                responseDto.setUserId(admin.getUserId());
                responseDto.setUserName(admin.getUserName());
                responseDto.setEmail(admin.getEmail());
                responseDto.setUserProfileUrl(admin.getProfileUrl());
                Optional.ofNullable(admin.getField())
                        .ifPresent(responseDto::setField);
            }
        });
        Optional.ofNullable(study.getStudyStacks())
                .ifPresent(ss -> ss.forEach(userStack -> {
                    Stack stack = userStack.getStack();
                    stackList.add(new StackDto(stack.getStackId(), stack.getStackName()));
                }));
        responseDto.setStack(stackList);

        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        boolean check = study.getLikes().stream().anyMatch(like -> like.getUser().getEmail().equals(email));
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
            study.getStudyUsers().forEach(user -> {
                if (user.isAdmin()) {
                    User admin = user.getUser();
                    listResponseDto.setUserName(admin.getUserName());
                    listResponseDto.setUserId(admin.getUserId());
                    listResponseDto.setUserProfileUrl(admin.getProfileUrl());
                }
            });
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

}
