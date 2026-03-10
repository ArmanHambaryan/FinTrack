package com.example.app.controller;

import com.example.app.serivce.security.SpringUser;
import lombok.RequiredArgsConstructor;
import model.User;
import model.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.UserService;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;



    @GetMapping("/")
    public String mainPage(@AuthenticationPrincipal SpringUser userPrincipal,
                           ModelMap modelMap) {
        if (userPrincipal != null) {
            return "redirect:/successLogin";
        }
        return "index";
    }

    @GetMapping("/successLogin")
    public String successLogin(@AuthenticationPrincipal SpringUser springUser) {
        if (springUser != null
                && springUser.getUser().getRole() == UserRole.ADMIN) {
            return "redirect:/admin/home";
        } else {
            return "redirect:/user/home";
        }
    }

    @GetMapping("/loginPage")
    public String loginPage(@RequestParam(required = false) String msg, ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "loginPage";
    }

    @GetMapping("/registerPage")
    public String registerPage(@RequestParam(required = false) String msg, ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "registerPage";
    }


    @PostMapping("/register")
    public String register(@ModelAttribute User registeredUser,
                           @RequestParam(value = "role", required = false) String role) {
        if (userService.findByEmail(registeredUser.getEmail()).isPresent()) {
            return "redirect:/registerPage?msg=Username already exists!";
        }
        if ("ADMIN".equalsIgnoreCase(role)) {
            registeredUser.setRole(UserRole.ADMIN);
        } else {
            registeredUser.setRole(UserRole.USER);
        }
        registeredUser.setPassword(passwordEncoder.encode(registeredUser.getPassword()));
        userService.save(registeredUser);
        return "redirect:/loginPage?msg=Registration successful, pls login!";
    }



}
