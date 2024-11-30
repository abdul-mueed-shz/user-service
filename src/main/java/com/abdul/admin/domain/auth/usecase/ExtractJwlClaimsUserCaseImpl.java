package com.abdul.admin.domain.auth.usecase;

import com.abdul.admin.domain.auth.port.in.ExtractJwlClaimsUserCase;
import com.abdul.admin.domain.auth.port.in.GetSignInKeyUseCase;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExtractJwlClaimsUserCaseImpl implements ExtractJwlClaimsUserCase {

    private final GetSignInKeyUseCase getSignInKeyUseCase;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Object extractTokenType(String token) {
        return extractHeader(token, (Map<String, Object> headers) -> headers.get("tokenType"));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    public <T> T extractHeader(String token, Function<Map<String, Object>, T> headersTFunction) {
        final Map<String, Object> headers = extractHeaders(token);
        return headersTFunction.apply(headers);
    }

    public Map<String, Object> extractHeaders(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKeyUseCase.get()) // Replace with your signing key
                .build()
                .parse(token)
                .getHeader();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKeyUseCase.get())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
