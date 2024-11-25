package com.abdul.admin.adapter.out.persistence.adapter;

import com.abdul.admin.adapter.out.persistence.entity.User;
import com.abdul.admin.adapter.out.persistence.mapper.UserMapper;
import com.abdul.admin.adapter.out.persistence.repository.UserJpaRepository;
import com.abdul.admin.domain.user.model.UserInfo;
import com.abdul.admin.domain.user.model.UserRegistrationRequestInfo;
import com.abdul.admin.domain.user.port.out.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public UserInfo save(UserRegistrationRequestInfo userRegistrationRequestInfo) {
        User user = userJpaRepository.save(userMapper.mapRegistrationInfoToUser(userRegistrationRequestInfo));
        return userMapper.map(user);
    }

    @Override
    public List<UserInfo> findAll() {
        return userMapper.map(userJpaRepository.findAll());
    }

    @Override
    public UserInfo findBySearchTerm(String searchTerm) {
        Optional<User> userOptional = userJpaRepository.findUserByUsernameOrEmail(searchTerm);
        return userOptional.map(userMapper::map).orElse(null);
    }

    @Override
    public UserInfo findByUserLinkedinState(String state) {
        Optional<User> userOptional = userJpaRepository.findUserByLinkedinUser_State(state);
        return userOptional.map(userMapper::map).orElse(null);
    }
}
