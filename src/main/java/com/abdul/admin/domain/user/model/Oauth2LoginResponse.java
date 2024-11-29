package com.abdul.admin.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Oauth2LoginResponse {

    private String accessToken;
    private String refreshToken;
}
