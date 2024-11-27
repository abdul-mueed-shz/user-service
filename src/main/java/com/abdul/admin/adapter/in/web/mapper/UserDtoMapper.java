package com.abdul.admin.adapter.in.web.mapper;


import com.abdul.admin.domain.google.model.GoogleUserResponse;
import com.abdul.admin.domain.linkedin.model.AccessToken;
import com.abdul.admin.domain.linkedin.model.LinkedinUserResponse;
import com.abdul.admin.domain.twitter.model.TwitterAccessTokenResponse;
import com.abdul.admin.domain.twitter.model.TwitterUserResponse;
import com.abdul.admin.domain.user.model.UserInfo;
import com.abdul.admin.domain.user.model.UserRegistrationRequestInfo;
import com.abdul.admin.dto.RegisterUserRequest;
import com.abdul.admin.dto.UserResponse;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/*
 * note -> Maps dto objects i.e request/response objects to domain info objects & vice versa
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserDtoMapper {

    @Mapping(source = "state", target = "twitterUser.state")
    @Mapping(source = "authCode", target = "twitterUser.usedAuthCode")
    @Mapping(source = "twitterAccessTokenResponse.accessToken", target = "twitterUser.accessToken")
    @Mapping(source = "twitterAccessTokenResponse.expiresIn", target = "twitterUser.expiresIn")
    @Mapping(source = "twitterAccessTokenResponse.scope", target = "twitterUser.tokenScope")
    @Mapping(source = "twitterAccessTokenResponse.tokenType", target = "twitterUser.tokenType")
    @Mapping(source = "twitterAccessTokenResponse.refreshToken", target = "twitterUser.refreshToken")
    @Mapping(source = "twitterUserResponse.profileImageUrl", target = "twitterUser.picture")
    @Mapping(source = "twitterUserResponse.name", target = "firstName")
    @Mapping(source = "twitterUserResponse.username", target = "username")
    UserRegistrationRequestInfo map(
            TwitterUserResponse twitterUserResponse,
            TwitterAccessTokenResponse twitterAccessTokenResponse,
            String state,
            String authCode);

    @Mapping(source = "state", target = "linkedinUser.state")
    @Mapping(source = "authCode", target = "linkedinUser.usedAuthCode")
    @Mapping(source = "accessToken.token", target = "linkedinUser.accessToken")
    @Mapping(source = "accessToken.idToken", target = "linkedinUser.idToken")
    @Mapping(source = "accessToken.expiresIn", target = "linkedinUser.expiresIn")
    @Mapping(source = "accessToken.scope", target = "linkedinUser.tokenScope")
    @Mapping(source = "accessToken.tokenType", target = "linkedinUser.tokenType")
    @Mapping(source = "linkedinUserResponse.picture", target = "linkedinUser.picture")
    @Mapping(source = "linkedinUserResponse.name", target = "username")
    @Mapping(source = "linkedinUserResponse.familyName", target = "lastName")
    @Mapping(source = "linkedinUserResponse.givenName", target = "firstName")
    UserRegistrationRequestInfo map(
            LinkedinUserResponse linkedinUserResponse,
            AccessToken accessToken,
            String state,
            String authCode);

    @Mapping(source = "googleUserInfo.name", target = "username")
    @Mapping(source = "googleUserInfo.familyName", target = "lastName")
    @Mapping(source = "googleUserInfo.givenName", target = "firstName")
    @Mapping(source = "googleUserInfo.verifiedEmail", target = "emailVerified")
    @Mapping(source = "googleUserInfo.id", target = "googleUser.googleId")
    @Mapping(source = "googleUserInfo.picture", target = "googleUser.picture")
    @Mapping(source = "authUserId", target = "googleUser.authUserId")
    UserRegistrationRequestInfo map(GoogleUserResponse googleUserInfo, String authUserId);

    UserRegistrationRequestInfo map(RegisterUserRequest registerUserRequest);

    List<UserResponse> map(List<UserInfo> userInfoList);

    UserResponse map(UserInfo userInfo);

    default OffsetDateTime mapOffsetDateTime(LocalDateTime value) {
        return value != null ? value.atOffset(ZoneOffset.UTC) : null;
    }
}
