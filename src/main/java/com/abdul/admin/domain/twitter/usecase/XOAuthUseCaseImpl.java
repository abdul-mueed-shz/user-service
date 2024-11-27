package com.abdul.admin.domain.twitter.usecase;

import com.abdul.admin.config.OauthProperties;
import com.abdul.admin.domain.twitter.model.XOauthLoginRequest;
import com.abdul.admin.domain.twitter.port.in.XOAuthUseCase;
import com.abdul.admin.domain.twitter.utils.Oauth2Helper;
import com.abdul.admin.domain.user.port.out.repository.UserRepository;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class XOAuthUseCaseImpl implements XOAuthUseCase {

    private final UserRepository userRepository;
    private final OauthProperties oauthProperties;
    private final Oauth2Helper oauth2Helper;

    @Override
    public String getRedirectUri(XOauthLoginRequest xOauthLoginRequest) throws IOException {
        if (Objects.nonNull(userRepository.findBySearchTerm(xOauthLoginRequest.getSearchTerm()))) {
            // throw Error. user is already registered in the system. login with email/username
            return null;
        }
        try (TwitterOAuth20Service xoAuth20Service = oauth2Helper.getXOAuthServiceInstance()) {
            return xoAuth20Service.getAuthorizationUrl(
                    oauth2Helper.getProofKeyForCodeExchange(
                            oauthProperties.getRegistration().getX().getPkceCodeChallenge(),
                            oauthProperties.getRegistration().getX().getPkceCodeVerifier()),
                    oauth2Helper.getSecretState(xOauthLoginRequest.getSearchTerm())
            );
        }
    }
}
