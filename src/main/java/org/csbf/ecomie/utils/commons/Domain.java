package org.csbf.ecomie.utils.commons;

import org.csbf.ecomie.entity.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public interface Domain extends Thing {
    UUID userId = ((UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).id();

    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddhhmmssSSSXXX");

    default LocalDateTime createdAt() {
        return LocalDateTime.now();
    }

    default LocalDateTime updatedAt() {
        return LocalDateTime.now();
    }

    default UUID createdBy(){return  userId;}

    default UUID updatedBy(){return userId;}


//    static String calculateHash(Integer revision, LocalDateTime createdOn,
//                                LocalDateTime updatedOn) {
//        var createdDate = Objects.nonNull(createdOn) ? createdOn.format(FORMATTER) : createdOn;
//        var updatedDate = Objects.nonNull(updatedOn) ? updatedOn.format(FORMATTER) : updatedOn;
//        return Password.hash(("%s:%s:%s".formatted(revision, createdDate, updatedDate)))
//                .withBcrypt()
//                .getResult();

//    }

//    default Integer revision() {
//        return 0;
//    }



//    UUID ownerId();

//    String hash();
}