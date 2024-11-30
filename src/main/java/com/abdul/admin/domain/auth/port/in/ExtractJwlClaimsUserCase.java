package com.abdul.admin.domain.auth.port.in;

import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface ExtractJwlClaimsUserCase {

    Object extractTokenType(String token);

    String extractUsername(String token);

    Date extractExpiration(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsTFunction);

    Claims extractAllClaims(String token);

    Map<String, Object> extractHeaders(String token);
}
