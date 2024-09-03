package com.sparta.preonboardingbackendcourse.domain.user.service;

import com.sparta.preonboardingbackendcourse.domain.user.entity.Role;
import com.sparta.preonboardingbackendcourse.domain.user.entity.User;
import com.sparta.preonboardingbackendcourse.domain.user.entity.UserRole;
import com.sparta.preonboardingbackendcourse.domain.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserRoleServiceImpl: 유저 역할을 연결
 */
@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleService roleService;

    public void addUserRole(User user, String roleName) {

        Role role = roleService.findRoleByName(roleName);

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        userRoleRepository.save(userRole);
    }

    // 사용자의 권한 목록 조회
    public List<String> getUserRoles(User user) {
        return userRoleRepository.findByUser(user).stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.toList());
    }
}
