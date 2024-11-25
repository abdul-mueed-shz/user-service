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
import com.github.scribejava.core.pkce.PKCE;
import com.github.scribejava.core.pkce.PKCECodeChallengeMethod;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
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

    @PostMapping(value = "/oauth2/twitter/login")
    public ResponseEntity<String> loginWithTwitter(@RequestBody LinkedinOauthLoginRequest linkedinOauthLoginRequest) {

        return ResponseEntity.ok("authorizationUrl");
    }


    @PostMapping(value = "/oauth2/linkedin/login")
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

    @GetMapping("/oauth2/twitter/redirect")
    public ResponseEntity<String> handleLoginWithTwitter(GoogleOauthRedirectInfo googleOauthRedirectInfo)
            throws IOException, ExecutionException, InterruptedException {
        TwitterCredentialsOAuth2 credentials = new TwitterCredentialsOAuth2(
                "dm90TmdTaUJVV3FtejRTby1kdGs6MTpjaQ",
                "g_zuEHeauS_krevgHNcaj7hTPiMJP2qhePSlIX1EoL_6oxldfz",
                "1535567896297996290-3q0TGQVxkrTwm2YlOocph6IDDGL1O1",
                "");
        TwitterOAuth20Service service = new TwitterOAuth20Service(
                credentials.getTwitterOauth2ClientId(),
                credentials.getTwitterOAuth2ClientSecret(),
                "http://127.0.0.1:8081/api/v1/oauth2/twitter/redirect",
                "offline.access tweet.read users.read");
        final Scanner in = new Scanner(System.in, "UTF-8");
        System.out.println("Fetching the Authorization URL...");

        final String secretState = "state";
        PKCE pkce = new PKCE();
        pkce.setCodeChallenge("challenge");
        pkce.setCodeChallengeMethod(PKCECodeChallengeMethod.PLAIN);
        pkce.setCodeVerifier("challenge");
        var accessToken = service.getAccessToken(pkce, googleOauthRedirectInfo.getCode());
        System.out.println("Access token: " + accessToken.getAccessToken());
        System.out.println("Refresh token: " + accessToken.getRefreshToken());
        return ResponseEntity.ok(
                accessToken.getAccessToken()
        );
    }

    @GetMapping("/oauth2/google/redirect")
    public ResponseEntity<String> handleLoginWithGoogle(GoogleOauthRedirectInfo googleOauthRedirectInfo)
            throws IOException {
        String token = handleOAuthRedirectUseCase.execute(googleOauthRedirectInfo);
        return ResponseEntity.ok(
                token
        );
    }

    @GetMapping("/oauth2/linkedin/redirect")
    public ResponseEntity<LinkedinUserResponse> handleLoginWithLinkedin(
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
