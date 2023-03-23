package com.spammayo.spam.study.mapper;

import com.spammayo.spam.stack.mapper.StackMapper;
import com.spammayo.spam.status.StudyStatus;
import com.spammayo.spam.study.dto.StudyDto;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.entity.StudyUser;
import com.spammayo.spam.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface StudyMapper {

    StackMapper stackMapper = Mappers.getMapper(StackMapper.class);

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
        study.setOnline(postDto.isOnline());

        if (!postDto.isOnline()) {
            study.setPlace( postDto.getPlace() );
            study.setLatitude( postDto.getLatitude() );
            study.setLongitude( postDto.getLongitude() );
        }

        study.setActivity( postDto.getActivity() );
        study.setPeriod( postDto.getPeriod() );
        study.setStudyStatus(StudyStatus.BEFORE_RECRUITMENT);

        return study;
    }

    default Study patchDtoToStudy(StudyDto.PatchDto patchDto) {
        if ( patchDto == null ) {
            return null;
        }
        //상태, 제목 - x
        Study study = new Study();

        study.setStudyId(patchDto.getStudyId());
        study.setStudyName(patchDto.getStudyName());
        study.setStartDate(patchDto.getStartDate());
        study.setEndDate(patchDto.getEndDate());
        study.setPersonnel(patchDto.getPersonnel());

        study.setOnline(patchDto.isOnline());

        if (patchDto.isOnline()) {
            study.setPlace(null);
            study.setLongitude(null);
            study.setLatitude(null);
        } else {
            study.setPlace(patchDto.getPlace());
            study.setLongitude(patchDto.getLongitude());
            study.setLatitude(patchDto.getLatitude());
        }

        study.setActivity(patchDto.getActivity());
        study.setPeriod(patchDto.getPeriod());

        return study;
    }

    StudyDto.SimpleResponseDto studyToSimpleResponseDto(Study study);

    default StudyDto.ResponseDto studyToResponseDto(Study study) {
        if ( study == null ) {
            return null;
        }

        User admin = study.getStudyUsers().stream().filter(StudyUser::isAdmin).findFirst().orElseThrow().getUser();
        StudyDto.OwnerDto ownerDto = new StudyDto.OwnerDto(admin.getUserId(), admin.getUserName(), admin.getEmail(), admin.getField(), admin.getProfileUrl());

        return StudyDto.ResponseDto.builder()
                .studyId(study.getStudyId())
                .studyName(study.getStudyName())
                .title(study.getTitle())
                .startDate(study.getStartDate())
                .endDate(study.getEndDate())
                .personnel(study.getPersonnel())
                .place(study.getPlace())
                .latitude(study.getLatitude())
                .longitude(study.getLongitude())
                .activity(study.getActivity())
                .period(study.getPeriod())
                .studyStatus(study.getStudyStatus())
                .online(study.isOnline())
                .createdAt(study.getCreatedAt().toLocalDate())
                .owner(ownerDto)
                .stack(stackMapper.toStudyStackDto(study))
                .checkLikes(isCheck(study))
                .build();
    }

    default List<StudyDto.ListResponseDto> studiesToListResponseDto(List<Study> studies) {
        if ( studies == null ) {
            return null;
        }

        List<StudyDto.ListResponseDto> list = new ArrayList<>(studies.size());

        for ( Study study : studies ) {

            User admin = study.getStudyUsers().stream().filter(StudyUser::isAdmin).findFirst().orElseThrow().getUser();
            StudyDto.SimpleOwnerDto ownerDto = new StudyDto.SimpleOwnerDto(admin.getUserId(), admin.getUserName(), admin.getProfileUrl());

            StudyDto.ListResponseDto listResponseDto = StudyDto.ListResponseDto.builder()
                    .studyId(study.getStudyId())
                    .title(study.getTitle())
                    .startDate(study.getStartDate())
                    .endDate(study.getEndDate())
                    .studyStatus(study.getStudyStatus())
                    .online(study.isOnline())
                    .checkLikes(isCheck(study))
                    .owner(ownerDto)
                    .stack(stackMapper.toStudyStackDto(study))
                    .build();

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

            myPageResponseDto.setStack(stackMapper.toStudyStackDto(study));
            list.add(myPageResponseDto);
        }

        return list;
    }

    private boolean isCheck(Study study) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return study.getLikes().stream().anyMatch(like -> like.getUser().getEmail().equals(email));
    }

}
