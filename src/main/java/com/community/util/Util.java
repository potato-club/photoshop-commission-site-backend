package com.community.util;

import com.community.controller.config.exception.ResourceNotFoundException;
import com.community.entity.User;
import com.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Util {

    private final UserRepository userRepository;


    public User findCurrentUser() {

        return userRepository.findById(SecurityUtil.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("NOT_FOUND_MEMBER"));
    }

}
