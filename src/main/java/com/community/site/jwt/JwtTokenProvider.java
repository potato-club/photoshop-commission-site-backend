package com.community.site.jwt;

import com.community.site.Repository.UserRepository;
import com.community.site.error.ErrorCode;
import com.community.site.error.exception.ForbiddenException;
import com.community.site.service.Jwt.CustomUserDetailService;
import com.community.site.service.Jwt.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserRepository userRepository;
    private final RedisService redisService;

    // 키
    @Value("${jwt.secret}")
    private String secretKey;

    // 액세스 토큰 유효시간 | 1h
    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenValidTime;
    // 리프레시 토큰 유효시간 | 7d
    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenValidTime;

    private final CustomUserDetailService customUserDetailService;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct // 의존성 주입 후, 초기화를 수행
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // Access Token 생성.
    @Transactional
    public String createAccessToken(String email, List<String> roles) {
        return this.createToken(email, roles, accessTokenValidTime);
    }
    // Refresh Token 생성.
    @Transactional
    public String createRefreshToken(String email, List<String> roles) {
        return this.createToken(email, roles, refreshTokenValidTime);
    }

    // Create token
    @Transactional
    public String createToken(String email, List<String> roles, long tokenValid) {
        Claims claims = Jwts.claims().setSubject(email); // claims 생성 및 payload 설정
        claims.put("roles", roles); // 권한 설정, key/ value 쌍으로 저장

        Date date = new Date();
        return Jwts.builder()
                .setClaims(claims) // 발행 유저 정보 저장
                .setIssuedAt(date) // 발행 시간 저장
                .setExpiration(new Date(date.getTime() + tokenValid)) // 토큰 유효 시간 저장
                .signWith(SignatureAlgorithm.HS256, secretKey) // 해싱 알고리즘 및 키 설정
                .compact(); // 생성
    }

    // JWT 토큰에서 인증 정보 조회
    @Transactional
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(this.getUserEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    @Transactional
    public String getUserEmail(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    @Transactional
    public String reissueAccessToken(String refreshToken) {
        String email = redisService.getValues(refreshToken);
        if (Objects.isNull(email)) {
            throw new ForbiddenException("E0003", ErrorCode.FORBIDDEN_EXCEPTION);
        }

        List<String> roles = userRepository.findByEmail(email).get().getRoles();
        String accessToken = createAccessToken(email, roles);

        return accessToken;
    }

    // Request의 Header에서 AccessToken 값을 가져옵니다. "authorization" : "token"
    @Transactional
    public String resolveAccessToken(HttpServletRequest request) {
        if(request.getHeader("authorization") != null )
            return request.getHeader("authorization").substring(7);
        return null;
    }
    // Request의 Header에서 RefreshToken 값을 가져옵니다. "refreshToken" : "token"
    @Transactional
    public String resolveRefreshToken(HttpServletRequest request) {
        if(request.getHeader("refreshToken") != null )
            return request.getHeader("refreshToken").substring(7);
        return null;
    }

    // 토큰의 유효성 + 만료일자 확인
    @Transactional
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("JWT signature does not match: {}", e.getMessage());
        }

        return false;
    }

    // 어세스 토큰 헤더 설정
    @Transactional
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("authorization", "bearer "+ accessToken);
    }

    // 리프레시 토큰 헤더 설정
    @Transactional
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader("refreshToken", "bearer "+ refreshToken);
    }

    // RefreshToken 존재유무 확인
    @Transactional
    public boolean existsRefreshToken(String refreshToken) {
        return redisService.getValues(refreshToken) != null;
    }

    // Email로 권한 정보 가져오기
    @Transactional
    public List<String> getRoles(String email) {
        return userRepository.findByEmail(email).get().getRoles();
    }
}
