package com.abdul.admin.domain.linkedin.port.in;

import com.abdul.admin.domain.linkedin.model.LinkedinOauthLoginRequest;

public interface LinkedInOAuthUseCase {

    String execute(LinkedinOauthLoginRequest linkedinOauthLoginRequest);
}
