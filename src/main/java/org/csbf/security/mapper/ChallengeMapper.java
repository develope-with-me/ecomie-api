package org.csbf.security.mapper;


import org.csbf.security.model.ChallengeEntity;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.HelperDto.Challenge;
import org.csbf.security.utils.helperclasses.HelperDto.Session;
import org.csbf.security.utils.helperclasses.HelperDto.Subscription;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Mapper(imports = {
        Session.class, Subscription.class,
}, uses = {
        SessionMapper.class, SubscriptionMapper.class
})
public interface ChallengeMapper extends
                               org.csbf.security.utils.commons.Mapper<Challenge, ChallengeEntity> {
    ChallengeMapper INSTANCE = Mappers.getMapper(ChallengeMapper.class);

    @Mappings({
//        @Mapping(source = "version", target = "revision"),
//        @Mapping(target = "fieldName", expression = "java(UserMapper.toFieldName(entity))"),
    })
    Challenge asDomainObject(ChallengeEntity entity);

    @InheritInverseConfiguration
    @Mappings({
//        @Mapping(target = "fieldName", expression = "java(UserMapper.fromFieldName(domainObject))"),
    })
    ChallengeEntity asEntity(Challenge domainObject);

    List<Challenge> asDomainObjects(List<ChallengeEntity> entities);

    List<ChallengeEntity> asEntities(List<Challenge> domainObjects);

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
