package com.abdul.admin.domain.twitter.utils;

import com.abdul.admin.config.OauthProperties;
import com.github.scribejava.core.pkce.PKCE;
import com.github.scribejava.core.pkce.PKCECodeChallengeMethod;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Oauth2Helper {

    private final OauthProperties oauthProperties;

    public String getSecretState(String statePostFix) {
        return "secret_" + statePostFix;
    }

    public TwitterOAuth20Service getXOAuthServiceInstance() {
        return getXOAuth20Service(
                getXCredentialsOAuth2(
                        oauthProperties.getRegistration().getX().getClientId(),
                        oauthProperties.getRegistration().getX().getClientSecret(),
                        oauthProperties.getRegistration().getX().getAccessToken()
                ),
                oauthProperties.getRegistration().getX().getRedirectUri(),
                oauthProperties.getRegistration().getX().getScope()
        );
    }

    public TwitterOAuth20Service getXOAuth20Service(
            TwitterCredentialsOAuth2 xCredentialsOAuth2,
            String redirectUri,
            String scopes) {
        return new TwitterOAuth20Service(
                xCredentialsOAuth2.getTwitterOauth2ClientId(),
                xCredentialsOAuth2.getTwitterOAuth2ClientSecret(),
                redirectUri,
                scopes.replace(",", " ")
        );
    }

    public PKCE getProofKeyForCodeExchange(String codeChallenge, String codeVerifier) {
        PKCE pkce = new PKCE();
        pkce.setCodeChallenge(codeChallenge);
        pkce.setCodeChallengeMethod(PKCECodeChallengeMethod.PLAIN);
        pkce.setCodeVerifier(codeVerifier);
        return pkce;
    }

    public TwitterCredentialsOAuth2 getXCredentialsOAuth2(String clientId, String clientSecret, String accessToken) {
        return new TwitterCredentialsOAuth2(clientId, clientSecret, accessToken, "");
    }
}
