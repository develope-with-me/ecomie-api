package org.csbf.security.utils.commons;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Entity extends Persistable<UUID> {

//    Integer getVersion();

    LocalDateTime getCreatedAt();

    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();

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

    default LocalDateTime createdAt() {
        return getCreatedAt();
    }

    default LocalDateTime updatedAt() {
        return getUpdatedAt();
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
