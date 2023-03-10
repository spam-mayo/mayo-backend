package com.spammayo.spam.studytodo.mapper;

import com.spammayo.spam.studytodo.dto.StudyTodoDto;
import com.spammayo.spam.studytodo.entity.StudyTodo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudyTodoMapper {

    StudyTodo postDtoToStudyTodo(StudyTodoDto.PostDto postDto);

    StudyTodo patchDtoToStudyTodo(StudyTodoDto.PatchDto patchDto);

    StudyTodoDto.SimpleResponseDto studyTodoToSimpleResponseDto(StudyTodo studyTodo);

    StudyTodoDto.ResponseDto studyTodoToResponseDto(StudyTodo studyTodo);

}
