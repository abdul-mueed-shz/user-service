package com.abdul.admin.domain.user.model;

import com.abdul.admin.adapter.out.persistence.entity.Role;
import com.abdul.admin.domain.github.model.GithubUserInfo;
import com.abdul.admin.domain.google.model.GoogleUserInfo;
import com.abdul.admin.domain.linkedin.model.LinkedinUserInfo;
import com.abdul.admin.domain.twitter.model.TwitterUserInfo;
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
    private Set<Role> roles;
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
