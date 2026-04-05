package com.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.impl.PasswordResetTokenServiceImpl;

@Controller
@RequiredArgsConstructor
public class PasswordResetTokenController {
    private final PasswordResetTokenServiceImpl passwordResetTokenService;

    @GetMapping("/forgotPassword")
    public String forgotPasswordPage() {
        return "forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String sendResetEmail(@RequestParam String email, ModelMap modelMap) {
        try {
            passwordResetTokenService.sendResetEmail(email);
            modelMap.addAttribute("msg", "Password reset link sent to your email.");
        } catch (RuntimeException e) {
            modelMap.addAttribute("error", e.getMessage());
        }
        return "forgotPassword";
    }

    @GetMapping("/reset-password/{token}")
    public String resetPasswordPage(@PathVariable String token, ModelMap modelMap) {
        try {
            passwordResetTokenService.getValidToken(token);
            modelMap.addAttribute("token", token);
            return "resetPassword";
        } catch (RuntimeException e) {
            return "redirect:/loginPage?msg=" + e.getMessage().replace(" ", "%20");
        }
    }

    @PostMapping("/reset-password/{token}")
    public String resetPassword(@PathVariable String token,
                                @RequestParam String newPassword,
                                ModelMap modelMap) {
        try {
            passwordResetTokenService.resetPassword(token, newPassword);
            return "redirect:/loginPage?msg=Password updated successfully";
        } catch (RuntimeException e) {
            modelMap.addAttribute("error", e.getMessage());
            modelMap.addAttribute("token", token);
            return "resetPassword";
        }
    }
}
