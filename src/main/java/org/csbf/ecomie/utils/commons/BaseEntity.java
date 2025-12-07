package org.csbf.ecomie.utils.commons;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.csbf.ecomie.config.EntityConfigParams;
import org.csbf.ecomie.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class BaseEntity implements Entity {

    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_on", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (id == null) {
            id = UUID.randomUUID();
        }

        if (createdAt == null) {
            createdAt = now;
        }
        if (createdBy == null) {
            createdBy = getOwnerId();
        }
        updatedAt = now;
        updatedBy = getOwnerId();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        updatedBy = getOwnerId();
    }


public UUID getOwnerId() {
    if( Objects.nonNull(SecurityContextHolder.getContext().getAuthentication())) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.toString().equals("anonymousUser")) {
            return ((UserEntity) principal).id();
        }
    }
    return EntityConfigParams.getAnonymousUserId();
}
}
