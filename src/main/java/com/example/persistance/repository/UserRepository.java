package com.example.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.persistance.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>
{
    User findUserByUsername(String username);
    
    User findUserByEmail(String email);

    User findUserByUsernameAndPassword(String username, String givenPassword);
    
}
