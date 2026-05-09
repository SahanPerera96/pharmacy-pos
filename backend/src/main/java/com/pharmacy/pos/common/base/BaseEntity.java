package com.pharmacy.pos.common.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * BaseEntity
 *
 * Parent class for every JPA entity in the system.
 * Provides:
 *   - Auto-generated primary key (IDENTITY strategy, works with PostgreSQL SERIAL)
 *   - createdAt / updatedAt timestamps via Spring Data JPA auditing
 *   - createdBy / updatedBy user tracking via AuditorAware (see JpaConfig)
 *
 * Usage: @Entity public class Product extends BaseEntity { ... }
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Timestamp when the record was first persisted.
     * Set automatically by Spring Data — never update manually.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of the most recent update.
     * Updated automatically on every merge/save.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Username (email) of the user who created this record.
     * Populated via AuditorAware<String> bean defined in JpaConfig.
     */
    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 100)
    private String createdBy;

    /**
     * Username (email) of the user who last modified this record.
     */
    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    /**
     * Soft-delete flag.
     * Use isActive = false instead of DELETE to preserve audit history.
     * All repository queries should filter by isActive = true by default.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
