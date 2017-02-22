package com.example.web.security;

import static java.util.Objects.isNull;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.persistance.entity.User;
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
        return user;
    }

}
