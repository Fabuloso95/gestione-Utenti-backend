package com.gestione_utenti.controller;

import com.gestione_utenti.dto.request.LoginRequest;
import com.gestione_utenti.dto.request.RefreshTokenRequestDTO;
import com.gestione_utenti.dto.request.RegistrazioneRequest;
import com.gestione_utenti.dto.response.AuthResponseDTO;
import com.gestione_utenti.dto.response.UtenteResponse;
import com.gestione_utenti.facade.AuthFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequest request) 
    {
    	AuthResponseDTO response = authFacade.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UtenteResponse> register(@Valid @RequestBody RegistrazioneRequest request) 
    {
    	UtenteResponse response = authFacade.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request)
    {
    	AuthResponseDTO response = authFacade.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDTO request)
    {
        authFacade.logout(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/me")
    public ResponseEntity<UtenteResponse> getCurrentUser(Authentication authentication)
    {
    	UtenteResponse userDetails = authFacade.getCurrentUserDetails(authentication);
        return ResponseEntity.ok(userDetails);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) 
    {
        Map<String, String> error = Map.of(
            "type", "INVALID_CREDENTIALS",
            "message", "Credenziali non valide. Per favore, riprova."
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) 
    {
        return new ResponseEntity<>(Map.of(
            "type", "INVALID_REQUEST",
            "message", ex.getMessage()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleGenericRuntimeException(RuntimeException ex)
    {
        return new ResponseEntity<>(Map.of(
            "type", "SERVER_ERROR",
            "message", "Si è verificato un errore del server."
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}