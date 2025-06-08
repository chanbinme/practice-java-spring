package com.chanbinme.springsecurityjwt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleName {

    USER("ROLE_USER"), // 일반 사용자 역할
    ADMIN("ROLE_ADMIN"); // 관리자 역할

    private final String value;
}
