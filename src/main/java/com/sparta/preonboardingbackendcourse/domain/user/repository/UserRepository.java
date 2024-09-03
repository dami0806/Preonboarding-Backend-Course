package com.sparta.preonboardingbackendcourse.domain.user.repository;

import com.sparta.preonboardingbackendcourse.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이름 중복 여부 확인
    boolean existsByUsername(String username);

    // 이름으로 사용자 찾기
    Optional<User> findByUsername(String username);

}

