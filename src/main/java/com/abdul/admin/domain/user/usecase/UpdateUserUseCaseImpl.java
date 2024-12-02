package com.abdul.admin.domain.user.usecase;

import com.abdul.toolkit.utils.user.model.UserInfo;
import com.abdul.admin.domain.user.port.in.UpdateUserUseCase;
import com.abdul.admin.domain.user.port.out.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserRepository userRepository;

    @Override
    public void execute(UserInfo userInfo) {
        userRepository.updateUser(userInfo);
    }
}
