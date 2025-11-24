package com.gestione_utenti.facade;

import org.springframework.security.core.Authentication;
import com.gestione_utenti.dto.request.*;
import com.gestione_utenti.dto.response.*;

public interface AuthFacade 
{
    AuthResponseDTO login(LoginRequest request);
    UtenteResponse register(RegistrazioneRequest request);
    AuthResponseDTO refreshToken(String refreshToken);
    void logout(String refreshToken);
    UtenteResponse getCurrentUserDetails(Authentication authentication);
}
