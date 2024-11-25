package com.abdul.admin.domain.linkedin.usecase;

import com.abdul.admin.adapter.in.web.mapper.UserDtoMapper;
import com.abdul.admin.adapter.out.web.LinkedinAuthFeignClient;
import com.abdul.admin.adapter.out.web.LinkedinUserInfoFeignClient;
import com.abdul.admin.config.OauthProperties;
import com.abdul.admin.domain.linkedin.model.AccessToken;
import com.abdul.admin.domain.linkedin.model.LinkedinUserResponse;
import com.abdul.admin.domain.linkedin.port.in.HandleLinkedinOauthRedirectUseCase;
import com.abdul.admin.domain.user.model.UserInfo;
import com.abdul.admin.domain.user.model.UserRegistrationRequestInfo;
import com.abdul.admin.domain.user.port.out.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Repository
@RequiredArgsConstructor
public class HandleLinkedinOauthRedirectUseCaseImpl implements HandleLinkedinOauthRedirectUseCase {

    private final LinkedinAuthFeignClient linkedinAuthFeignClient;
    private final LinkedinUserInfoFeignClient linkedinUserInfoFeignClient;
    private final OauthProperties oauthProperties;
    private final UserDtoMapper userDtoMapper;
    private final UserRepository userRepository;

    public LinkedinUserResponse execute(String code, String state) {
        // Check if saved user's linkedin info contains this state, if yes check expiry of token
        // If token ain't expired. Return new system generated token.
        // If expired fetch new token, get updated user info, update already stored info and return system token
        // If linkedin info doesn't exist Register the user. Return system generated token.
        // You've skipped this already registered user check for google oauth fix that as well.
        UserInfo userInfo = validateSessionState(state);
        if (Objects.nonNull(userInfo)) {
            return linkedinUserInfoFeignClient.getUserInfo(userInfo.getLinkedinUser().getAccessToken());
        }

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        AccessToken accessToken = linkedinAuthFeignClient.getAccessToken(
                oauthProperties.getRegistration().getLinkedin().getTokenGrantTypeParam(),
                code,
                oauthProperties.getRegistration().getLinkedin().getRedirectUri().replace("{baseUrl}", baseUrl),
                oauthProperties.getRegistration().getLinkedin().getClientId(),
                oauthProperties.getRegistration().getLinkedin().getClientSecret()
        );
        LinkedinUserResponse linkedinUserResponse = linkedinUserInfoFeignClient.getUserInfo(accessToken.getToken());
        UserRegistrationRequestInfo userRegistrationRequestInfo =
                userDtoMapper.map(linkedinUserResponse, accessToken, state, code);
        userRepository.save(userRegistrationRequestInfo);
        return linkedinUserResponse;
    }

    private UserInfo validateSessionState(String state) {
        UserInfo userInfo = userRepository.findByUserLinkedinState(state);
        if (Objects.isNull(userInfo)) {
            return null;
        }
        LocalDateTime tokenExpiryDateTime = userInfo.getLinkedinUser()
                .getCreatedAt()
                .plusSeconds(
                        Long.parseLong(userInfo.getLinkedinUser().getExpiresIn())
                );
        return LocalDateTime.now().isBefore(tokenExpiryDateTime) || LocalDateTime.now().isEqual(tokenExpiryDateTime)
                ? userInfo
                : null;
    }
}
