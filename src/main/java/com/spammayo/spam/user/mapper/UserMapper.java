package com.spammayo.spam.user.mapper;

import com.spammayo.spam.stack.entity.Stack;
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
        List<UserDto.UserStackDto> userStacks = patchDto.getUserStacks();

        if (userStacks != null) {
            userStacks.forEach(stacks -> {
                Stack stack = new Stack();
                stack.setStackId(stacks.getStackId());
                UserStack userStack = new UserStack();
                userStack.setStack(stack);
                user.addUserStack(userStack);
            });
        }

        return user;
    }

    UserDto.SimpleResponseDto userToSimpleResponseDto(User user);

    default UserDto.ResponseDto userToResponseDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.ResponseDto responseDto = new UserDto.ResponseDto();
        List<UserDto.UserStackDto> stackList = new ArrayList<>();
        responseDto.setUserId( user.getUserId() );
        responseDto.setUserName( user.getUserName() );
        responseDto.setEmail( user.getEmail() );
        responseDto.setProfileUrl( user.getProfileUrl() );
        responseDto.setField( user.getField() );
        Optional.ofNullable(user.getUserStacks())
                .ifPresent(us -> us.forEach(userStack -> {
                    Stack stack = userStack.getStack();
                    stackList.add(new UserDto.UserStackDto(stack.getStackId(), stack.getStackName()));
                }));
        responseDto.setStack(stackList);

        return responseDto;
    }

    List<UserDto.ResponseDto> usersToResponseDto(List<User> user);
}
