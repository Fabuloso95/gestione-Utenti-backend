package com.gestione_utenti.service.impl;

import com.gestione_utenti.dto.request.LoginRequest;
import com.gestione_utenti.dto.request.RegistrazioneRequest;
import com.gestione_utenti.dto.response.AuthResponseDTO;
import com.gestione_utenti.dto.response.UtenteResponse;
import com.gestione_utenti.exception.ResourceNotFoundException;
import com.gestione_utenti.mapper.UtenteMapper;
import com.gestione_utenti.model.Ruolo;
import com.gestione_utenti.model.Utente;
import com.gestione_utenti.repository.UtenteRepository;
import com.gestione_utenti.security.JwtGenerator;
import com.gestione_utenti.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService 
{
    private final AuthenticationManager authenticationManager;
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final UtenteMapper utenteMapper;
    
    private static final String MESSAGGIO_UTENTE_NON_TROVATO = "Utente non trovato per il Codice Fiscale: ";

    @Override
    @Transactional
    public AuthResponseDTO login(LoginRequest request)
    {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getCodiceFiscale(),
                request.getPassword()
            )
        );

        String accessToken = jwtGenerator.generateToken(authentication);

        Utente utente = (Utente) authentication.getPrincipal();

        String refreshToken = generateRefreshToken(utente);
        
        utente.setRefreshToken(refreshToken);
        utenteRepository.save(utente);
        
        return AuthResponseDTO.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .codiceFiscale(utente.getCodiceFiscale())
            .ruolo(utente.getRuolo().name())
            .build();
    }

    @Override
    @Transactional
    public UtenteResponse registraUtente(RegistrazioneRequest registrazioneDTO) 
    {
        if (utenteRepository.findByCodiceFiscale(registrazioneDTO.getCodiceFiscale()).isPresent()) 
        {
            throw new IllegalArgumentException("Codice Fiscale già registrato.");
        }
        
        Utente utente = Utente.builder()
                .codiceFiscale(registrazioneDTO.getCodiceFiscale())
                .password(passwordEncoder.encode(registrazioneDTO.getPassword()))
                .nome(registrazioneDTO.getNome())
                .cognome(registrazioneDTO.getCognome())
                .attivo(true)
                .dataNascita(registrazioneDTO.getDataNascita() != null ? registrazioneDTO.getDataNascita() : LocalDate.of(1990, 1, 1)) 
                .ruolo(Ruolo.USER)
                .build();
        
        Utente utenteSalvato = utenteRepository.save(utente);
        
        return utenteMapper.toResponse(utenteSalvato);
    }
    
    @Override
    @Transactional
    public AuthResponseDTO refreshToken(String refreshToken) 
    { 
        if (!jwtGenerator.validateToken(refreshToken)) 
        {
             throw new IllegalArgumentException("Refresh Token non valido.");
        }
        
        String codiceFiscale = jwtGenerator.getUsernameFromJwt(refreshToken);

        Utente utente = utenteRepository.findByCodiceFiscale(codiceFiscale)
                .filter(u -> refreshToken.equals(u.getRefreshToken())) 
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token non trovato o revocato per l'utente " + codiceFiscale));
                
        Authentication simulatedAuth = new UsernamePasswordAuthenticationToken(
            utente.getCodiceFiscale(), 
            null, 
            utente.getAuthorities()
        );

        String nuovoAccessToken = jwtGenerator.generateToken(simulatedAuth);
        
        return AuthResponseDTO.builder()
                .accessToken(nuovoAccessToken)
                .refreshToken(refreshToken) 
                .codiceFiscale(utente.getCodiceFiscale())
                .ruolo(utente.getRuolo().name())
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken)
    {
        String codiceFiscale = jwtGenerator.getUsernameFromJwt(refreshToken);
        
        Optional<Utente> optionalUtente = utenteRepository.findByCodiceFiscale(codiceFiscale)
                .filter(u -> refreshToken.equals(u.getRefreshToken()));

        if (optionalUtente.isPresent()) {
            Utente utente = optionalUtente.get();
            utente.setRefreshToken(null);
            utenteRepository.save(utente);
        }
    }
    
    @Override
    public UtenteResponse getCurrentUserDetails(Authentication authentication) 
    {
        if (authentication == null || !authentication.isAuthenticated()) 
        {
            throw new IllegalArgumentException("Nessun utente autenticato.");
        }

        String codiceFiscale = authentication.getName(); 

        Utente utente = utenteRepository.findByCodiceFiscale(codiceFiscale)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGGIO_UTENTE_NON_TROVATO + codiceFiscale));

        return utenteMapper.toResponse(utente);
    }
    
    private String generateRefreshToken(Utente utente) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
            utente.getCodiceFiscale(), 
            null, 
            utente.getAuthorities()
        );
        return jwtGenerator.generateRefreshToken(auth);
    }
}