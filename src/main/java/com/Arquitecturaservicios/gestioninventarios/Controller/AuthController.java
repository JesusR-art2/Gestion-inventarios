package com.Arquitecturaservicios.gestioninventarios.Controller;

import com.Arquitecturaservicios.gestioninventarios.Config.JwtUtil;
import com.Arquitecturaservicios.gestioninventarios.Entities.UserRole;
import com.Arquitecturaservicios.gestioninventarios.Repository.UsuarioRepository;
import com.Arquitecturaservicios.gestioninventarios.Service.UsuarioService;
import com.Arquitecturaservicios.gestioninventarios.dto.LoginRequestDto;
import com.Arquitecturaservicios.gestioninventarios.dto.LoginResponseDto;
import com.Arquitecturaservicios.gestioninventarios.dto.UsuarioDto;
import com.Arquitecturaservicios.gestioninventarios.Entities.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioService usuarioService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            UsuarioDto usuario = usuarioService.findByEmail(loginRequest.getEmail());
            
            if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (usuario.getActive() == null || !usuario.getActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            UserRole role = UserRole.ADMIN;
            
            String token = jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getEmail(),
                "",
                role
            );

            LoginResponseDto response = new LoginResponseDto(
                token,
                usuario.getEmail(),
                usuario.getEmail(),
                "",
                role.name(),
                jwtUtil.extractIssuedAt(token),
                jwtUtil.extractExpiration(token)
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/reset-admin-password")
    public ResponseEntity<String> resetAdminPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String newPassword = request.get("password");

            if (email == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Email and password are required");
            }

            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            usuario.setPassword(passwordEncoder.encode(newPassword));
            usuarioRepository.save(usuario);

            return ResponseEntity.ok("Admin password updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating password: " + e.getMessage());
        }
    }
}
