package com.spammayo.spam.user.controller;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.stack.repository.StackRepository;
import com.spammayo.spam.status.Field;
import com.spammayo.spam.user.dto.UserDto;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.entity.UserStack;
import com.spammayo.spam.user.mapper.UserMapper;
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
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;
    private final StackRepository stackRepository;

    @PostMapping("/join")
    public ResponseEntity postUser(@RequestBody @Valid UserDto.PostDto postDto) {
        if (postDto.getField() == null) postDto.setField(Field.NO_FIELD);
        User user = userService.join(mapper.postDtoToUser(postDto));
        return new ResponseEntity<>(mapper.userToSimpleResponseDto(user), HttpStatus.CREATED);
    }

    @PatchMapping("/{user-id}")
    public ResponseEntity patchUser(@PathVariable("user-id") @Positive long userId,
                                    @RequestBody @Valid UserDto.PatchDto patchDto) {
        patchDto.setUserId(userId);
        User user = mapper.patchDtoToUser(patchDto);
        List<String> userStacks = patchDto.getUserStacks();
        if (userStacks != null) {
            userStacks.forEach(stack -> {
                UserStack userStack = new UserStack();
                userStack.setStack(stackRepository.findByStackName(stack).orElseThrow(() -> new BusinessLogicException(ExceptionCode.INVALID_VALUES)));
                user.addUserStack(userStack);
            });
        } else user.setUserStacks(null);
        User updatedUser = userService.updateUser(user);
        return new ResponseEntity<>(mapper.userToSimpleResponseDto(updatedUser), HttpStatus.OK);

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

    @GetMapping
    public ResponseEntity getAllUser() {
        List<User> users = userService.getAllUser();
        return new ResponseEntity<>(mapper.usersToResponseDto(users), HttpStatus.OK);
    }

    @PatchMapping("/{user-id}/image")
    public ResponseEntity updateProfileImage(@PathVariable("user-id") @Positive long userId,
                                             MultipartFile image) throws IOException {
        User user = userService.updateProfileImage(image, userId);
        return new ResponseEntity<>(mapper.userToSimpleResponseDto(user), HttpStatus.OK);
    }
}
