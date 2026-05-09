package com.pharmacy.pos.common.security;

import com.pharmacy.pos.common.rbac.Permission;
import com.pharmacy.pos.common.rbac.Role;
import com.pharmacy.pos.common.rbac.RolePermissionRegistry;
import com.pharmacy.pos.modules.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * UserPrincipal
 *
 * Adapts our User entity to Spring Security's UserDetails interface.
 * Stored in the SecurityContext for the duration of a request.
 *
 * Authorities are built from the user's role via RolePermissionRegistry:
 *   - "ROLE_MANAGER"       ← for hasRole() checks
 *   - "SALE_CREATE"        ← for hasAuthority() / @PreAuthorize checks
 */
@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private final Long branchId;
    private final boolean accountAvailable;
    private final Collection<? extends GrantedAuthority> authorities;

    private UserPrincipal(User user) {
        this.id             = user.getId();
        this.email          = user.getEmail();
        this.passwordHash   = user.getPasswordHash();
        this.role           = user.getRole();
        this.branchId       = user.getBranchId();
        this.accountAvailable = user.isAccountAvailable();

        // Build authorities: role prefix + all individual permissions
        Set<Permission> permissions = RolePermissionRegistry.permissionsFor(user.getRole());

        this.authorities = Stream.concat(
            // "ROLE_MANAGER" etc. — used by hasRole()
            Stream.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
            // "SALE_CREATE" etc. — used by hasAuthority() and @PreAuthorize
            permissions.stream()
                       .map(p -> new SimpleGrantedAuthority(p.name()))
        ).collect(Collectors.toUnmodifiableSet());
    }

    public static UserPrincipal from(User user) {
        return new UserPrincipal(user);
    }

    // ── UserDetails interface ────────────────────────────────────────

    @Override public String getUsername()   { return email; }
    @Override public String getPassword()   { return passwordHash; }

    @Override
    public boolean isAccountNonExpired()    { return true; }

    @Override
    public boolean isAccountNonLocked()     { return accountAvailable; }

    @Override
    public boolean isCredentialsNonExpired(){ return true; }

    @Override
    public boolean isEnabled()              { return accountAvailable; }

    // ── Convenience helpers used across the app ──────────────────────

    public boolean hasRole(Role r)              { return this.role == r; }
    public boolean hasAnyRole(Role... roles)    {
        for (Role r : roles) if (this.role == r) return true;
        return false;
    }
    public boolean hasPermission(Permission p)  {
        return authorities.stream()
                          .anyMatch(a -> a.getAuthority().equals(p.name()));
    }
    public boolean isGlobalUser() {
        return role == Role.OWNER || role == Role.ADMIN;
    }
}
