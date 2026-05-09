package com.pharmacy.pos.common.rbac;

/**
 * Permission
 *
 * Fine-grained permissions used with @PreAuthorize.
 * Each role maps to a set of permissions in RolePermissionRegistry.
 *
 * Naming convention: DOMAIN_ACTION
 * e.g.  SALE_CREATE, INVENTORY_EDIT, USER_MANAGE
 */
public enum Permission {

    // ── Sales ────────────────────────────────────────────────────────
    SALE_CREATE,         // Process a new sale at POS
    SALE_VIEW,           // View sale history
    SALE_REFUND,         // Issue a refund (manager+)
    SALE_VOID,           // Void a sale before completion

    // ── Inventory ────────────────────────────────────────────────────
    INVENTORY_VIEW,      // View stock levels
    INVENTORY_EDIT,      // Add / adjust stock quantities
    INVENTORY_TRANSFER,  // Transfer stock between branches (admin+)
    INVENTORY_DELETE,    // Remove inventory batch (manager+)

    // ── Products ─────────────────────────────────────────────────────
    PRODUCT_VIEW,        // View product catalogue
    PRODUCT_CREATE,      // Add new medicines / products
    PRODUCT_EDIT,        // Edit product details and prices
    PRODUCT_DELETE,      // Soft-delete a product (manager+)

    // ── Prescriptions ────────────────────────────────────────────────
    PRESCRIPTION_VIEW,   // View prescription records
    PRESCRIPTION_CREATE, // Attach prescription to a sale
    PRESCRIPTION_MANAGE, // Edit / delete prescriptions (pharmacist+)

    // ── Users ────────────────────────────────────────────────────────
    USER_VIEW,           // View users in own branch (manager+)
    USER_MANAGE,         // Create / update users in own branch
    USER_MANAGE_ALL,     // Create / update users across all branches (admin+)

    // ── Reports ──────────────────────────────────────────────────────
    REPORT_VIEW_BRANCH,  // View reports for own branch (manager+)
    REPORT_VIEW_ALL,     // View reports across all branches (admin/owner)
    REPORT_EXPORT,       // Export reports as CSV/PDF (manager+)

    // ── Branch Management ────────────────────────────────────────────
    BRANCH_VIEW,         // View branch list (manager sees own, admin sees all)
    BRANCH_MANAGE,       // Create / update branches (admin/owner)

    // ── Suppliers / Purchase Orders ──────────────────────────────────
    SUPPLIER_VIEW,
    SUPPLIER_MANAGE,
    PURCHASE_ORDER_CREATE,
    PURCHASE_ORDER_APPROVE,  // manager+

    // ── System ───────────────────────────────────────────────────────
    SYSTEM_CONFIG,       // System-wide settings (admin/owner only)
    AUDIT_LOG_VIEW,      // View audit trail (admin/owner only)
}
