package com.abdul.admin.domain.user.port.in;

import com.abdul.admin.domain.user.model.UserInfo;

public interface GetUserDetailUseCase {

    UserInfo get(String searchTerm);
}
