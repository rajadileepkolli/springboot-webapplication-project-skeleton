package com.example.web.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.example.persistance.entity.User;
import com.example.persistance.enums.Role;
import com.example.web.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserController.class)
@WithMockUser
public class UserControllerTest
{
    @Autowired
    private MockMvc mvc;

    @MockBean
    UserService userService;

    @Test
    public void testGetUserList() throws Exception
    {
        User user = new User();
        user.setUsername("JUNITUSERNAME");
        user.setRoles(Arrays.asList(Role.ROLE_USER));
        List<User> userList = Arrays.asList(user);
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
    @WithMockUser(username = "admin", roles = { "USER" })
    public void testCreateNewUser() throws Exception
    {
        User user = new User();
        user.setUsername("JUNIT");
        user.setEmail("JUNIT@JUNIT.COM");
        user.setPassword("JUNITPASS");
        user.setRoles(Arrays.asList(Role.ROLE_USER));
        this.mvc.perform(post("/admin/users/create").content(this.json(user))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(view().name("users-create"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    private String json(User user) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(user);
    }
}
