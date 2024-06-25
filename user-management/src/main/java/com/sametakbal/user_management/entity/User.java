package com.sametakbal.user_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity implements UserDetails, Principal {

    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String name;
    @Column
    private String surname;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column
    private Boolean enabled;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<RoleEntity> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles
                .stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
