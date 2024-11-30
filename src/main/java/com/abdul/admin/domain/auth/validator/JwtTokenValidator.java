package com.abdul.admin.domain.auth.validator;

import com.abdul.admin.domain.auth.port.in.ExtractJwlClaimsUserCase;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenValidator {

    private final ExtractJwlClaimsUserCase extractJwlClaimsUserCase;

    public Boolean isTokenValid(String token) {
        return extractJwlClaimsUserCase.extractExpiration(token).after(new Date());
    }

}
