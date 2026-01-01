package com.portfolio.backend.controller;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.portfolio.backend.dto.AuthRequest;
import com.portfolio.backend.dto.AuthResponse;
import com.portfolio.backend.repository.UserRepository;
import com.portfolio.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.portfolio.backend.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Autowired
    private UserRepository userRepository;
//    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtService jwtService) {
//        this.authenticationManager = authenticationManager;
//        this.userDetailsService = userDetailsService;
//        this.jwtService = jwtService;
//    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        // 1️⃣ Authenticate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
//        System.out.println("Authorities: " + userDetails.getAuthorities());

        System.out.println("Step 1: entering login");

        // 2️⃣ Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        System.out.println("Step 2: after authentication");

        // 3️⃣ Generate JWT
        String jwt = jwtService.generateToken(userDetails);
        System.out.println("Step 3: token generated");

        // 4️⃣ Retrieve full user entity (to include role)
        User userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 5️⃣ Build structured response for frontend
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("user", Map.of(
                "email", userEntity.getUsername(),
                "role", userEntity.getRole() // assuming role is an enum or string
        ));

        return ResponseEntity.ok(response);
    }


    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API working!");
    }

    @GetMapping("/health")
    public String health() { return "ok"; }

}
