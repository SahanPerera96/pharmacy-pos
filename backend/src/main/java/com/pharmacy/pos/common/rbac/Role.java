package com.pharmacy.pos.common.rbac;

/**
 * Role
 *
 * Defines the user hierarchy for the pharmacy POS.
 * Stored as a STRING in the database (not ordinal) for readability
 * and migration safety.
 *
 * Hierarchy (highest → lowest):
 *   OWNER → ADMIN → MANAGER → PHARMACIST → DATA_ENTRY → CASHIER
 */
public enum Role {

    /**
     * Business owner. Full access to everything across all branches.
     * Can see financial summaries, configure system settings, manage admins.
     */
    OWNER,

    /**
     * System administrator. Same access as OWNER except cannot manage
     * other OWNER accounts. Typically an IT/ops person.
     */
    ADMIN,

    /**
     * Branch manager. Full access WITHIN their assigned branch.
     * Can issue refunds, manage branch staff, view branch reports.
     * Cannot see other branches or system-level settings.
     */
    MANAGER,

    /**
     * Licensed pharmacist. Can process sales, handle prescriptions,
     * view inventory. Cannot manage users or issue refunds.
     */
    PHARMACIST,

    /**
     * Data entry clerk. Can add/edit products and inventory records.
     * Cannot process sales or view financial reports.
     */
    DATA_ENTRY,

    /**
     * Cashier / front-desk staff. Can process sales only.
     * Most restricted role — cannot modify products or view reports.
     */
    CASHIER
}
