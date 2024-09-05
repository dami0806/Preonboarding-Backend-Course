package com.sparta.preonboardingbackendcourse.domain.user.service;

import com.sparta.preonboardingbackendcourse.domain.user.entity.Role;
import com.sparta.preonboardingbackendcourse.domain.user.entity.User;
import com.sparta.preonboardingbackendcourse.domain.user.entity.UserRole;
import com.sparta.preonboardingbackendcourse.domain.user.entity.UserStatus;
import com.sparta.preonboardingbackendcourse.domain.user.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRoleServiceTest {
    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserRoleService userRoleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("유저에 역할 추가")
    @Test
    void addUserRole() {
        // given: 유저가 있을떄
        User user = new User("password", "username", "nickname", UserStatus.ACTIVE);
        Role role = new Role("USER");

        // when
        when(roleService.findRoleByName("USER")).thenReturn(role);

        // when
        userRoleService.addUserRole(user, "USER");

        // then
        verify(userRoleRepository, times(1)).save(any(UserRole.class));
    }

    @DisplayName("유저 역할 조회")
    @Test
    void getUserRoles() {
        // given: 유저가 있을 때
        User user = new User("password", "username", "nickname", UserStatus.ACTIVE);

        // UserRole 객체 생성
        UserRole userRole1 = UserRole.builder()
                .user(user)
                .role(new Role("USER"))
                .build();

        UserRole userRole2 = UserRole.builder()
                .user(user)
                .role(new Role("ADMIN"))
                .build();

        when(userRoleRepository.findByUser(user)).thenReturn(Arrays.asList(userRole1, userRole2));

        // when
        List<String> roles = userRoleService.getUserRoles(user);

        // then
        assertEquals(2, roles.size());
        assertEquals("USER", roles.get(0));
        assertEquals("ADMIN", roles.get(1));
    }
}