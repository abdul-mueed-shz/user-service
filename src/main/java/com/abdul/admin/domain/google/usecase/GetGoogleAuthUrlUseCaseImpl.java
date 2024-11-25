package com.abdul.admin.domain.google.usecase;

import com.abdul.admin.domain.google.model.GoogleOauthLoginRequest;
import com.abdul.admin.domain.google.port.in.GetGoogleAuthUrlUseCase;
import com.abdul.admin.domain.google.port.in.GetGoogleOAuthRedirectUriUseCase;
import com.abdul.admin.domain.user.port.out.repository.UserRepository;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetGoogleAuthUrlUseCaseImpl implements GetGoogleAuthUrlUseCase {

    private final UserRepository userRepository;
    private final GetGoogleOAuthRedirectUriUseCase getRedirectUriUseCaseImpl;
    private final AuthorizationCodeFlow authorizationCodeFlow;

    public String execute(GoogleOauthLoginRequest googleOauthLoginRequest) {
        if (Objects.nonNull(userRepository.findBySearchTerm(googleOauthLoginRequest.getSearchTerm()))) {
            // throw Error. user is already registered in the system. login with email/username
            return null;
        }
        return authorizationCodeFlow.newAuthorizationUrl()
                .setRedirectUri(getRedirectUriUseCaseImpl.execute())
                .build();
    }

}
