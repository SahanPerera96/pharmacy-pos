package com.pharmacy.pos.modules.user;

import com.pharmacy.pos.common.rbac.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used by UserDetailsService (login)
    Optional<User> findByEmailAndIsActiveTrue(String email);

//    boolean existsByEmail(String email);
//
//    // Manager sees only their branch; Admin sees all
//    Page<User> findByBranchIdAndIsActiveTrue(Long branchId, Pageable pageable);
//
//    Page<User> findByIsActiveTrue(Pageable pageable);
//
//    // Search by name or email within a branch
//    @Query("""
//        SELECT u FROM User u
//        WHERE u.isActive = true
//          AND (:branchId IS NULL OR u.branch.id = :branchId)
//          AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
//               OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
//               OR LOWER(u.email)     LIKE LOWER(CONCAT('%', :search, '%')))
//    """)
//    Page<User> searchUsers(
//        @Param("branchId") Long branchId,
//        @Param("search")   String search,
//        Pageable pageable
//    );
//
//    long countByBranchIdAndRole(Long branchId, Role role);
}
