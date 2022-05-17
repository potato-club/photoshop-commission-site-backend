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
//    private RedisUtil redisUtil;
//    private Util util;
//    private S3Service s3Service;
//
//    @Transactional
//    public void save(User user){
//        userRepository.save(user);
//    }
//
//    /**
//     * 토큰으로 받아온 사용자 정보 넘겨주는 메서드
//     */
//    public User findCurrentUserId() {
//        return util.findCurrentUser();
//    }
//
//    /**
//     * 헤더에서 엑세스 토큰을 받아와서 회원 정보 및 회원 프로필이 수정 되어있는지 확인하고 반환하는 메서드
//     */
//    public UserResponse findValidateProfileSaveUser() {
//        User findUser = findCurrentUserId();
//        boolean validateProfileSaveUser = validateProfileSaveUser(findUser.getId());
//        return UserResponse.builder()
//                .id(findUser.getId())
//                .nickName(findUser.getUserProfile().getNickName())
//                .email(findUser.getEmail())
//                .role(findUser.getRole())
//                .address(findUser.getUserProfile().getAddress())
//                .imageUrl(findUser.getUserProfile().getUploadFile().getStoreFilename())
//                .gender(findUser.getUserProfile().getGender())
//                .phoneNumber(findUser.getUserProfile().getPhoneNumber())
//                .age(findUser.getUserProfile().getAge())
//                .profileSaveUser(validateProfileSaveUser)
//                .build();
//    }
//
//
    /**
     * 회원 프로필 단건 조회
     */
    public User findById(Long id){
        return getFindById(id);
    }
//
//
//    /**
//     * 회원 탈퇴
//     */
//    @Transactional
//    public CustomResponse deleteUser() {
//
//        User findUser = findCurrentUserId();
//
//        userRepository.delete(findUser);
//
//        redisUtil.deleteData(String.valueOf(findUser.getId()));
//
//        return new CustomResponse("회원 탈퇴가 완료 되었습니다.");
//
//    }
//
//    /**
//     * 회원 프로필 생성 및 변경
//     */
//    @Transactional
//    public UserProfileResponse updateProfile(MultipartFile multipartFile, UserProfileRequest userProfileRequest) throws IOException {
//
//        User findUser = findCurrentUserId();
//        String storeFilename = findUser.getUserProfile().getUploadFile().getStoreFilename();
//        if (multipartFile != null) {
//            s3Service.deleteImage(storeFilename);
//            UploadFile uploadFile = s3Service.upload(multipartFile);
//            findUser.addUploadImg(
//                    userProfileRequest.getNickName(),
//                    userProfileRequest.getAddress(),
//                    userProfileRequest.getPhoneNumber(),
//                    userProfileRequest.getEmail(),
//                    userProfileRequest.getGender(),
//                    uploadFile);
//
//            return new UserProfileResponse(uploadFile.getStoreFilename(), "회원 프로필이 저장 되었습니다.");
//        } else {
//            findUser.updateUserProfile(
//                    userProfileRequest.getNickName(),
//                    userProfileRequest.getAddress(),
//                    userProfileRequest.getPhoneNumber(),
//                    userProfileRequest.getEmail(),
//                    userProfileRequest.getGender(),
//                    userProfileRequest.getImageUrl());
//
//            return new UserProfileResponse(findUser.getUserProfile().getUploadFile().getStoreFilename(),"회원 프로필이 저장 되었습니다.");
//        }
//    }
//
//    /**
//     * 현재 유저가 작성한 모든 글 반환
//     * @param pageable 페이지 정보 담는 변수
//     * @return 현재 유저가 작성한 모든 글 반환
//     */
//    public Page<MyPostResponse> getAllMyPosts(Pageable pageable) {
//        return userRepository.findAllMyPosts(pageable, findCurrentUserId());
//    }
//
//    /**
//     * 현재 유저가 작성한 모든 댓글 반환
//     * @param pageable 페이지 정보 담는 변수
//     * @return 현재 유저가 작성한 모든 댓글 반환
//     */
//    public Page<MyCommentResponse> getAllMyComments(Pageable pageable) {
//        return userRepository.findAllMyComments(pageable, findCurrentUserId());
//    }
//
//
//    public UserProfileQueryDto findUserProfileById() {
//        User findUser = findCurrentUserId();
//        return userRepository.findUserProfileById(findUser.getId())
//                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
//    }
//
//
    /**
     * 중복 로직 findById
     */
    private User getFindById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("NOT_FOUND_MEMBER"));
    }
//
//    /**
//     * 회원 프로필이 저장 되어있는지 확인하는 메서드
//     */
//    private boolean validateProfileSaveUser(Long id) {
//
//        UserProfileQueryDto findUser = userRepository.findUserProfileById(id)
//                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
//
//        if (findUser.getImgUrl() == null || findUser.getNickName() == null || findUser.getGender() == null || findUser.getAddress() == null ||
//                findUser.getEmail() == null || findUser.getPhoneNumber() == null) {
//            return false;
//        }
//        return true;
//    }

}
