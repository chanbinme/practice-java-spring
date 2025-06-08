package com.chanbinme.springsecurityjwt.repository;

import com.chanbinme.springsecurityjwt.entity.Role;
import com.chanbinme.springsecurityjwt.enums.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // 역할 이름으로 역할 조회
    Optional<Role> findByName(RoleName name);

    // 역할 존재 여부 확인
    boolean existsByName(RoleName name);

}
