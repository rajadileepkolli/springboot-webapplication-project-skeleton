package com.example.web.security;

import static java.util.Objects.isNull;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.persistance.entity.User;
import com.example.persistance.enums.Role;
import com.example.web.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService
{
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String userName)
    {
        User user = userService.findUserByUsername(userName);
        if (isNull(user))
        {
            throw new UsernameNotFoundException("UserName " + userName + " not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), user.isEnabled(), true,
                true, !user.isLocked(),
                getAuthorities(user.getRoles()));
    }
    
    /**
     * Spring Security UserDetails required methods 
     * @param roles *
     */
    public Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles)
    {
        return roles.stream().map(Role::getAuthority)
                .map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

}
