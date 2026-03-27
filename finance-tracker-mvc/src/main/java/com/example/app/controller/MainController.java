package com.example.app.controller;

import dto.UserDto;
import lombok.RequiredArgsConstructor;
import model.User;
import model.UserRole;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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



    @GetMapping("/")
    public String mainPage(Authentication authentication, ModelMap modelMap) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/successLogin";
        }
        return "index";
    }

    @GetMapping("/successLogin")
    public String successLogin(Authentication authentication) {
        boolean isAdmin = authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(auth -> "ADMIN".equals(auth.getAuthority()));
        if (isAdmin) {
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

        UserDto userDto = new UserDto();
        userDto.setUsername(registeredUser.getUsername());
        userDto.setEmail(registeredUser.getEmail());
        userDto.setPassword(registeredUser.getPassword());
        userDto.setRole(registeredUser.getRole());

        userService.registerUser(userDto);
        return "redirect:/loginPage?msg=Registration successful, pls login!";
    }



}
