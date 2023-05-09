package com.spammayo.spam.studycomment.mapper;

import com.spammayo.spam.studycomment.dto.StudyCommentDto;
import com.spammayo.spam.studycomment.entity.StudyComment;
import com.spammayo.spam.task.entity.Task;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StudyCommentMapper {

    default StudyComment postDtoToStudyComment(StudyCommentDto.PostDto requestBody) {

        Task task = new Task();
        task.setTaskId(requestBody.getTaskId());

        StudyComment studyComment = new StudyComment();
        studyComment.setTask(task);
        studyComment.setComment(requestBody.getComment());

        return studyComment;
    }

    default StudyComment patchDtoToStudyComment(StudyCommentDto.PatchDto requestBody) {

        Task task = new Task();
        task.setTaskId(requestBody.getTaskId());

        StudyComment studyComment = new StudyComment();
        studyComment.setTask(task);
        studyComment.setComment(requestBody.getComment());

        return studyComment;
    }

    default StudyCommentDto.ResponseDto studyCommentToStudyCommentResponseDto(StudyComment studyComment) {

        return StudyCommentDto.ResponseDto.builder()
                .studyCommentId(studyComment.getStudyCommentId())
                .taskDate(studyComment.getTask().getTaskDate())
                .comment(studyComment.getComment())
                .build();
    }

    default List<StudyCommentDto.AllResponseDto> studyCommentsToStudyCommentResponseDto(List<StudyComment> studyComments) {

        return studyComments.stream()
                .map(studycomment -> StudyCommentDto.AllResponseDto.builder()
                        .userId(studycomment.getUser().getUserId())
                        .userName(studycomment.getUser().getUserName())
                        .profileUrl(studycomment.getUser().getProfileUrl())
                        .createdAt(studycomment.getCreatedAt().toString())
                        .studyCommentId(studycomment.getStudyCommentId())
                        .comment(studycomment.getComment())
                        .build())
                .collect(Collectors.toList());
    }
}
