package com.abdul.admin.adapter.out.persistence.mapper;

import com.abdul.admin.adapter.out.persistence.entity.LinkedinUser;
import com.abdul.admin.domain.linkedin.model.LinkedinUserInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LinkedinUserMapper {

    LinkedinUserInfo map(LinkedinUser linkedinUser);
}
