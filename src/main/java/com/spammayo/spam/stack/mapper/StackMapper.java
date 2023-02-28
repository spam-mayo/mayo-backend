package com.spammayo.spam.stack.mapper;

import com.spammayo.spam.stack.dto.StackDto;
import com.spammayo.spam.stack.entity.Stack;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StackMapper {

    List<StackDto.ResponseDto> stacksToResponseDto(List<Stack> stacks);
}
