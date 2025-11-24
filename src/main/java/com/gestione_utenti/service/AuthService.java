package com.gestione_utenti.service;

import org.springframework.security.core.Authentication;
import com.gestione_utenti.dto.request.*;
import com.gestione_utenti.dto.response.*;

public interface AuthService 
{
    UtenteResponse registraUtente(RegistrazioneRequest registrazioneDTO);
    AuthResponseDTO login(LoginRequest loginDTO);
    AuthResponseDTO refreshToken(String refreshToken);
    void logout(String refreshToken);
    UtenteResponse getCurrentUserDetails(Authentication authentication);
}
