package com.abdul.admin.domain.google.port.in;

import com.abdul.admin.domain.google.model.GoogleOauthRedirectInfo;
import java.io.IOException;

public interface HandleGoogleOAuthRedirectUseCase {

    String execute(GoogleOauthRedirectInfo googleOauthRedirectInfo) throws IOException;
}
