package com.abdul.admin.domain.auth.usecase;

import com.abdul.admin.adapter.out.persistence.mapper.UserMapper;
import com.abdul.admin.domain.auth.port.in.GenerateJwtTokenUseCase;
import com.abdul.admin.domain.auth.port.in.GetSignInKeyUseCase;
import com.abdul.admin.domain.user.model.UserInfo;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenerateJwtTokenUseCaseImpl implements GenerateJwtTokenUseCase {

    @Value("${app.jwt.token-validity}")
    private Integer tokenValidity;

    private final UserMapper userMapper;
    private final GetSignInKeyUseCase getSignInKeyUseCase;


    public String generateAccessToken(UserInfo userInfo) {
        Map<String, Object> headers = Map.of(
                "alg", "HS256",
                "typ", "JWT",
                "appVersion", "1.0.0",
                "tokenType", "access"
        );
        return generateToken(userInfo, new HashMap<>(), headers, tokenValidity);
    }

    public String generateRefreshToken(UserInfo userInfo) {
        Map<String, Object> headers = Map.of(
                "alg", "HS256",
                "typ", "JWT",
                "appVersion", "1.0.0",
                "tokenType", "refresh"
        );
        return generateToken(
                userInfo,
                new HashMap<>(),
                headers,
                tokenValidity * 30 // Make this configurable for refresh token
        );
    }

    public String generateToken(
            UserInfo userInfo,
            Map<String, Object> claims,
            Map<String, Object> headers,
            Integer tokenValidity) {
        return Jwts.builder()
                .header().add(headers)
                .and()
                .subject(userInfo.getUsername())
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenValidity))
                .signWith(getSignInKeyUseCase.get())
                .compact();
    }
}
