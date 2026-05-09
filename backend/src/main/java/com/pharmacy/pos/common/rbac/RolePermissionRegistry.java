package com.pharmacy.pos.common.rbac;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * RolePermissionRegistry
 *
 * Single source of truth that maps every Role to its allowed Permissions.
 * Spring Security reads this at startup via UserPrincipal.getAuthorities().
 *
 * To change what a role can do: edit ONLY this file.
 */
public final class RolePermissionRegistry {

    private RolePermissionRegistry() {}

    private static final Map<Role, Set<Permission>> REGISTRY =
            new EnumMap<>(Role.class);

    static {

        // ── CASHIER ──────────────────────────────────────────────────
        REGISTRY.put(Role.CASHIER, EnumSet.of(
                Permission.SALE_CREATE,
                Permission.SALE_VIEW,
                Permission.SALE_VOID,
                Permission.PRODUCT_VIEW,
                Permission.INVENTORY_VIEW,
                Permission.PRESCRIPTION_VIEW
        ));

        // ── DATA_ENTRY ────────────────────────────────────────────────
        REGISTRY.put(Role.DATA_ENTRY, EnumSet.of(
                Permission.PRODUCT_VIEW,
                Permission.PRODUCT_CREATE,
                Permission.PRODUCT_EDIT,
                Permission.INVENTORY_VIEW,
                Permission.INVENTORY_EDIT,
                Permission.SUPPLIER_VIEW,
                Permission.SUPPLIER_MANAGE,
                Permission.PURCHASE_ORDER_CREATE
        ));

        // ── PHARMACIST ────────────────────────────────────────────────
        REGISTRY.put(Role.PHARMACIST, EnumSet.of(
                Permission.SALE_CREATE,
                Permission.SALE_VIEW,
                Permission.SALE_VOID,
                Permission.PRODUCT_VIEW,
                Permission.PRODUCT_CREATE,
                Permission.PRODUCT_EDIT,
                Permission.INVENTORY_VIEW,
                Permission.INVENTORY_EDIT,
                Permission.PRESCRIPTION_VIEW,
                Permission.PRESCRIPTION_CREATE,
                Permission.PRESCRIPTION_MANAGE,
                Permission.SUPPLIER_VIEW,
                Permission.PURCHASE_ORDER_CREATE
        ));

        // ── MANAGER ───────────────────────────────────────────────────
        REGISTRY.put(Role.MANAGER, EnumSet.of(
                Permission.SALE_CREATE,
                Permission.SALE_VIEW,
                Permission.SALE_VOID,
                Permission.SALE_REFUND,
                Permission.PRODUCT_VIEW,
                Permission.PRODUCT_CREATE,
                Permission.PRODUCT_EDIT,
                Permission.PRODUCT_DELETE,
                Permission.INVENTORY_VIEW,
                Permission.INVENTORY_EDIT,
                Permission.INVENTORY_DELETE,
                Permission.PRESCRIPTION_VIEW,
                Permission.PRESCRIPTION_CREATE,
                Permission.PRESCRIPTION_MANAGE,
                Permission.USER_VIEW,
                Permission.USER_MANAGE,
                Permission.REPORT_VIEW_BRANCH,
                Permission.REPORT_EXPORT,
                Permission.BRANCH_VIEW,
                Permission.SUPPLIER_VIEW,
                Permission.SUPPLIER_MANAGE,
                Permission.PURCHASE_ORDER_CREATE,
                Permission.PURCHASE_ORDER_APPROVE
        ));

        // ── ADMIN ─────────────────────────────────────────────────────
        REGISTRY.put(Role.ADMIN, EnumSet.of(
                Permission.SALE_CREATE,
                Permission.SALE_VIEW,
                Permission.SALE_VOID,
                Permission.SALE_REFUND,
                Permission.PRODUCT_VIEW,
                Permission.PRODUCT_CREATE,
                Permission.PRODUCT_EDIT,
                Permission.PRODUCT_DELETE,
                Permission.INVENTORY_VIEW,
                Permission.INVENTORY_EDIT,
                Permission.INVENTORY_DELETE,
                Permission.INVENTORY_TRANSFER,
                Permission.PRESCRIPTION_VIEW,
                Permission.PRESCRIPTION_CREATE,
                Permission.PRESCRIPTION_MANAGE,
                Permission.USER_VIEW,
                Permission.USER_MANAGE,
                Permission.USER_MANAGE_ALL,
                Permission.REPORT_VIEW_BRANCH,
                Permission.REPORT_VIEW_ALL,
                Permission.REPORT_EXPORT,
                Permission.BRANCH_VIEW,
                Permission.BRANCH_MANAGE,
                Permission.SUPPLIER_VIEW,
                Permission.SUPPLIER_MANAGE,
                Permission.PURCHASE_ORDER_CREATE,
                Permission.PURCHASE_ORDER_APPROVE,
                Permission.SYSTEM_CONFIG,
                Permission.AUDIT_LOG_VIEW
        ));

        // ── OWNER ─────────────────────────────────────────────────────
        // Owner has ALL permissions
        REGISTRY.put(Role.OWNER, EnumSet.allOf(Permission.class));
    }

    /**
     * Returns the permissions for a given role.
     * Returns an empty set for unknown roles (fail-safe).
     */
    public static Set<Permission> permissionsFor(Role role) {
        return REGISTRY.getOrDefault(role, EnumSet.noneOf(Permission.class));
    }
}
