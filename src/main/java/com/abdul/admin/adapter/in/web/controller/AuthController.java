package com.abdul.admin.adapter.in.web.controller;

import com.abdul.admin.adapter.in.web.mapper.UserDtoMapper;
import com.abdul.admin.domain.enums.UserMessageCodeEnum;
import com.abdul.admin.domain.google.model.GoogleOauthRedirectInfo;
import com.abdul.admin.domain.google.port.in.HandleGoogleOAuthRedirectUseCase;
import com.abdul.admin.domain.user.model.OauthLoginRequest;
import com.abdul.admin.domain.user.port.in.RegisterUserUseCase;
import com.abdul.admin.domain.user.usecase.AbstractGetOAuthUrlUseCase;
import com.abdul.admin.domain.user.usecase.AbstractUserOauthUseCase;
import com.abdul.admin.dto.MessageInfo;
import com.abdul.admin.dto.RegisterUserRequest;
import com.twitter.clientlib.ApiException;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final UserDtoMapper userDtoMapper;
    private final RegisterUserUseCase registerUserUseCase;
    private final HandleGoogleOAuthRedirectUseCase handleGoogleOAuthRedirectUseCase;
    private final ApplicationContext applicationContext;

    @PostMapping("/oauth2/{client}/login")
    public ResponseEntity<String> oauth2Login(
            @PathVariable("client") String client,
            @RequestBody OauthLoginRequest oauthLoginRequest) throws IOException {
        AbstractGetOAuthUrlUseCase abstractGetOAuthUrlUseCase =
                (AbstractGetOAuthUrlUseCase) applicationContext.getBean(client);
        return ResponseEntity.ok(
                abstractGetOAuthUrlUseCase.execute(client, oauthLoginRequest)
        );
    }

    @GetMapping("/oauth2/google/redirect")
    public ResponseEntity<String> handleGoogleOauthRedirect(GoogleOauthRedirectInfo googleOauthRedirectInfo)
            throws IOException {
        String token = handleGoogleOAuthRedirectUseCase.execute(googleOauthRedirectInfo);
        return ResponseEntity.ok(
                token
        );
    }

    @GetMapping("/oauth2/{client}/redirect")
    public ResponseEntity<String> handleOauthRedirect(
            @PathVariable("client") String client,
            @RequestParam(name = "state", required = false) final String state,
            @RequestParam(name = "code", required = false) final String code)
            throws IOException, ExecutionException, InterruptedException, ApiException {
        String beanName = client + "Redirect";
        AbstractUserOauthUseCase handleLinkedinOauthRedirectUseCase =
                (AbstractUserOauthUseCase) applicationContext.getBean(beanName);
        return ResponseEntity.ok(handleLinkedinOauthRedirectUseCase.execute(code, state));
    }

    @PostMapping("/auth/register")
    public ResponseEntity<MessageInfo> registerUser(
            @Valid @RequestBody RegisterUserRequest registerUserRequest) {
        Long userId =
                registerUserUseCase.execute(userDtoMapper.map(registerUserRequest)).getId();
        return ResponseEntity.ok(
                createMessageInfo(userId,
                        UserMessageCodeEnum.USER_REGISTERED_SUCCESSFULLY.getMessageDescription(),
                        UserMessageCodeEnum.USER_REGISTERED_SUCCESSFULLY.getMessageCode())
        );
    }

    private MessageInfo createMessageInfo(Long displayableId, String messageDescription, String messageCode) {
        var messageInfo = new MessageInfo();
        messageInfo.setId(displayableId);
        messageInfo.setDisplayableId(displayableId.toString());
        messageInfo.setDisplayableName(displayableId.toString());
        messageInfo.setMessageDescription(messageDescription);
        messageInfo.setMessageCode(messageCode);
        return messageInfo;
    }
}
