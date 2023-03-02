package com.spammayo.spam.stack.service;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.stack.entity.Stack;
import com.spammayo.spam.stack.repository.StackRepository;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.entity.UserStack;
import com.spammayo.spam.user.repository.UserStackRepository;
import com.spammayo.spam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StackService {

    private final StackRepository stackRepository;
    private final UserStackRepository userStackRepository;
    private final UserService userService;

    public List<Stack> getAllStacks() {
        return stackRepository.findAll();
    }

    public void deleteUserStack(long stackId) {
        User user = userService.getLoginUser();
        Stack stack = findStack(stackId);

        UserStack userStack = userStackRepository.findByUserAndStack(user, stack);
        userStackRepository.delete(userStack);
    }

    private Stack findStack(long stackId) {
        return stackRepository.findById(stackId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.STACK_NOT_FOUND));
    }
}
