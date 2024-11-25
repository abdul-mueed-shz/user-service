package com.abdul.admin.adapter.in.web.controller;

import com.abdul.admin.adapter.in.web.mapper.UserDtoMapper;
import com.abdul.admin.domain.enums.UserMessageCodeEnum;
import com.abdul.admin.domain.google.model.GoogleOauthLoginRequest;
import com.abdul.admin.domain.google.model.GoogleOauthRedirectInfo;
import com.abdul.admin.domain.google.port.in.GetGoogleAuthUrlUseCase;
import com.abdul.admin.domain.google.port.in.HandleOAuthRedirectUseCase;
import com.abdul.admin.domain.linkedin.model.LinkedinOauthLoginRequest;
import com.abdul.admin.domain.linkedin.model.LinkedinUserResponse;
import com.abdul.admin.domain.linkedin.port.in.HandleLinkedinOauthRedirectUseCase;
import com.abdul.admin.domain.linkedin.port.in.LinkedInOAuthUseCase;
import com.abdul.admin.domain.user.port.in.RegisterUserUseCase;
import com.abdul.admin.dto.MessageInfo;
import com.abdul.admin.dto.RegisterUserRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final GetGoogleAuthUrlUseCase getGoogleAuthUrlUseCase;
    private final HandleOAuthRedirectUseCase handleOAuthRedirectUseCase;
    private final LinkedInOAuthUseCase linkedInOAuthUseCase;
    private final HandleLinkedinOauthRedirectUseCase handleLinkedinOauthRedirectUseCase;

    @RequestMapping(value = "/oauth2/linkedin/login")
    public ResponseEntity<String> loginWithLinkedin(@RequestBody LinkedinOauthLoginRequest linkedinOauthLoginRequest) {
        return ResponseEntity.ok(
                linkedInOAuthUseCase.execute(linkedinOauthLoginRequest)
        );
    }

    @PostMapping("/oauth2/google/login")
    public ResponseEntity<String> loginWithGoogle(@RequestBody GoogleOauthLoginRequest googleOauthLoginRequest) {
        return ResponseEntity.ok(
                getGoogleAuthUrlUseCase.execute(googleOauthLoginRequest)
        );
    }

    @GetMapping("/oauth2/google/redirect")
    public ResponseEntity<String> loginWithGoogle(GoogleOauthRedirectInfo googleOauthRedirectInfo) throws IOException {
        String token = handleOAuthRedirectUseCase.execute(googleOauthRedirectInfo);
        return ResponseEntity.ok(
                token
        );
    }

    @GetMapping("/oauth2/linkedin/redirect")
    public ResponseEntity<LinkedinUserResponse> loginWithLinkedin(
            @RequestParam(name = "state", required = false) final String state,
            @RequestParam(name = "code", required = false) final String code) {
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
