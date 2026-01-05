package org.csbf.ecomie.mapper;


import org.csbf.ecomie.entity.SubscriptionEntity;
import org.csbf.ecomie.utils.helperclasses.HelperDomain;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.Challenge;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.Session;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.User;
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
@Mapper(
        componentModel = "spring",
        imports = {
        User.class, Session.class, Challenge.class
}, uses = {
        UserMapper.class, SessionMapper.class, ChallengeMapper.class
})
public interface SubscriptionMapper extends
                               org.csbf.ecomie.utils.commons.Mapper<HelperDomain.Subscription, SubscriptionEntity> {
    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);

    @Mappings({
//        @Mapping(source = "version", target = "revision"),
//        @Mapping(target = "fieldName", expression = "java(UserMapper.toFieldName(entity))"),
    })
    HelperDomain.Subscription asDomainObject(SubscriptionEntity entity);

    @InheritInverseConfiguration
    @Mappings({
//        @Mapping(target = "fieldName", expression = "java(UserMapper.fromFieldName(domainObject))"),
    })
    SubscriptionEntity asEntity(HelperDomain.Subscription domainObject);

    List<HelperDomain.Subscription> asDomainObjects(List<SubscriptionEntity> entities);

    List<SubscriptionEntity> asEntities(List<HelperDomain.Subscription> domainObjects);
}
