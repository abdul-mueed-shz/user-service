package com.abdul.admin.domain.twitter.port.in;

import com.abdul.admin.domain.twitter.model.XOauthLoginRequest;
import java.io.IOException;

public interface XOAuthUseCase {

    String getRedirectUri(XOauthLoginRequest xOauthLoginRequest) throws IOException;
}
