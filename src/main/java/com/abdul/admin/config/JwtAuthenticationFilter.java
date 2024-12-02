package com.abdul.admin.config;

import com.abdul.admin.domain.user.port.in.GetUserDetailUseCase;
import com.abdul.toolkit.security.domain.auth.port.in.ExtractJwtClaimsUseCase;
import com.abdul.toolkit.security.domain.auth.validator.JwtTokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final GetUserDetailUseCase getUserDetailUseCase;
    private final ExtractJwtClaimsUseCase extractJwtClaimsUseCase;
    private final JwtTokenValidator jwtTokenValidator;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String userName = null;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        String tokenType = (String) extractJwtClaimsUseCase.extractTokenType(jwt);
        userName = extractJwtClaimsUseCase.extractUsername(jwt);

        if (tokenType.equals("access") && Objects.nonNull(userName) && Objects.isNull(
                SecurityContextHolder.getContext().getAuthentication())) {
            UserDetails userDetails = getUserDetailUseCase.loadUserByUsername(userName);
            if (Objects.nonNull(userDetails) && Boolean.TRUE.equals(jwtTokenValidator.isTokenValid(jwt))) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}