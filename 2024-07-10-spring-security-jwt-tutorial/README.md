# Spring Security Authorization Server 만들기
>로그인 인증 인가를 구현하였습니다.

#### Domain
User (int userNumber, String password,String userName)
Role (USER)

#### 기능 설명
BCryptPasswordEncoder (제대로 사용x)

Spring Security Config (filterChain, webSecurityCustomizer)

cors Config

JwtTokenProvider

JwtAuthenticationFiler (핵심)

#### 구현 설명
Spring Security 실행 사이클을 생각하며
구현을 진행하였습니다.

JwtAuthenticationFilter 에서 토큰의 유효성을 검사하고, 아니면 NullPointerException을 던지며, null을 받은 handler는 Access 거부를 반환합니다.

발급을 받기위해서 회원가입, 로그인 route만 허용해주었습니다.

로그인을 검증하기 위해서 
