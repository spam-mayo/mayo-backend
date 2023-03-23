package com.spammayo.spam.user.mapper;

import com.spammayo.spam.stack.dto.StackDto;
import com.spammayo.spam.stack.entity.Stack;
import com.spammayo.spam.study.entity.StudyUser;
import com.spammayo.spam.user.dto.UserDto;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.entity.UserStack;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User postDtoToUser(UserDto.PostDto postDto);

    default User patchDtoToUser(UserDto.PatchDto patchDto) {
        if ( patchDto == null ) {
            return null;
        }

        User user = new User();

        user.setUserId( patchDto.getUserId() );
        user.setUserName( patchDto.getUserName() );
        user.setPassword( patchDto.getPassword() );
        user.setField( patchDto.getField() );

        return user;
    }

    UserDto.SimpleResponseDto userToSimpleResponseDto(User user);

    default UserDto.ResponseDto userToResponseDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.ResponseDto responseDto = new UserDto.ResponseDto();
        List<StackDto> stackList = new ArrayList<>();
        responseDto.setUserId( user.getUserId() );
        responseDto.setUserName( user.getUserName() );
        responseDto.setEmail( user.getEmail() );
        responseDto.setProfileUrl( user.getProfileUrl() );
        responseDto.setField( user.getField() );
        Optional.ofNullable(user.getUserStacks())
                .ifPresent(us -> us.forEach(userStack -> {
                    Stack stack = userStack.getStack();
                    stackList.add(new StackDto(stack.getStackId(), stack.getStackName()));
                }));
        responseDto.setStack(stackList);

        return responseDto;
    }

    List<UserDto.ListResponseDto> usersToResponseDto(List<User> user);

    default List<UserDto.ListResponseDto> userToListResponseDto(List<User> users, long studyId) {
        if ( users == null ) {
            return null;
        }

        List<UserDto.ListResponseDto> list = new ArrayList<>();

        for (User user : users) {
            UserDto.ListResponseDto listResponseDto = new UserDto.ListResponseDto();
            listResponseDto.setUserId(user.getUserId());
            listResponseDto.setUserName(user.getUserName());
            listResponseDto.setProfileUrl(user.getProfileUrl());
            Optional.ofNullable(user.getField())
                            .ifPresent(listResponseDto::setField);
            listResponseDto.setCreatedAt(user.getCreatedAt().toLocalDate().toString());
            StudyUser findStudyUser = user.getStudyUsers().stream().filter(studyUser -> studyUser.getStudy().getStudyId() == studyId).findAny().orElseThrow();
            listResponseDto.setApplicationDate(findStudyUser.getCreatedAt().toLocalDate().toString());
            listResponseDto.setApprovalStatus(findStudyUser.getApprovalStatus());
            list.add(listResponseDto);
        }

        return list;
    }
}
