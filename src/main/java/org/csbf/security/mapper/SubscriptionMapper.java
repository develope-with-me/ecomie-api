package org.csbf.security.mapper;


import org.csbf.security.entity.SubscriptionEntity;
import org.csbf.security.utils.helperclasses.HelperDomain.Challenge;
import org.csbf.security.utils.helperclasses.HelperDomain.ChallengeReport;
import org.csbf.security.utils.helperclasses.HelperDomain.Session;
import org.csbf.security.utils.helperclasses.HelperDomain.User;
import org.csbf.security.utils.helperclasses.HelperDomain.Subscription;
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
        User.class, Session.class, Challenge.class, ChallengeReport.class,
}, uses = {
        UserMapper.class, SessionMapper.class, ChallengeMapper.class, ChallengeReport.class
})
public interface SubscriptionMapper extends
                               org.csbf.security.utils.commons.Mapper<Subscription, SubscriptionEntity> {
    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);

    @Mappings({
//        @Mapping(source = "version", target = "revision"),
//        @Mapping(target = "fieldName", expression = "java(UserMapper.toFieldName(entity))"),
    })
    Subscription asDomainObject(SubscriptionEntity entity);

    @InheritInverseConfiguration
    @Mappings({
//        @Mapping(target = "fieldName", expression = "java(UserMapper.fromFieldName(domainObject))"),
    })
    SubscriptionEntity asEntity(Subscription domainObject);

    List<Subscription> asDomainObjects(List<SubscriptionEntity> entities);

    List<SubscriptionEntity> asEntities(List<Subscription> domainObjects);

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
