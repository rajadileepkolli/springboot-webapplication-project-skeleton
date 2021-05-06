package com.example.web.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.persistance.entity.User;
import com.example.persistance.enums.Role;
import com.example.web.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = UserController.class)
@WithMockUser
public class UserControllerTest
{
    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Test
    public void testGetUserList() throws Exception
    {
        User user = new User();
        user.setUsername("JUNITUSERNAME");
        user.setRoles(Collections.singletonList(Role.ROLE_USER));
        List<User> userList = Collections.singletonList(user);
        when(userService.findAllUsers()).thenReturn(userList);
        this.mvc.perform(get("/admin/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(view().name("users"))
                .andExpect(redirectedUrl(null));

    }

    @Test
    public void testCreateNewUserForm() throws Exception
    {
        this.mvc.perform(get("/admin/users/create").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(view().name("users-create"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "admin", password= "{noop}123456")
    public void testCreateNewUser() throws Exception
    {
        User user = new User();
        user.setUsername("JUNIT");
        user.setEmail("JUNIT@JUNIT.COM");
        user.setPassword("JUNITPASS");
        user.setRoles(Collections.singletonList(Role.ROLE_USER));
        this.mvc.perform(post("/admin/users/create")
                .content(this.objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("users-create"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

}
