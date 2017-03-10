package com.example.web.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.persistance.entity.User;
import com.example.web.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController
{
    private final UserService userService;

    @RequestMapping("/admin/users")
    public String getUserList(Model model)
    {
        log.debug("getUserList");
        model.addAttribute(userService.findAllUsers());

        return "users";
    }

    @GetMapping(value = "/admin/users/create")
    public String createNewUserForm(Model model)
    {
        model.addAttribute("user", new User());
        return "users-create";
    }

    @PostMapping(value = "/admin/users/create")
    public String createNewUser(@Valid User user, BindingResult bindingResult)
    {
        log.debug("createNewUser, username={}, email={}, errorCount={}",
                user.getUsername(), user.getEmail(), bindingResult.getErrorCount());

        if (userService.findUserByUsername(user.getUsername()) != null)
        {
            bindingResult.rejectValue("username", "error.username.exist");
        }

        if (userService.findUserByEmail(user.getEmail()) != null)
        {
            bindingResult.rejectValue("email", "error.email.exist");
        }

        if (bindingResult.hasErrors())
        {
            return "users-create";
        }

        user.setEnabled(true);
        userService.saveUser(user);

        return "redirect:/admin/users";
    }

}
