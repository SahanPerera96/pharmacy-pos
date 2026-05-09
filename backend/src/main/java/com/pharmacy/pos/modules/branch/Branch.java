package com.pharmacy.pos.modules.branch;

import com.pharmacy.pos.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

/**
 * Branch Entity
 *
 * Represents a physical pharmacy location.
 * Every piece of inventory, sale, and staff belongs to a branch.
 * OWNER and ADMIN users can query across all branches.
 */
@Entity
@Table(name = "branches")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;          // e.g. "BR001" — used in invoice numbers

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;  // Pharmacy business license

    @Column(name = "is_headquarters", nullable = false)
    @Builder.Default
    private Boolean isHeadquarters = false;
}
