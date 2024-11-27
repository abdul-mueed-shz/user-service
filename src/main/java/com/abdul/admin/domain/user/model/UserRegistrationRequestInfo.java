package com.abdul.admin.domain.user.model;

import com.abdul.admin.adapter.out.persistence.entity.Role;
import com.abdul.admin.domain.google.model.GoogleUserInfo;
import com.abdul.admin.domain.linkedin.model.LinkedinUserInfo;
import com.abdul.admin.domain.twitter.model.TwitterUserInfo;
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
    private Set<Role> roles;
    private GoogleUserInfo googleUser;
    private LinkedinUserInfo linkedinUser;
    private TwitterUserInfo twitterUser;
    private Boolean emailVerified;
}
