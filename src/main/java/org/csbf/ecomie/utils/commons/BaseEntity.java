package org.csbf.ecomie.utils.commons;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.csbf.ecomie.entity.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class BaseEntity implements Entity {
    private static final UUID ANONYMOUS_USER_ID = UUID.fromString("019a2841-513e-7b99-b042-5564a99b9ae6");

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
    var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!principal.toString().equals("anonymousUser")) {
        return ((UserEntity) principal).id();
    }
    return ANONYMOUS_USER_ID;
}

    //    @Column(name = "deleted")
//    private boolean deleted;

//    @Version
//    @Builder.Default()
//    private Integer version = 0;


//    @Column(name = "owner_id")
//    private UUID ownerId;


//    @Override
//    public Boolean deleted() {
//        return this.deleted;
//    }
}
