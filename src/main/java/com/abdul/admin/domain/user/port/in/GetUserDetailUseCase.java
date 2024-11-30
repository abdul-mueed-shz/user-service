package com.abdul.admin.domain.user.port.in;

import com.abdul.admin.domain.user.model.UserInfo;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface GetUserDetailUseCase extends UserDetailsService {

    UserInfo get(String searchTerm);
}
