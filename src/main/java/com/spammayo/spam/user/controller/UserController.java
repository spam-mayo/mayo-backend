package com.spammayo.spam.user.controller;

import com.spammayo.spam.user.mapper.UserMapper;
import com.spammayo.spam.user.dto.UserDto;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Validated
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping("/join")
    public ResponseEntity postUser(@RequestBody @Valid UserDto.PostDto postDto) {
        User user = userService.join(mapper.postDtoToUser(postDto));
        return new ResponseEntity<>(mapper.userToSimpleResponseDto(user), HttpStatus.CREATED);
    }

    @PatchMapping("/{user-id}")
    public ResponseEntity patchUser(@PathVariable("user-id") long userId,
                                    @RequestBody @Valid UserDto.PatchDto patchDto) {
        patchDto.setUserId(userId);
        User user = userService.updateUser(mapper.patchDtoToUser(patchDto));
        return new ResponseEntity<>(mapper.userToSimpleResponseDto(user), HttpStatus.OK);

    }

    @DeleteMapping("/{user-id}")
    public ResponseEntity deleteUser(@PathVariable("user-id") @Positive long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{user-id}")
    public ResponseEntity getUser(@PathVariable("user-id") @Positive long userId) {
        User user = userService.getUser(userId);
        return new ResponseEntity<>(mapper.userToResponseDto(user), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity getAllUser() {
        List<User> users = userService.getAllUser();
        return new ResponseEntity<>(mapper.usersToResponseDto(users), HttpStatus.OK);
    }

    @PatchMapping("/{user-id}/profile")
    public ResponseEntity updateProfileImage(@PathVariable("user-id") @Positive long userId,
                                             MultipartFile image) throws IOException {
        User user = userService.updateProfileImage(image, userId);
        return new ResponseEntity(mapper.userToSimpleResponseDto(user), HttpStatus.OK);
    }
}
