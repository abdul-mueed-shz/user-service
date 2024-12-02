package com.abdul.admin.domain.user.model;

import com.abdul.toolkit.domain.github.model.GithubUserInfo;
import com.abdul.toolkit.domain.google.model.GoogleUserInfo;
import com.abdul.toolkit.domain.linkedin.model.LinkedinUserInfo;
import com.abdul.toolkit.domain.twitter.model.TwitterUserInfo;
import com.abdul.toolkit.domain.user.model.RoleInfo;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private Long id;
    private String username;
    private String email;
    private Set<RoleInfo> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private Boolean isSystemLock;
    private Boolean areCredentialsValid;
    private String firstName;
    private String lastName;
    private Boolean emailVerified = Boolean.FALSE;
    private GoogleUserInfo googleUser;
    private LinkedinUserInfo linkedinUser;
    private TwitterUserInfo twitterUser;
    private GithubUserInfo githubUser;
}
