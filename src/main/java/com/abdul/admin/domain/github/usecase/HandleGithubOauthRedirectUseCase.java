package com.abdul.admin.domain.github.usecase;

import com.abdul.admin.adapter.out.web.GitHubClient;
import com.abdul.admin.config.OauthProperties;
import com.abdul.admin.domain.twitter.utils.Oauth2Helper;
import com.abdul.admin.domain.user.model.AccessToken;
import com.abdul.admin.domain.user.model.UserInfo;
import com.abdul.admin.domain.user.usecase.AbstractUserOauthUseCase;
import com.twitter.clientlib.ApiException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Service;

@Service("githubRedirect")
public class HandleGithubOauthRedirectUseCase extends AbstractUserOauthUseCase {

    private final OauthProperties oauthProperties;
    private final Oauth2Helper oauth2Helper;
    private final GitHubClient gitHubClient;

    public HandleGithubOauthRedirectUseCase(
            OauthProperties oauthProperties,
            Oauth2Helper oauth2Helper,
            GitHubClient gitHubClient
    ) {
        this.oauthProperties = oauthProperties;
        this.oauth2Helper = oauth2Helper;
        this.gitHubClient = gitHubClient;
    }

    /**
     * @param state
     * @return
     */
    @Override
    protected UserInfo getUserByState(String state) {
        return null;
    }

    /**
     * @param code
     * @param state
     * @param userInfo
     * @throws ApiException
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    protected void executeTokenValidationFlow(String code, String state, UserInfo userInfo)
            throws ApiException, IOException, ExecutionException, InterruptedException {

    }

    /**
     * @param code
     * @param state
     */
    @Override
    protected void executeAuthCodeFlow(String code, String state)
            throws IOException, ExecutionException, InterruptedException {
        AccessToken accessToken = gitHubClient.fetchAccessToken(code);
    }
}
