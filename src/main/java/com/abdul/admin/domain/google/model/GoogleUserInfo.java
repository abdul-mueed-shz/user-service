package com.abdul.admin.domain.google.model;

import com.abdul.admin.domain.user.model.UserInfo;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GoogleUserInfo {

    private Long id;
    private String authUserId;
    private String googleId;
    private String picture;
    private UserInfo user;
    private LocalDateTime createdAt;
}
