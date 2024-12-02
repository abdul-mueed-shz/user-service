package com.abdul.admin.domain.user.model;


import com.abdul.toolkit.domain.github.model.GithubUserInfo;
import com.abdul.toolkit.domain.google.model.GoogleUserInfo;
import com.abdul.toolkit.domain.linkedin.model.LinkedinUserInfo;
import com.abdul.toolkit.domain.twitter.model.TwitterUserInfo;
import com.abdul.toolkit.domain.user.model.RoleInfo;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequestInfo {

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Set<RoleInfo> roles;
    private GoogleUserInfo googleUser;
    private LinkedinUserInfo linkedinUser;
    private TwitterUserInfo twitterUser;
    private GithubUserInfo githubUser;
    private Boolean emailVerified;
}
