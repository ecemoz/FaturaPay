package com.yildiz.faturapay.controllers;

import com.yildiz.faturapay.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username, @RequestParam String password) {
        try {
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Kullanıcı adı boş olamaz.");
            }
            if (password == null || password.length() < 6) {
                return ResponseEntity.badRequest().body("Şifre en az 6 karakter uzunluğunda olmalıdır.");
            }
            String response = authService.registerUser(username, password);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Bu kullanıcı adı zaten alınmış.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Kayıt sırasında bir hata oluştu.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Kullanıcı adı boş olamaz.");
            }
            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body("Şifre boş olamaz.");
            }
            String token = authService.loginUser(username, password);
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Geçersiz kullanıcı adı veya şifre.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Giriş sırasında bir hata oluştu.");
        }
    }
}