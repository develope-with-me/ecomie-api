package org.csbf.ecomie.mapper;


import org.apache.commons.lang3.EnumUtils;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.exceptions.Problems;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.User;
import org.csbf.ecomie.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;

/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Mapper(
        componentModel = "spring",
        imports = {
        Objects.class,
//        ImportedObject.class
}, uses = {
//        ImportedMapper.class

})
public interface UserMapper extends
                               org.csbf.ecomie.utils.commons.Mapper<User, UserEntity> {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mappings(
            {
//        @Mapping(source = "version", target = "revision"),
        @Mapping(target = "role", expression = "java(UserMapper.toRole(entity))"),
    }
    )
    User asDomainObject(UserEntity entity);

    @InheritInverseConfiguration
    @Mappings({
        @Mapping(target = "role", expression = "java(UserMapper.fromRole(domainObject))"),
    })
    UserEntity asEntity(User domainObject);

    List<User> asDomainObjects(List<UserEntity> entities);

    List<UserEntity> asEntities(List<User> domainObjects);

    static String toRole(UserEntity entity) {
        return !Objects.isNull(entity.getRole()) ? entity.getRole().name() : null;
    }

    static Role fromRole(User domainObject) {
        if(Objects.isNull(domainObject.role())) {
            return null;
        }
        if (EnumUtils.isValidEnum(Role.class, domainObject.role().toUpperCase())) {
            return Role.valueOf(domainObject.role().toUpperCase());
        }

        throw  Problems.INVALID_PARAMETER_ERROR.withProblemError("role", "Invalid user role (%s)".formatted(domainObject.role())).toException();
    }

}
