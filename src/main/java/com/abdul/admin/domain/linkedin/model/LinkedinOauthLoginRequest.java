package com.abdul.admin.domain.linkedin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LinkedinOauthLoginRequest {

    private String searchTerm; // username or email
}
