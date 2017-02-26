package com.example.web.security;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.RequiredArgsConstructor;

@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    private final UserDetailsService customUserDetailsService;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;      
    private final AuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    public MessageDigestPasswordEncoder messageDigestPasswordEncoder()
    {
        return new MessageDigestPasswordEncoder("sha-256");
    }

    
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
     // @formatter:off
            http
                .authorizeRequests()
                    .antMatchers("/login").permitAll()
                    .antMatchers("/admin","/admin/**").hasRole("ADMIN")
                    .antMatchers("/**").hasRole("USER")
                    .anyRequest().authenticated()
            .and()
                .formLogin()
                    .loginProcessingUrl("/login-check")
                    .loginPage("/login")
                    .usernameParameter("j_username")
                    .passwordParameter("j_password")
                    .successHandler(authenticationSuccessHandler)       
                    .failureHandler(authenticationFailureHandler)
                    .defaultSuccessUrl("/home")
                    .failureUrl("/login?error").permitAll()
            .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout=true")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .permitAll();
     // @formatter:on
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.userDetailsService(customUserDetailsService);
    }
    
}
