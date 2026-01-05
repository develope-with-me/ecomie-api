package org.csbf.ecomie.utils.commons;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Entity extends Persistable<UUID> {

//    Integer getVersion();

    LocalDateTime getCreatedOn();

    void setCreatedOn(LocalDateTime createdOn);

    LocalDateTime getUpdatedOn();

    UUID getCreatedBy();

    UUID getUpdatedBy();

//    UUID getOwnerId();

//    default Boolean deleted() {
//        return false;
//    }

    default UUID id() {
        return getId();
    }

//    default Integer version() {
//        return getVersion();
//    }

    default LocalDateTime createdOn() {
        return getCreatedOn();
    }

    default LocalDateTime updatedOn() {
        return getUpdatedOn();
    }

    default UUID createdBy() {
        return getCreatedBy();
    }

    default UUID updatedBy() {
        return getUpdatedBy();
    }

//    default UUID ownerId() {
//        return getOwnerId();
//    }
}
