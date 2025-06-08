package com.chanbinme.springsecurityjwt.security;

import com.chanbinme.springsecurityjwt.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 사용자 인증 정보를 나타내는 UserDetails 구현 클래스
 * - Spring Security에서 사용자 인증 및 권한 부여에 사용됨
 * - 기본적인 사용자 정보와 권한을 제공
 */
@Getter
@Builder
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private Long id;    // 사용자 ID
    private String username;    // 사용자 이름
    private String email;   // 이메일
    private String password;    // 암호화된 비밀번호
    private Collection<? extends GrantedAuthority> authorities;   // 사용자 권한 목록

    public static UserPrincipal create(User user) {
        // 사용자 엔티티에서 권한 목록을 가져와 GrantedAuthority 객체로 변환
        List<GrantedAuthority> authorities = user.getRole().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().getValue()))
            .collect(Collectors.toList());

        return UserPrincipal.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .password(user.getPassword())
            .authorities(authorities)
            .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }
}
