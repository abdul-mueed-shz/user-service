package com.abdul.admin.domain.twitter.usecase;

import com.abdul.admin.adapter.in.web.mapper.UserDtoMapper;
import com.abdul.admin.config.OauthProperties;
import com.abdul.admin.domain.twitter.mapper.TwitterInfoMapper;
import com.abdul.admin.domain.twitter.model.TwitterAccessTokenResponse;
import com.abdul.admin.domain.twitter.model.TwitterUserResponse;
import com.abdul.admin.domain.twitter.model.XOauthRedirectInfo;
import com.abdul.admin.domain.twitter.port.in.HandleXRedirectUseCase;
import com.abdul.admin.domain.twitter.utils.Oauth2Helper;
import com.abdul.admin.domain.user.mapper.UserInfoMapper;
import com.abdul.admin.domain.user.model.UserInfo;
import com.abdul.admin.domain.user.model.UserRegistrationRequestInfo;
import com.abdul.admin.domain.user.port.out.repository.UserRepository;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import com.twitter.clientlib.model.Get2UsersMeResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HandleXRedirectUseCaseImpl implements HandleXRedirectUseCase {

    private final Oauth2Helper oauth2Helper;
    private final OauthProperties oauthProperties;
    private final UserRepository userRepository;
    private final TwitterInfoMapper twitterInfoMapper;
    private final UserDtoMapper userDtoMapper;
    private final UserInfoMapper userInfoMapper;

    @Transactional
    @Override
    public String execute(XOauthRedirectInfo xOauthRedirectInfo)
            throws IOException, InterruptedException, ExecutionException, ApiException {
        try (TwitterOAuth20Service twitterOAuth20Service = oauth2Helper.getXOAuthServiceInstance()) {
            UserInfo userInfo = userRepository.findByUserTwitterState(xOauthRedirectInfo.getState());
            if (Objects.nonNull(userInfo)) {
                handleUserLogin(
                        xOauthRedirectInfo.getCode(),
                        xOauthRedirectInfo.getState(),
                        userInfo,
                        twitterOAuth20Service);
            } else {
                handleUserRegistration(
                        twitterOAuth20Service,
                        xOauthRedirectInfo.getCode(),
                        xOauthRedirectInfo.getState());
            }
        }
        return "Return System Token.";
    }

    private void handleUserLogin(
            String currentAuthCode,
            String currentSecretState,
            UserInfo userInfo,
            TwitterOAuth20Service twitterOAuth20Service)
            throws ApiException, IOException, ExecutionException, InterruptedException {
        TwitterUserResponse twitterUserResponse;
        String authCode = null;
        String state = null;
        TwitterAccessTokenResponse twitterAccessTokenResponse = null;
        if (isAccessTokenValid(userInfo)) {
            twitterUserResponse = fetchTwitterUser(
                    userInfo.getTwitterUser().getAccessToken(),
                    userInfo.getTwitterUser().getRefreshToken()
            );
        } else {
            ImmutableTriple<TwitterAccessTokenResponse, TwitterUserResponse, Boolean> tokenAndUserInfoTuple =
                    refreshAndPersistTwitterUserInfo(twitterOAuth20Service, userInfo, currentAuthCode,
                            currentSecretState);
            twitterAccessTokenResponse = tokenAndUserInfoTuple.getLeft();
            twitterUserResponse = tokenAndUserInfoTuple.getMiddle();
            boolean isAuthCodeUsed = tokenAndUserInfoTuple.getRight();
            if (isAuthCodeUsed) {
                authCode = currentAuthCode;
                state = currentSecretState;
            }
        }
        UserInfo updatedUserInfo =
                userInfoMapper.map(userInfo, twitterAccessTokenResponse, twitterUserResponse, authCode, state);
        userRepository.updateUser(updatedUserInfo);
    }

    private ImmutableTriple<TwitterAccessTokenResponse, TwitterUserResponse, Boolean> refreshAndPersistTwitterUserInfo(
            TwitterOAuth20Service twitterOAuth20Service, UserInfo userInfo, String authCode, String state)
            throws ApiException, IOException, ExecutionException, InterruptedException {
        TwitterAccessTokenResponse twitterAccessTokenResponse;
        boolean isAuthCodeUsed = false;

        OAuth2AccessToken oAuth2AccessToken = twitterOAuth20Service.refreshAccessToken(
                userInfo.getTwitterUser().getRefreshToken()
        );
        if (Objects.isNull(oAuth2AccessToken)) {
            twitterAccessTokenResponse = fetchTwitterAccessTokenByAuthCode(twitterOAuth20Service, authCode);
        } else {
            twitterAccessTokenResponse = twitterInfoMapper.map(oAuth2AccessToken);
            isAuthCodeUsed = true;
        }
        TwitterUserResponse twitterUserResponse = fetchTwitterUser(
                twitterAccessTokenResponse.getAccessToken(),
                twitterAccessTokenResponse.getRefreshToken()
        );
        // Return token and user info
        return ImmutableTriple.of(twitterAccessTokenResponse, twitterUserResponse, isAuthCodeUsed);
    }

    private void handleUserRegistration(TwitterOAuth20Service twitterOAuth20Service, String authCode, String state)
            throws ApiException, IOException, ExecutionException, InterruptedException {
        TwitterAccessTokenResponse twitterAccessTokenResponse = fetchTwitterAccessTokenByAuthCode(twitterOAuth20Service,
                authCode);
        TwitterUserResponse twitterUserResponse = fetchTwitterUser(
                twitterAccessTokenResponse.getAccessToken(),
                twitterAccessTokenResponse.getRefreshToken()
        );
        // Map and save user registration info
        UserRegistrationRequestInfo userRegistrationRequestInfo =
                userDtoMapper.map(
                        twitterUserResponse,
                        twitterAccessTokenResponse,
                        state,
                        authCode
                );
        userRepository.save(userRegistrationRequestInfo);
    }

    private boolean isAccessTokenValid(UserInfo userInfo) {
        LocalDateTime tokenExpiryDateTime = userInfo.getTwitterUser()
                .getCreatedAt()
                .plusSeconds(
                        Long.parseLong(userInfo.getTwitterUser().getExpiresIn())
                );
        return !LocalDateTime.now().isAfter(tokenExpiryDateTime);
    }

    private TwitterUserResponse fetchTwitterUser(String accessToken, String refreshToken) throws ApiException {
        TwitterApi apiInstance = new TwitterApi(new TwitterCredentialsOAuth2(
                oauthProperties.getRegistration().getX().getClientId(),
                oauthProperties.getRegistration().getX().getClientSecret(),
                accessToken,
                refreshToken
        ));

        Set<String> userFields = new HashSet<>(Arrays.asList(
                "id", "name", "profile_image_url", "username", "verified", "withheld"
        ));

        Get2UsersMeResponse get2UsersMeResponse = apiInstance.users()
                .findMyUser()
                .userFields(userFields)
                .execute();

        return twitterInfoMapper.map(get2UsersMeResponse);
    }

    private TwitterAccessTokenResponse fetchTwitterAccessTokenByAuthCode(
            TwitterOAuth20Service twitterOAuth20Service, String authCode)
            throws IOException, ExecutionException, InterruptedException {
        OAuth2AccessToken oAuth2AccessToken = twitterOAuth20Service.getAccessToken(
                oauth2Helper.getProofKeyForCodeExchange(
                        oauthProperties.getRegistration().getX().getPkceCodeChallenge(),
                        oauthProperties.getRegistration().getX().getPkceCodeVerifier()
                ),
                authCode
        );
        return twitterInfoMapper.map(oAuth2AccessToken);
    }

}
