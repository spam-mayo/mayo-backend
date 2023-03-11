package com.spammayo.spam.stack.controller;

import com.spammayo.spam.stack.entity.Stack;
import com.spammayo.spam.stack.mapper.StackMapper;
import com.spammayo.spam.stack.service.StackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stacks")
@RequiredArgsConstructor
public class StackController {
    private final StackService stackService;
    private final StackMapper mapper;

    @GetMapping
    public ResponseEntity getStacks() {
        List<Stack> stacks = stackService.getAllStacks();
        return new ResponseEntity<>(mapper.stacksToResponseDto(stacks), HttpStatus.OK);
    }

    //회원 스택
    @DeleteMapping("/{stack-id}/user")
    public ResponseEntity deleteUserStack(@PathVariable("stack-id") long stackId) {
        stackService.deleteUserStack(stackId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
