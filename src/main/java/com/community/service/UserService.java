package com.community.service;

import com.community.controller.config.exception.ResourceNotFoundException;
import com.community.dto.CustomResponse;
import com.community.entity.User;
import com.community.repository.UserRepository;
import com.community.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private UserRepository userRepository;

    /**
     * 회원 프로필 단건 조회
     */
    public User findById(Long id){
        return getFindById(id);
    }

    /**
     * 중복 로직 findById
     */
    private User getFindById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("NOT_FOUND_MEMBER"));
    }
}
