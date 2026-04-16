package com.example.rest.controller;

import com.example.rest.dto.PasswordResetTokenRestDto;
import model.PasswordResetToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.rest.service.impl.PasswordResetTokenServiceImpl;

import java.util.Map;

@RestController
@RequestMapping("/api/password-reset")
public class PasswordResetTokenRestController {

    private final PasswordResetTokenServiceImpl passwordResetTokenService;

    public PasswordResetTokenRestController(PasswordResetTokenServiceImpl passwordResetTokenService) {
        this.passwordResetTokenService = passwordResetTokenService;
    }

    @PostMapping("/send")
    public String sendResetEmail(@RequestBody Map<String, String> body) {
        passwordResetTokenService.sendResetEmail(body.get("email"));
        return "Password reset link sent";
    }

    @GetMapping("/{token}")
    public PasswordResetTokenRestDto validateToken(@PathVariable String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenService.getValidToken(token);
        return new PasswordResetTokenRestDto(
                passwordResetToken.getId(),
                passwordResetToken.getToken(),
                passwordResetToken.getUser().getId(),
                passwordResetToken.getUser().getEmail(),
                passwordResetToken.getExpiryDate(),
                passwordResetToken.isExpired());
    }

    @PostMapping("/{token}")
    public String resetPassword(@PathVariable String token, @RequestBody Map<String, String> body) {
        passwordResetTokenService.resetPassword(token, body.get("newPassword"));
        return "Password updated successfully";
    }
}
