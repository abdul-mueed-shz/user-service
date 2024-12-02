package com.abdul.admin.domain.twitter.usecase;

import com.abdul.admin.adapter.in.web.mapper.UserDtoMapper;
import com.abdul.admin.config.OauthProperties;
import com.abdul.admin.domain.twitter.mapper.TwitterInfoMapper;
import com.abdul.admin.domain.twitter.model.TwitterAccessTokenResponse;
import com.abdul.admin.domain.twitter.model.TwitterUserResponse;
import com.abdul.admin.domain.twitter.utils.Oauth2Helper;
import com.abdul.admin.domain.user.mapper.UserInfoMapper;
import com.abdul.toolkit.utils.user.model.UserInfo;
import com.abdul.admin.domain.user.model.UserRegistrationRequestInfo;
import com.abdul.admin.domain.user.port.in.GetUserDetailUseCase;
import com.abdul.admin.domain.user.port.in.RegisterUserUseCase;
import com.abdul.admin.domain.user.port.in.UpdateUserUseCase;
import com.abdul.admin.domain.user.port.out.repository.UserRepository;
import com.abdul.admin.domain.user.usecase.AbstractUserOauthUseCase;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import com.twitter.clientlib.model.Get2UsersMeResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Service;

@Service("twitterRedirect")
@RequiredArgsConstructor
public class HandleXRedirectUseCase extends AbstractUserOauthUseCase {

    private final Oauth2Helper oauth2Helper;
    private final OauthProperties oauthProperties;
    private final UserRepository userRepository;
    private final TwitterInfoMapper twitterInfoMapper;
    private final UserDtoMapper userDtoMapper;
    private final UserInfoMapper userInfoMapper;
    private final RegisterUserUseCase registerUserUseCase;
    private final GetUserDetailUseCase getUserDetailUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final TwitterOAuth20Service twitterOAuth20Service;

    @Override
    protected void executeTokenValidationFlow(String currentCode, String currentState, UserInfo userInfo)
            throws ApiException, IOException, ExecutionException, InterruptedException {
        TwitterUserResponse twitterUserResponse;
        String authCode = null;
        String state = null;
        TwitterAccessTokenResponse twitterAccessTokenResponse = null;
        if (isAccessTokenValid(userInfo.getTwitterUser().getCreatedAt(), userInfo.getTwitterUser().getExpiresIn())) {
            twitterUserResponse = fetchTwitterUser(userInfo.getTwitterUser().getAccessToken(),
                    userInfo.getTwitterUser().getRefreshToken());
        } else {
            ImmutableTriple<TwitterAccessTokenResponse, TwitterUserResponse, Boolean> tokenAndUserInfoTuple = refreshAndPersistTwitterUserInfo(
                    userInfo, currentCode);
            twitterAccessTokenResponse = tokenAndUserInfoTuple.getLeft();
            twitterUserResponse = tokenAndUserInfoTuple.getMiddle();
            boolean isAuthCodeUsed = tokenAndUserInfoTuple.getRight();
            if (isAuthCodeUsed) {
                authCode = currentCode;
                state = currentState;
            }
        }
        UserInfo updatedUserInfo = userInfoMapper.map(userInfo, twitterAccessTokenResponse, twitterUserResponse,
                authCode, state);
        updateUserUseCase.execute(updatedUserInfo);
    }

    @Override
    protected void executeAuthCodeFlow(String code, String state)
            throws ApiException, IOException, ExecutionException, InterruptedException {
        TwitterAccessTokenResponse twitterAccessTokenResponse = getAccessTokenByAuthCode(code);
        TwitterUserResponse twitterUserResponse = fetchTwitterUser(twitterAccessTokenResponse.getAccessToken(),
                twitterAccessTokenResponse.getRefreshToken());
        UserInfo userInfo = getUserDetailUseCase.get(twitterUserResponse.getUsername());
        if (Objects.nonNull(userInfo)) {
            updateUserUseCase.execute(
                    userInfoMapper.map(userInfo, twitterAccessTokenResponse, twitterUserResponse, code, state));
            return;
        }
        UserRegistrationRequestInfo userRegistrationRequestInfo = userDtoMapper.map(twitterUserResponse,
                twitterAccessTokenResponse, state, code);
        registerUserUseCase.execute(userRegistrationRequestInfo);
    }

    @Override
    protected UserInfo getUserByState(String state) {
        return userRepository.findByUserTwitterState(state);
    }

    private TwitterUserResponse fetchTwitterUser(String accessToken, String refreshToken) throws ApiException {
        TwitterApi apiInstance = new TwitterApi(
                new TwitterCredentialsOAuth2(oauthProperties.getRegistration().getX().getClientId(),
                        oauthProperties.getRegistration().getX().getClientSecret(), accessToken, refreshToken));

        Set<String> userFields = new HashSet<>(
                Arrays.asList("id", "name", "profile_image_url", "username", "verified", "withheld"));

        Get2UsersMeResponse get2UsersMeResponse = apiInstance.users().findMyUser().userFields(userFields).execute();

        return twitterInfoMapper.map(get2UsersMeResponse);
    }

    // Move this to abstract class as well.
    private TwitterAccessTokenResponse getAccessTokenByAuthCode(String code)
            throws IOException, ExecutionException, InterruptedException {
        OAuth2AccessToken oAuth2AccessToken = twitterOAuth20Service.getAccessToken(
                oauth2Helper.getProofKeyForCodeExchange(oauthProperties.getRegistration().getX().getPkceCodeChallenge(),
                        oauthProperties.getRegistration().getX().getPkceCodeVerifier()), code);
        return twitterInfoMapper.map(oAuth2AccessToken);
    }

    private ImmutableTriple<TwitterAccessTokenResponse, TwitterUserResponse, Boolean> refreshAndPersistTwitterUserInfo(
            UserInfo userInfo, String code) throws ApiException, IOException, ExecutionException, InterruptedException {
        TwitterAccessTokenResponse twitterAccessTokenResponse;
        boolean isAuthCodeUsed = false;
        OAuth2AccessToken oAuth2AccessToken = twitterOAuth20Service.refreshAccessToken(
                userInfo.getTwitterUser().getRefreshToken());
        if (Objects.isNull(oAuth2AccessToken)) {
            twitterAccessTokenResponse = getAccessTokenByAuthCode(code);
        } else {
            twitterAccessTokenResponse = twitterInfoMapper.map(oAuth2AccessToken);
            isAuthCodeUsed = true;
        }
        TwitterUserResponse twitterUserResponse = fetchTwitterUser(twitterAccessTokenResponse.getAccessToken(),
                twitterAccessTokenResponse.getRefreshToken());
        return ImmutableTriple.of(twitterAccessTokenResponse, twitterUserResponse, isAuthCodeUsed);
    }
}
