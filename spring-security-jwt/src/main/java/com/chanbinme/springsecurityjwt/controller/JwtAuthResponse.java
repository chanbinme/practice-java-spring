package com.chanbinme.springsecurityjwt.controller;

import lombok.Builder;

public record JwtAuthResponse(String accessToken,
                              String refreshToken,
                              String tokenType,
                              Long accessTokenExpiresIn,
                              Long refreshTokenExpiresIn) {

    public JwtAuthResponse(String accessToken) {
        this(accessToken, null, "Bearer", null, null);
    }

    public JwtAuthResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "Bearer", null, null);
    }
}
