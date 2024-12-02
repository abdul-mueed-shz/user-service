package com.abdul.admin.domain.user.model;

import com.abdul.admin.adapter.out.persistence.entity.Permission;
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
public class RoleInfo {

    private Long id;

    private String name;

    private Set<Permission> permissions;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
