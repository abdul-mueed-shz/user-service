package com.abdul.admin.domain.google.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GoogleOauthLoginRequest {

    private String searchTerm; // username or email
}
