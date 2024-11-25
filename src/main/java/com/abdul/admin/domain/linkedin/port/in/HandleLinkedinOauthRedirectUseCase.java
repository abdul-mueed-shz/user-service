package com.abdul.admin.domain.linkedin.port.in;

import com.abdul.admin.domain.linkedin.model.LinkedinUserResponse;

public interface HandleLinkedinOauthRedirectUseCase {

    LinkedinUserResponse execute(String code, String state);
}
