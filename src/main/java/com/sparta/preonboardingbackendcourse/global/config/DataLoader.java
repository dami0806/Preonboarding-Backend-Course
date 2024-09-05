package com.sparta.preonboardingbackendcourse.global.config;

import com.sparta.preonboardingbackendcourse.domain.user.entity.Role;
import com.sparta.preonboardingbackendcourse.domain.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// 데이터 초기 로드
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("USER"));
            roleRepository.save(new Role("ADMIN"));
            roleRepository.save(new Role("MANAGER"));
        }
    }
}
