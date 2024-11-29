package com.abdul.admin.domain.github.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GithubUserInfo {

    private Long id;
    private boolean hireable;
    private String bio;
    private String company;
    private String blog;
    private String htmlUrl;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String accessToken;
}
