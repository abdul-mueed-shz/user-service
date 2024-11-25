package com.abdul.admin.domain.google.port.in;

import com.abdul.admin.domain.google.model.GoogleOauthLoginRequest;

public interface GetGoogleAuthUrlUseCase {

    String execute(GoogleOauthLoginRequest googleOauthLoginRequest);
}
