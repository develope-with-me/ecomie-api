package org.csbf.security.mapper;


import org.csbf.security.utils.helperclasses.HelperDomain.User;
import org.csbf.security.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;

/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Mapper(imports = {
        Objects.class,
//        ImportedObject.class
}, uses = {
//        ImportedMapper.class

})
public interface UserMapper extends
                               org.csbf.security.utils.commons.Mapper<User, UserEntity> {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mappings({
//        @Mapping(source = "version", target = "revision"),
//        @Mapping(target = "fieldName", expression = "java(UserMapper.toFieldName(entity))"),
    })
    User asDomainObject(UserEntity entity);

    @InheritInverseConfiguration
    @Mappings({
//        @Mapping(target = "fieldName", expression = "java(UserMapper.fromFieldName(domainObject))"),
    })
    UserEntity asEntity(User domainObject);

    List<User> asDomainObjects(List<UserEntity> entities);

    List<UserEntity> asEntities(List<User> domainObjects);

//    static domainFieldType toFieldName(UserEntity entity) {
//        logic here
//        return null;
//    }
//
//    static entityFieldType fromFieldName(User User) {
//        logic here
//        return null;
//    }

}
