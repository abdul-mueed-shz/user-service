package com.abdul.admin.domain.user.port.out.repository;

import com.abdul.admin.domain.user.model.UserInfo;
import com.abdul.admin.domain.user.model.UserRegistrationRequestInfo;
import java.util.List;

public interface UserRepository {

    UserInfo save(UserRegistrationRequestInfo sampleInfo);

    List<UserInfo> findAll();

    UserInfo findBySearchTerm(String searchTerm); // Username or Email for now

    UserInfo findByUserLinkedinState(String state);
}
