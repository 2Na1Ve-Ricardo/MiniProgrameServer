package com.chenling.miniprogram.security.entity;

import com.sun.istack.NotNull;
import com.sun.xml.internal.ws.developer.Serialization;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@Entity
@Table(name = "sys_user")
@Serialization
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Nullable
  @Column(name = "display_name")
  private String displayName;

  @Column(nullable = false)
  private String password;

  @Column(name = "app_id", unique = true)
  private String appId;

  @Nullable
  private String phone;

  @Nullable
  private String company;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;
}
