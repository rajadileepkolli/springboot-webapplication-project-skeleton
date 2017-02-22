package com.example.web.service;

import java.util.List;

import com.example.persistance.entity.User;

public interface UserService
{

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    List<User> findAllUsers();

    User saveUser(User user);

    User findUserByUsernameAndPassword(String username, String givenPassword);

}
