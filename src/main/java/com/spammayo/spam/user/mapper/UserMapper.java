package com.spammayo.spam.user.mapper;

import com.spammayo.spam.user.dto.UserDto;
import com.spammayo.spam.user.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User postDtoToUser(UserDto.PostDto postDto);

    User patchDtoToUser(UserDto.PatchDto patchDto);

    UserDto.SimpleResponseDto userToSimpleResponseDto(User user);

    UserDto.ResponseDto userToResponseDto(User user);

    List<UserDto.ResponseDto> usersToResponseDto(List<User> user);
}
