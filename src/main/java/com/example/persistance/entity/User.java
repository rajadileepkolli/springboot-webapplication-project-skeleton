package com.example.persistance.entity;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.persistance.enums.Role;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "USER", uniqueConstraints = { @UniqueConstraint(columnNames = "EMAIL"),
        @UniqueConstraint(columnNames = "USERNAME") })
public class User extends Persistent implements UserDetails
{

    private static final long serialVersionUID = 1L;

    @Size(min = 6, max = 100)
    @Column(name = "EMAIL", unique = true, nullable = false, length = 100)
    private String email;

    @Size(min = 5, max = 50)
    @Pattern(regexp = "^[a-z0-9]*$", message = "Only small letters and numbers allowed")
    @Column(name = "USERNAME", unique = true, nullable = false, length = 50)
    private String username;

    @Size(min = 6, max = 50)
    @Column(name = "PASSWORD", nullable = false, length = 50)
    private String password;

    @Column(name = "ENABLED", nullable = false)
    private boolean enabled;

    @Column(name = "LOCKED", nullable = false)
    private boolean locked = false;

    @NotEmpty
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    private Collection<Role> roles;

    public User()
    {
        super();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return getRoles().stream().map(Role::getAuthority)
                .map(SimpleGrantedAuthority::new).collect(toSet());
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true; // Not Implemented
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return !isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true; // Not Implemented
    }

}
