package com.abdul.admin.domain.linkedin.usecase;

import com.abdul.admin.config.OauthProperties;
import com.abdul.admin.domain.linkedin.model.LinkedinOauthLoginRequest;
import com.abdul.admin.domain.linkedin.port.in.LinkedInOAuthUseCase;
import com.abdul.admin.domain.twitter.utils.Oauth2Helper;
import com.abdul.admin.domain.user.port.out.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Objects;
import java.util.Random;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Service
@RequiredArgsConstructor
public class LinkedInOAuthUseCaseImpl implements LinkedInOAuthUseCase {

    private final UserRepository userRepository;
    private final OauthProperties oauthProperties;
    private final Oauth2Helper oauth2Helper;

    private final Random random = new Random();

    @Transactional
    public String execute(LinkedinOauthLoginRequest linkedinOauthLoginRequest) {
        if (Objects.nonNull(userRepository.findByUsernameOrEmail(linkedinOauthLoginRequest.getSearchTerm()))) {
            // throw Error. user is already registered in the system. login with email/username
            return null;
        }

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String scopes = String.join("+", oauthProperties.getRegistration().getLinkedin().getScope());

        return UriComponentsBuilder.fromUriString(oauthProperties.getProvider().getLinkedin().getAuthorizationUri())
                .queryParam("response_type",
                        oauthProperties.getRegistration().getLinkedin().getAuthorizationGrantType())
                .queryParam("client_id", oauthProperties.getRegistration().getLinkedin().getClientId())
                .queryParam("redirect_uri",
                        oauthProperties.getRegistration().getLinkedin().getRedirectUri().replace("{baseUrl}", baseUrl))
                .queryParam("state", oauth2Helper.getSecretState(linkedinOauthLoginRequest.getSearchTerm()))
                .queryParam("scope", scopes).encode()
                .toUriString();
    }
}