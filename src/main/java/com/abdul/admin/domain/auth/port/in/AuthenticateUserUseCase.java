package com.abdul.admin.domain.auth.port.in;

import com.abdul.admin.domain.auth.model.AuthenticationInfo;
import com.abdul.admin.domain.auth.model.AuthenticationRequestInfo;

public interface AuthenticateUserUseCase {

    AuthenticationInfo authenticate(AuthenticationRequestInfo authenticationRequestInfo);
}
