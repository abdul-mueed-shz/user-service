package com.abdul.admin.domain.twitter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class XOauthLoginRequest {

    private String searchTerm;
}
