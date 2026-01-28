package com.chenling.miniprogram.security.mapper;

import com.chenling.miniprogram.security.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  Optional<User> findByAppId(String appId);

  boolean existsByUsername(String username);

  boolean existsByAppId(String appId);
}
