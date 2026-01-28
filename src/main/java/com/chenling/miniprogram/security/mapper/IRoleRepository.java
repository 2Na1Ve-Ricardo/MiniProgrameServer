package com.chenling.miniprogram.security.mapper;

import com.chenling.miniprogram.security.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByRoleCode(String roleCode);
}
