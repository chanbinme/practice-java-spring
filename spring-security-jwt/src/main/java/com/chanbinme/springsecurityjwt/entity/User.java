package com.chanbinme.springsecurityjwt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Entity
@Table(name = "users",
    uniqueConstraints = {
        @jakarta.persistence.UniqueConstraint(columnNames = {"username", "email"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 ID
    private String username; // 사용자 이름
    private String email; // 이메일
    private String password; // 암호화된 비밀번호
    private Boolean enabled; // 계정 활성화 여부
    private Boolean accountNonLocked = true; // 계정 잠금 여부
    private Boolean accountNonExpired = true; // 계정 만료 여부
    private Boolean credentialsNonExpired = true; // 비밀번호 만료 여부
    @CreationTimestamp
    private LocalDateTime createdAt; // 계정 생성 시간
    @UpdateTimestamp
    private LocalDateTime updatedAt; // 계정 수정 시간

    @ManyToMany(fetch = jakarta.persistence.FetchType.EAGER)
    @JoinTable(name = "user_roles",
        joinColumns = @jakarta.persistence.JoinColumn(name = "user_id"),
        inverseJoinColumns = @jakarta.persistence.JoinColumn(name = "role_id"))
    private Set<Role> role = new HashSet<>();  // 사용자 권한 목록

    @Builder
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = true; // 기본적으로 계정 활성화
    }
}
