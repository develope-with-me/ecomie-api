package org.csbf.security.mapper;


import org.csbf.security.model.SessionEntity;
import org.csbf.security.utils.helperclasses.HelperDto.Session;
import org.csbf.security.utils.helperclasses.HelperDto.Challenge;
import org.csbf.security.utils.helperclasses.HelperDto.Subscription;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Mapper(imports = {
        Challenge.class, Subscription.class,
}, uses = {
        ChallengeMapper.class, SubscriptionMapper.class
})
public interface SessionMapper extends
                               org.csbf.security.utils.commons.Mapper<Session, SessionEntity> {
    SessionMapper INSTANCE = Mappers.getMapper(SessionMapper.class);

    @Mappings({
//        @Mapping(source = "version", target = "revision"),
//        @Mapping(target = "fieldName", expression = "java(UserMapper.toFieldName(entity))"),
    })
    Session asDomainObject(SessionEntity entity);

    @InheritInverseConfiguration
    @Mappings({
//        @Mapping(target = "fieldName", expression = "java(UserMapper.fromFieldName(domainObject))"),
    })
    SessionEntity asEntity(Session domainObject);

    List<Session> asDomainObjects(List<SessionEntity> entities);

    List<SessionEntity> asEntities(List<Session> domainObjects);

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
