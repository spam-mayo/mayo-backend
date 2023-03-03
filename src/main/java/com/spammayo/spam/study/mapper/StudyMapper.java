package com.spammayo.spam.study.mapper;

import com.spammayo.spam.stack.dto.StackDto;
import com.spammayo.spam.stack.entity.Stack;
import com.spammayo.spam.study.dto.StudyDto;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.entity.StudyStack;
import org.mapstruct.Mapper;

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
        study.setPlace( postDto.getPlace() );
        study.setPlaceDetails( postDto.getPlaceDetails() );
        study.setAddress( postDto.getAddress() );
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
        Optional.ofNullable(study.getStudyStacks())
                .ifPresent(ss -> ss.forEach(userStack -> {
                    Stack stack = userStack.getStack();
                    stackList.add(new StackDto(stack.getStackId(), stack.getStackName()));
                }));
        responseDto.setStack(stackList);

        return responseDto;
    }
}
