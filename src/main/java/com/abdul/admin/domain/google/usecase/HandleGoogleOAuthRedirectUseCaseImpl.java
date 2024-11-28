package com.abdul.admin.domain.google.usecase;

import com.abdul.admin.adapter.in.web.mapper.UserDtoMapper;
import com.abdul.admin.domain.google.model.GoogleOauthRedirectInfo;
import com.abdul.admin.domain.google.model.GoogleUserResponse;
import com.abdul.admin.domain.google.port.in.GetGoogleOAuthRedirectUriUseCase;
import com.abdul.admin.domain.google.port.in.GetUserProfileUseCase;
import com.abdul.admin.domain.google.port.in.HandleGoogleOAuthRedirectUseCase;
import com.abdul.admin.domain.user.mapper.UserInfoMapper;
import com.abdul.admin.domain.user.model.UserInfo;
import com.abdul.admin.domain.user.port.in.RegisterUserUseCase;
import com.abdul.admin.domain.user.port.in.UpdateUserUseCase;
import com.abdul.admin.domain.user.port.out.repository.UserRepository;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class HandleGoogleOAuthRedirectUseCaseImpl implements HandleGoogleOAuthRedirectUseCase {

    private final AuthorizationCodeFlow authorizationCodeFlow;
    private final GetGoogleOAuthRedirectUriUseCase getGoogleOAuthRedirectUriUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UserDtoMapper userDtoMapper;
    private final RegisterUserUseCase registerUserUseCase;
    private final UserRepository userRepository;
    private final UpdateUserUseCase updateUserUseCase;
    private final UserInfoMapper userInfoMapper;

    @Value("${spring.application.client-app-home-url}")
    private String clientAppHomeUrl;

    @Override
    public String execute(GoogleOauthRedirectInfo googleOauthRedirectInfo) throws IOException {
        Credential credential = authorizationCodeFlow.loadCredential(googleOauthRedirectInfo.getAuthuser());
        UserInfo userInfo = userRepository.findByGoogleAuthUser(googleOauthRedirectInfo.getAuthuser());
        if (Objects.nonNull(credential) && Objects.nonNull(userInfo)) {
            executeAccessTokenFlow(userInfo, credential, googleOauthRedirectInfo.getCode(),
                    googleOauthRedirectInfo.getAuthuser());
        } else {
            executeAuthorizationCodeFlow(googleOauthRedirectInfo.getCode(), googleOauthRedirectInfo.getAuthuser());
        }
        return UriComponentsBuilder.fromHttpUrl(clientAppHomeUrl).queryParam("token", "System Generated Token")
                .toUriString();
    }

    private void executeAccessTokenFlow(UserInfo userInfo, Credential credential, String code, String authUser)
            throws IOException {
        if (isAccessTokenValid(userInfo, credential) || credential.refreshToken()) {
            return;
        }
        executeAuthorizationCodeFlow(code, authUser);
    }

    private void executeAuthorizationCodeFlow(String code, String authUser) throws IOException {
        AuthorizationCodeTokenRequest tokenRequest = authorizationCodeFlow.newTokenRequest(code)
                .setRedirectUri(getGoogleOAuthRedirectUriUseCase.execute());
        TokenResponse tokenResponse = tokenRequest.execute();
        authorizationCodeFlow.createAndStoreCredential(tokenResponse, authUser);
        GoogleUserResponse googleUserResponse = getUserProfileUseCase.execute(authUser);
        UserInfo userInfo = userRepository.findByUsernameOrEmail(googleUserResponse.getEmail());
        if (Objects.nonNull(userInfo)) {
            updateUserUseCase.execute(userInfoMapper.map(userInfo, googleUserResponse, authUser));
            return;
        }
        registerUserUseCase.execute(userDtoMapper.map(googleUserResponse, authUser));
    }

    protected boolean isAccessTokenValid(UserInfo userInfo, Credential credential) {
        LocalDateTime tokenExpiryDateTime = userInfo.getGoogleUser().getCreatedAt()
                .plusSeconds(credential.getExpiresInSeconds());
        return !LocalDateTime.now().isAfter(tokenExpiryDateTime);
    }
}
