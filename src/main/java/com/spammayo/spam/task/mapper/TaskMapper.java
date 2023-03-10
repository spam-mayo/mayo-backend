package com.spammayo.spam.task.mapper;

import com.spammayo.spam.task.dto.TaskDto;
import com.spammayo.spam.task.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task postDtoToTask(TaskDto.PostDto postDto);

    Task patchDtoToTask(TaskDto.PatchDto patchDto);

    TaskDto.SimpleResponseDto taskToSimpleResponseDto(Task task);

    TaskDto.ResponseDto taskToResponseDto(Task task);

}
