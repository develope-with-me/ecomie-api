package org.csbf.ecomie.mapper;


import org.apache.commons.lang3.EnumUtils;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.constant.SessionStatus;
import org.csbf.ecomie.entity.SessionEntity;
import org.csbf.ecomie.exceptions.Problems;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
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
        Challenge.class
}, uses = {
        ChallengeMapper.class
})
public interface SessionMapper extends
                               org.csbf.ecomie.utils.commons.Mapper<Session, SessionEntity> {
    SessionMapper INSTANCE = Mappers.getMapper(SessionMapper.class);

    @Mappings({
//        @Mapping(source = "version", target = "revision"),
        @Mapping(target = "status", expression = "java(SessionMapper.toStatus(entity))"),
    })
    Session asDomainObject(SessionEntity entity);

    @InheritInverseConfiguration
    @Mappings({
        @Mapping(target = "status", expression = "java(SessionMapper.fromStatus(domainObject))"),
    })
    SessionEntity asEntity(Session domainObject);

    List<Session> asDomainObjects(List<SessionEntity> entities);

    List<SessionEntity> asEntities(List<Session> domainObjects);


    static String toStatus(SessionEntity entity) {
        return !Objects.isNull(entity.getStatus()) ? entity.getStatus().name() : null;
    }

    static SessionStatus fromStatus(Session domainObject) {
        if(Objects.isNull(domainObject.status())) {
            return null;
        }
        if (EnumUtils.isValidEnum(SessionStatus.class, domainObject.status().toUpperCase())) {
            return SessionStatus.valueOf(domainObject.status().toUpperCase());
        }

        throw  Problems.INVALID_PARAMETER_ERROR.withProblemError("status", "Invalid session status (%s)".formatted(domainObject.status())).toException();
    }

//    static String toChallenges(SessionEntity entity) {
//        return entity.getStatus().name();
//    }
//
//    static Role fromChallenges(Session domainObject) {
//        if (EnumUtils.isValidEnum(Role.class, domainObject.status().toUpperCase())) {
//            return Role.valueOf(domainObject.status().toUpperCase());
//        }
//
//        throw  Problems.INVALID_PARAMETER_ERROR.withProblemError("status", "Invalid session status (%s)".formatted(domainObject.status())).toException();
//    }

}
