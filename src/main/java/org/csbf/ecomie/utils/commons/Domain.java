package org.csbf.ecomie.utils.commons;

import org.csbf.ecomie.entity.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Logger;

public interface Domain extends Thing {
//    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddhhmmssSSSXXX");

//    default LocalDateTime createdAt() {
//        return LocalDateTime.now();
//    }
//
//    default LocalDateTime updatedAt() {
//        return LocalDateTime.now();
//    }
//
//    default UUID createdBy(){return  getUserId();}
//
//    default UUID updatedBy(){return getUserId();}
//
//default UUID getUserId() {
//    var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    if (!principal.getClass().isInstance(String.class)) {
//        return ((UserEntity) principal).id();
//    }
//    return null;
//}

}