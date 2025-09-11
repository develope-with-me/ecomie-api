package org.csbf.security.mapper;


import org.csbf.security.model.ChallengeReportEntity;
import org.csbf.security.utils.helperclasses.HelperDto.ChallengeReport;
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
        Subscription.class,
}, uses = {
        SubscriptionMapper.class
})
public interface ChallengeReportMapper extends
                               org.csbf.security.utils.commons.Mapper<ChallengeReport, ChallengeReportEntity> {
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
