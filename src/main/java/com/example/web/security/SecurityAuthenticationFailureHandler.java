package com.example.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("authenticationFailureHandler")
public class SecurityAuthenticationFailureHandler implements AuthenticationFailureHandler
{

    private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter = new UsernamePasswordAuthenticationFilter();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException
    {

        String userName = request.getParameter(
                usernamePasswordAuthenticationFilter.getUsernameParameter());

        log.info("onAuthenticationFailure- username={}, exceptionClass={}", userName,
                exception.getClass().getName());

        String parameter = "unknown";
        if (exception instanceof UsernameNotFoundException)
        {
            parameter = "usernameEmpty";
        }
        else if (exception instanceof BadCredentialsException)
        {
            parameter = "badCredential";
        }
        else if (exception instanceof LockedException)
        {
            parameter = "userLocked";
        }

        response.sendRedirect("login?error=" + parameter);
    }
}
