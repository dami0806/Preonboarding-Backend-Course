package com.sparta.preonboardingbackendcourse.domain.user.service;

import com.sparta.preonboardingbackendcourse.domain.user.entity.Role;
import com.sparta.preonboardingbackendcourse.domain.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    public Role findRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}
