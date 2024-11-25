package com.abdul.admin.domain.google.usecase;

import com.abdul.admin.adapter.in.web.mapper.UserDtoMapper;
import com.abdul.admin.domain.google.model.GoogleOauthRedirectInfo;
import com.abdul.admin.domain.google.model.GoogleUserResponse;
import com.abdul.admin.domain.google.port.in.GetGoogleOAuthRedirectUriUseCase;
import com.abdul.admin.domain.google.port.in.GetUserProfileUseCase;
import com.abdul.admin.domain.google.port.in.HandleOAuthRedirectUseCase;
import com.abdul.admin.domain.user.port.in.RegisterUserUseCase;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class HandleOAuthRedirectUseCaseImpl implements HandleOAuthRedirectUseCase {

    private final AuthorizationCodeFlow authorizationCodeFlow;
    private final GetGoogleOAuthRedirectUriUseCase getGoogleOAuthRedirectUriUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UserDtoMapper userDtoMapper;
    private final RegisterUserUseCase registerUserUseCase;

    @Value("${spring.application.client-app-home-url}")
    private String clientAppHomeUrl;

    @Override
    public String execute(GoogleOauthRedirectInfo googleOauthRedirectInfo) throws IOException {
        Credential credential = authorizationCodeFlow.loadCredential(googleOauthRedirectInfo.getAuthuser());
        if (Objects.nonNull(credential)) {
            // Check if token expired using expired in seconds and createdAi
            // If not return system gen token
            // If yes call refresh token to fetch new access token
            // If found return system generated token, else let upcoming flow execute.
            return credential.getAccessToken();
        }

        AuthorizationCodeTokenRequest tokenRequest = authorizationCodeFlow.newTokenRequest(
                googleOauthRedirectInfo.getCode()).setRedirectUri(getGoogleOAuthRedirectUriUseCase.execute());

        TokenResponse tokenResponse = tokenRequest.execute();
        authorizationCodeFlow.createAndStoreCredential(tokenResponse, googleOauthRedirectInfo.getAuthuser());

        GoogleUserResponse googleUserResponse = getUserProfileUseCase.execute(googleOauthRedirectInfo.getAuthuser());
        registerUserUseCase.execute(userDtoMapper.map(googleUserResponse, googleOauthRedirectInfo.getAuthuser()));
        /*
         * TODO:
         * Check if user with same google id & authUserId exists. If yes fetch it, else register.
         * Create user-admin apps jwt token using the user-details from the above step.
         * (b)Return the JWT token to FE using Spring Socket or (b)from this function.
         *       a - Return FE's home page url from this function with Params(if required).
         *           Show loader on FE until socket's response for JWT
         *       b - Return to FE's home page url from this function with token in query params.
         */
        return UriComponentsBuilder.fromHttpUrl(clientAppHomeUrl)
                .queryParam("token", tokenResponse.getAccessToken())
                .toUriString();
    }
}
