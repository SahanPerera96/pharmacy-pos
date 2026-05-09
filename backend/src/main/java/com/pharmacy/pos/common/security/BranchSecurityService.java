package com.pharmacy.pos.common.security;

import com.pharmacy.pos.common.rbac.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * BranchSecurityService
 *
 * Enforces branch-level data isolation.
 * Used in @PreAuthorize SpEL expressions on service methods:
 *
 *   @PreAuthorize("@branchSecurity.canAccess(#branchId)")
 *   public Page<SaleDTO> getSalesForBranch(Long branchId, Pageable pageable) { ... }
 *
 * Rules:
 *   - OWNER / ADMIN : can access ANY branch
 *   - MANAGER / PHARMACIST / DATA_ENTRY / CASHIER : own branch only
 */
@Slf4j
@Service("branchSecurity")
public class BranchSecurityService {

    /**
     * Returns true if the currently authenticated user may access data
     * belonging to the given branchId.
     */
    public boolean canAccess(Long branchId) {
        UserPrincipal principal = getCurrentPrincipal();
        if (principal == null) return false;

        // Global roles bypass branch restrictions
        if (principal.isGlobalUser()) return true;

        // Branch-scoped roles must match their own branch
        boolean allowed = branchId != null && branchId.equals(principal.getBranchId());
        if (!allowed) {
            log.warn("Branch access denied: user {} (branch {}) attempted to access branch {}",
                    principal.getEmail(), principal.getBranchId(), branchId);
        }
        return allowed;
    }

    /**
     * Convenience: returns true if the current user IS the owner or admin.
     * Use for endpoints that should only ever be reached by global admins.
     */
    public boolean isGlobalAdmin() {
        UserPrincipal p = getCurrentPrincipal();
        return p != null && p.isGlobalUser();
    }

    /**
     * Returns the branchId the current user is allowed to query.
     * Services call this to inject the correct branchId automatically,
     * rather than trusting the client-supplied value.
     *
     * @param requestedBranchId the branch the client asked for
     * @return the branch to actually use
     * @throws SecurityException if the user is trying to access a forbidden branch
     */
    public Long resolveAllowedBranchId(Long requestedBranchId) {
        UserPrincipal principal = getCurrentPrincipal();
        if (principal == null) throw new SecurityException("Not authenticated");

        if (principal.isGlobalUser()) {
            // Admin/Owner: use the client's requested branch (or null = all branches)
            return requestedBranchId;
        }

        Long userBranch = principal.getBranchId();
        if (requestedBranchId != null && !requestedBranchId.equals(userBranch)) {
            throw new SecurityException("Access denied to branch " + requestedBranchId);
        }
        return userBranch;
    }

    /**
     * Returns the current principal from Spring Security context.
     * Returns null if the request is unauthenticated.
     */
    public UserPrincipal getCurrentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal p) {
            return p;
        }
        return null;
    }

    /**
     * Utility: check if the current user has a specific role.
     * Usable in @PreAuthorize: @branchSecurity.hasRole('MANAGER')
     */
    public boolean hasRole(String roleName) {
        UserPrincipal p = getCurrentPrincipal();
        if (p == null) return false;
        try {
            return p.getRole() == Role.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
