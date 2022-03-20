package org.greatstillness.persistence.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @PrePersist
    void preCreate() {
        Instant now = Instant.now();
        setCreatedDate(now);
        setLastModifiedDate(now);
    }

    @PreUpdate
    void preUpdate() {
        Instant now = Instant.now();
        setLastModifiedDate(now);
    }
}