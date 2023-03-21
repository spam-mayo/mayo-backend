package com.spammayo.spam.stack.mapper;

import com.spammayo.spam.stack.dto.StackDto;
import com.spammayo.spam.stack.entity.Stack;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.user.entity.User;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface StackMapper {

    List<StackDto> stacksToResponseDto(List<Stack> stacks);

    default List<StackDto> toStudyStackDto(Study study) {
        List<StackDto> stackList = new ArrayList<>();
        Optional.ofNullable(study.getStudyStacks())
                .ifPresent(studyStack -> studyStack.forEach(userStack -> {
                    Stack stack = userStack.getStack();
                    stackList.add(new StackDto(stack.getStackId(), stack.getStackName()));
                }));

        return stackList;
    }
}
