package org.csbf.ecomie.mapper;


import org.apache.commons.lang3.EnumUtils;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.constant.TokenType;
import org.csbf.ecomie.entity.SubscriptionEntity;
import org.csbf.ecomie.entity.UserEntity;
import org.csbf.ecomie.entity.UserTokenEntity;
import org.csbf.ecomie.exceptions.Problems;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.User;
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
        User.class
}, uses = {
        UserMapper.class
})
public interface UserTokenMapper extends
                               org.csbf.ecomie.utils.commons.Mapper<UserToken, UserTokenEntity> {
    UserTokenMapper INSTANCE = Mappers.getMapper(UserTokenMapper.class);

    @Mappings({
//        @Mapping(source = "version", target = "revision"),
        @Mapping(target = "type", expression = "java(UserTokenMapper.toType(entity))"),
    })
    UserToken asDomainObject(UserTokenEntity entity);

    @InheritInverseConfiguration
    @Mappings({
        @Mapping(target = "type", expression = "java(UserTokenMapper.fromType(domainObject))"),
    })
    UserTokenEntity asEntity(UserToken domainObject);

    List<UserToken> asDomainObjects(List<UserTokenEntity> entities);

    List<UserTokenEntity> asEntities(List<UserToken> domainObjects);

    static String toType(UserTokenEntity entity) {
        return !Objects.isNull(entity.getType()) ? entity.getType().name() : null;
    }

    static TokenType fromType(UserToken domainObject) {
        if(Objects.isNull(domainObject.type())) {
            return null;
        }
        if (EnumUtils.isValidEnum(TokenType.class, domainObject.type().toUpperCase())) {
            return TokenType.valueOf(domainObject.type().toUpperCase());
        }

        throw  Problems.INVALID_PARAMETER_ERROR.withProblemError("type", "Invalid userToken.type (%s)".formatted(domainObject.type())).toException();
    }

}
