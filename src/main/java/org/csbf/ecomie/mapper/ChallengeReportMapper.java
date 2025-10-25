package org.csbf.ecomie.mapper;


import org.csbf.ecomie.entity.ChallengeReportEntity;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
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
        User.class, Subscription.class,
}, uses = {
        UserMapper.class, SubscriptionMapper.class
})
public interface ChallengeReportMapper extends
                               org.csbf.ecomie.utils.commons.Mapper<ChallengeReport, ChallengeReportEntity> {
    ChallengeReportMapper INSTANCE = Mappers.getMapper(ChallengeReportMapper.class);

    @Mappings({
//        @Mapping(source = "version", target = "revision"),
//        @Mapping(target = "fieldName", expression = "java(UserMapper.toFieldName(entity))"),
    })
    ChallengeReport asDomainObject(ChallengeReportEntity entity);

    @InheritInverseConfiguration
    @Mappings({
//        @Mapping(target = "fieldName", expression = "java(UserMapper.fromFieldName(domainObject))"),
    })
    ChallengeReportEntity asEntity(ChallengeReport domainObject);

    List<ChallengeReport> asDomainObjects(List<ChallengeReportEntity> entities);

    List<ChallengeReportEntity> asEntities(List<ChallengeReport> domainObjects);

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
