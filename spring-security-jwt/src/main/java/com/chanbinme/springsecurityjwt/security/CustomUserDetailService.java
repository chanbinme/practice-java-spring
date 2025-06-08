package com.chanbinme.springsecurityjwt.security;

import com.chanbinme.springsecurityjwt.entity.User;
import com.chanbinme.springsecurityjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 인증 정보를 로드하는 서비스 클래스
 * - UserDetailsService 인터페이스를 구현하여 사용자 정보를 조회
 * - Spring Security에서 인증 및 권한 부여에 사용됨
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. DB에서 사용자 조회
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 2. 사용자 정보로 UserDetails 객체 생성
        return UserPrincipal.create(user);
    }
}
