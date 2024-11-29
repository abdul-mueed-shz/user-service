package com.abdul.admin.domain.github.usecase;

import com.abdul.admin.adapter.in.web.mapper.UserDtoMapper;
import com.abdul.admin.adapter.out.web.GitHubClient;
import com.abdul.admin.config.OauthProperties;
import com.abdul.admin.domain.github.model.GithubUserResponse;
import com.abdul.admin.domain.twitter.utils.Oauth2Helper;
import com.abdul.admin.domain.user.mapper.UserInfoMapper;
import com.abdul.admin.domain.user.model.AccessToken;
import com.abdul.admin.domain.user.model.UserInfo;
import com.abdul.admin.domain.user.model.UserRegistrationRequestInfo;
import com.abdul.admin.domain.user.port.in.GetUserDetailUseCase;
import com.abdul.admin.domain.user.port.in.RegisterUserUseCase;
import com.abdul.admin.domain.user.port.in.UpdateUserUseCase;
import com.abdul.admin.domain.user.usecase.AbstractUserOauthUseCase;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("githubRedirect")
@RequiredArgsConstructor
public class HandleGithubOauthRedirectUseCase extends AbstractUserOauthUseCase {

    private final OauthProperties oauthProperties;
    private final Oauth2Helper oauth2Helper;
    private final GitHubClient gitHubClient;
    private final GetUserDetailUseCase getUserDetailUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final UserInfoMapper userInfoMapper;
    private final UserDtoMapper userDtoMapper;

    /**
     * @param state
     * @return
     */
    @Override
    protected UserInfo getUserByState(String state) {
        // Because state is always null for GITHUB. Proceed with null response.
        return null;
    }

    /**
     * @param code
     * @param state
     * @param userInfo
     */
    @Override
    protected void executeTokenValidationFlow(String code, String state, UserInfo userInfo) {
        // Because state is always null for GITHUB. Proceed with auth code flow.
        executeAuthCodeFlow(code, state);
    }

    /**
     * @param code
     * @param state
     */
    @Override
    protected void executeAuthCodeFlow(String code, String state) {
        AccessToken accessToken = gitHubClient.fetchAccessToken(code);
        GithubUserResponse githubUserResponse = gitHubClient.getUserProfile(accessToken);
        UserInfo userInfo = getUserDetailUseCase.get(githubUserResponse.getEmail());
        if (Objects.nonNull(userInfo)) {
            UserInfo updatedUserInfo =
                    userInfoMapper.map(
                            userInfo,
                            githubUserResponse,
                            accessToken
                    );
            updateUserUseCase.execute(updatedUserInfo);
            return;
        }
        UserRegistrationRequestInfo userRegistrationRequestInfo =
                userDtoMapper.map(githubUserResponse, accessToken);
        registerUserUseCase.execute(userRegistrationRequestInfo);
    }
}
