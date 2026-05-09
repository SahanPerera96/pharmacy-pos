package com.pharmacy.pos.modules.user;

import com.pharmacy.pos.common.base.BaseEntity;
import com.pharmacy.pos.common.rbac.Role;
import com.pharmacy.pos.modules.branch.Branch;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

/**
 * User Entity
 *
 * Represents a staff member of the pharmacy system.
 * Each user belongs to exactly one branch (or no branch for OWNER/ADMIN).
 * Role determines what the user can see and do across the system.
 */
@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true),
        @Index(name = "idx_users_branch_id", columnList = "branch_id"),
        @Index(name = "idx_users_role", columnList = "role")
    }
)
@Audited                    // Hibernate Envers: auto-tracks every change to a users_audit_log table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    /**
     * Email is the login username. Must be globally unique.
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * BCrypt-hashed password. Never store or log plaintext.
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * The user's role determines permissions via RolePermissionRegistry.
     * Stored as STRING for readability in DB.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    /**
     * Branch this user belongs to.
     * NULL for OWNER and ADMIN roles (they operate across all branches).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    /**
     * Locks out the account without deleting it.
     * Use this for suspended employees.
     */
    @Column(name = "is_locked", nullable = false)
    @Builder.Default
    private Boolean isLocked = false;

    /**
     * Forces password change on next login.
     * Set to true when an admin creates/resets a user's password.
     */
    @Column(name = "must_change_password", nullable = false)
    @Builder.Default
    private Boolean mustChangePassword = false;

    // ── Convenience methods ──────────────────────────────────────────

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAccountAvailable() {
        return Boolean.TRUE.equals(getIsActive()) && !Boolean.TRUE.equals(isLocked);
    }

    public Long getBranchId() {
        return branch != null ? branch.getId() : null;
    }
}
