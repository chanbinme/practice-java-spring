package com.chanbinme.springsecurityjwt.repository;

import com.chanbinme.springsecurityjwt.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자 이름으로 사용자 조회
    Optional<User> findByUsername(String username);

}
