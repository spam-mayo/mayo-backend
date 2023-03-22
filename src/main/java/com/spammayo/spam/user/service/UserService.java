package com.spammayo.spam.user.service;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.security.utils.CustomAuthorityUtils;
import com.spammayo.spam.security.utils.RedisUtils;
import com.spammayo.spam.status.ApprovalStatus;
import com.spammayo.spam.status.StudyStatus;
import com.spammayo.spam.study.entity.StudyUser;
import com.spammayo.spam.study.repository.StudyRepository;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.repository.UserRepository;
import com.spammayo.spam.user.repository.UserStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils authorityUtils;
    private final RedisUtils redisUtils;
    private final StudyRepository studyRepository;
    private final UserStackRepository userStackRepository;

    public User join(User user) {
        verifiedUser(user.getEmail());

        //이메일 인증 여부 확인
        Object authEmail = redisUtils.get("join_" + user.getEmail());
        if (authEmail == null || !authEmail.toString().equals("confirm")) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_AUTH_REQUIRED);
        }

        List<String> roles = authorityUtils.createRoles(user.getEmail());
        user.setRoles(roles);

        setBasicImage(user);

        String encryptPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptPassword);

        return userRepository.save(user);
    }

    public User updateUser(User user) {
        checkJwtAndUser(user.getUserId());
        User findUser = existUser(user.getUserId());

        Optional.ofNullable(user.getUserName())
                .ifPresent(findUser::setUserName);
        Optional.ofNullable(user.getPassword())
                .ifPresent(newPassword -> {
                    isSocialUser(findUser);
                    findUser.setPassword(passwordEncoder.encode(newPassword));
                });
        Optional.ofNullable(user.getField())
                .ifPresent(findUser::setField);
        Optional.ofNullable(user.getUserStacks())
                .ifPresent(userStack -> {
                    userStackRepository.deleteAll(findUser.getUserStacks());
                    findUser.setUserStacks(userStack);
                });

        return userRepository.save(findUser);
    }

    public void deleteUser(long userId) {
        checkJwtAndUser(userId);
        User findUser = existUser(userId);

        List<StudyUser> studyUsers = findUser.getStudyUsers();

        boolean forbiddenUser = studyUsers.stream()
                .anyMatch(studyUser -> studyUser.getApprovalStatus() == ApprovalStatus.APPROVAL
                        && (studyUser.getStudy().getStudyStatus() == StudyStatus.ONGOING || studyUser.getStudy().getStudyStatus() == StudyStatus.RECRUITING));

        if (forbiddenUser) {
            throw new BusinessLogicException(ExceptionCode.STUDY_EXISTS);
        }

        studyUsers.forEach(studyUser -> {
            if (studyUser.isAdmin()) {
                studyRepository.delete(studyUser.getStudy());
            }
        });

        userRepository.delete(findUser);
    }

    public User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    private User existUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    public void verifiedUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }

    public User getLoginUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    public String getLoginUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    //본인만 접근 허용
    public void checkJwtAndUser(long userId) {
        if (getLoginUser().getUserId() != userId) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }
    }

    //프로필 이미지
    public User updateProfileImage(MultipartFile multipartFile, long userId) throws IOException {
        User user = existUser(userId);
        checkJwtAndUser(userId);

        if (user.getUserId() != getLoginUser().getUserId()) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }

        //no image == basic image
        if (multipartFile == null || multipartFile.isEmpty()) {
            setBasicImage(user);
            return userRepository.save(user);
        }

        boolean isBasicImage = user.getProfileKey().equals("basic.png");
        Map<String, String> profile =
                s3Service.uploadFile(user.getProfileKey(), multipartFile, isBasicImage);
        user.setProfileKey(profile.get("key"));
        user.setProfileUrl(profile.get("url"));

        return userRepository.save(user);
    }

    private void setBasicImage(User user) {
        user.setProfileUrl("https://spam-image.s3.ap-northeast-2.amazonaws.com/basic.png");
        user.setProfileKey("basic");
    }

    public void isSocialUser(User user) {
        if (user.getPassword().equals("GOOGLE") || user.getPassword().equals("KAKAO")) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }
    }
}
