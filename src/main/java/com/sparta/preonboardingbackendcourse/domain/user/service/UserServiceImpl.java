package com.sparta.preonboardingbackendcourse.domain.user.service;

import com.sparta.preonboardingbackendcourse.domain.user.dto.SignupRequest;
import com.sparta.preonboardingbackendcourse.domain.user.dto.SignupResponse;
import com.sparta.preonboardingbackendcourse.domain.user.entity.User;
import com.sparta.preonboardingbackendcourse.domain.user.entity.UserStatus;
import com.sparta.preonboardingbackendcourse.domain.user.repository.UserRepository;

import com.sparta.preonboardingbackendcourse.global.exception.CustomException;
import com.sparta.preonboardingbackendcourse.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;

    @Override
    public SignupResponse signup(SignupRequest signupRequest) {

        // 이미 존재하는 사용자 확인
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        User user = User.builder()
                .password(encodedPassword)
                .username(signupRequest.getUsername())
                .userNickname(signupRequest.getNickname())
                .userStatus(UserStatus.PENDING) // 사용자 상태 비활성화 -> 로그인하면 그때 활성화시킬것
                .build();

        userRepository.save(user);

        // 권한 지정
        userRoleService.addUserRole(user,"USER"); // 지금은 임시로 USER로 권한 들어가게

        // 권한 정보 조회
        List<String> authorities = userRoleService.getUserRoles(user);

        return SignupResponse.builder()
                .username(user.getUsername())
                .nickname(user.getUserNickname())
                .authorities(authorities)
                .build();
    }
}
