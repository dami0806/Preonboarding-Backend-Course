package com.sparta.preonboardingbackendcourse.domain.user.repository;

import com.sparta.preonboardingbackendcourse.domain.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}

