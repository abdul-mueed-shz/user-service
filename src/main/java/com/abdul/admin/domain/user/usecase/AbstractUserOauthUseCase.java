package com.abdul.admin.domain.user.usecase;

import com.abdul.admin.domain.user.model.Oauth2LoginResponse;
import com.abdul.admin.domain.user.model.UserInfo;
import com.twitter.clientlib.ApiException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractUserOauthUseCase {

    @Transactional
    public Oauth2LoginResponse execute(String code, String state)
            throws IOException, ExecutionException, InterruptedException, ApiException {
        UserInfo userInfo = getUserByState(state);
        if (Objects.nonNull(userInfo)) {
            executeTokenValidationFlow(code, state, userInfo);
        } else {
            executeAuthCodeFlow(code, state);
        }
        return Oauth2LoginResponse.builder()
                .accessToken("System Generated")
                .refreshToken("System Generated")
                .build();
    }

    protected abstract UserInfo getUserByState(String state);

    protected abstract void executeTokenValidationFlow(String code, String state, UserInfo userInfo)
            throws ApiException, IOException, ExecutionException, InterruptedException;

    protected abstract void executeAuthCodeFlow(String code, String state)
            throws ApiException, IOException, ExecutionException, InterruptedException;


    protected boolean isAccessTokenValid(LocalDateTime createdAt, String expiresInSeconds) {
        LocalDateTime tokenExpiryDateTime = createdAt.plusSeconds(Long.parseLong(expiresInSeconds));
        return !LocalDateTime.now().isAfter(tokenExpiryDateTime);
    }

}
