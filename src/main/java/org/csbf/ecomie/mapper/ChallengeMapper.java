package org.csbf.ecomie.mapper;


import org.csbf.ecomie.entity.ChallengeEntity;
import org.csbf.ecomie.entity.SessionEntity;
import org.csbf.ecomie.exceptions.Problems;
import org.csbf.ecomie.repository.RepositoryFactory;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.Challenge;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.Session;
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
@Mapper(
        componentModel = "spring"
//        imports = {
//        Session.class
//}, uses = {
//        SessionMapper.class
//}
)
public interface ChallengeMapper extends
                               org.csbf.ecomie.utils.commons.Mapper<Challenge, ChallengeEntity> {
    ChallengeMapper INSTANCE = Mappers.getMapper(ChallengeMapper.class);

    @Mappings({
        @Mapping(target = "sessions", expression = "java(ChallengeMapper.mapOnlySessionIdsAndName(entity))")
    })
    Challenge asDomainObject(ChallengeEntity entity);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "sessions", expression = "java(ChallengeMapper.mapEntireSessionEntities(domainObject))")
    })
    ChallengeEntity asEntity(Challenge domainObject);

    List<Challenge> asDomainObjects(List<ChallengeEntity> entities);

    List<ChallengeEntity> asEntities(List<Challenge> domainObjects);

    static List<Session> mapOnlySessionIdsAndName(ChallengeEntity entity) {
        if (entity.getSessions().isEmpty()) {
            return null;
        }
        return entity.getSessions().stream()
                .map(sessionEntity -> Session.builder()
                        .id(sessionEntity.getId())
                        .name(sessionEntity.getName())
                        .description(sessionEntity.getDescription())
                        .status(sessionEntity.getStatus().name())
                        .build()).toList();
    }

    static List<SessionEntity> mapEntireSessionEntities(Challenge domainObject) {
        if (domainObject.sessions().isEmpty()) {
            return null;
        }
        return domainObject.sessions().stream()
                .map(session -> RepositoryFactory.getSessionRepository()
                        .findById(session.id())
                        .orElseThrow(() -> Problems.NOT_FOUND.withProblemError("session",
                                "session with id (%s) not found".formatted(session.id().toString()))
                                .toException()))
                .toList();
    }
}
