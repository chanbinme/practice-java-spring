# 전체 인증/인가 흐름 다이어그램
1. 클라이언트 로그인 요청 → AuthController
2. AuthController → AuthenticationManager (인증 처리)
3. AuthenticationManager → UserDetailsService (사용자 정보 조회)
4. UserDetailsService → UserRepository (DB에서 사용자 조회)
5. 인증 성공 시 → JwtTokenProvider (JWT 토큰 생성)
6. 클라이언트에게 JWT 토큰 반환
7. 이후 요청 시 → JwtAuthenticationFilter (토큰 검증)
8. 토큰 유효 시 → SecurityContext에 인증 정보 저장
9. 컨트롤러에서 인가 확인 후 비즈니스 로직 실행