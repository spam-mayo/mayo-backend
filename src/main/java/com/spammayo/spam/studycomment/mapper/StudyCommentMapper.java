package com.spammayo.spam.studycomment.mapper;

import com.spammayo.spam.studycomment.dto.StudyCommentDto;
import com.spammayo.spam.studycomment.entity.StudyComment;
import com.spammayo.spam.task.entity.Task;
import com.spammayo.spam.user.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

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
                .comment(studyComment.getComment())
                .build();
    }

    List<StudyCommentDto.ResponseDto> studyCommentsToStudyCommentResponseDto(List<StudyComment> studyComments);
}
