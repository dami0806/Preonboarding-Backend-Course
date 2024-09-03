package com.sparta.preonboardingbackendcourse.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String userNickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    //유저와 유저롤
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<UserRole> roles = new HashSet<>();

    @Builder
    public User(String password, String username, String userNickname, UserStatus userStatus) {
        this.password = password;
        this.username = username;
        this.userNickname = userNickname;
        this.userStatus = userStatus;
    }

    public void login() {
        this.userStatus = UserStatus.ACTIVE;
    }

    // 역할 추가 메서드
    public void addRole(Role role) {
        UserRole userRole = UserRole.builder()
                .user(this)
                .role(role)
                .build();
        roles.add(userRole);
    }
}

