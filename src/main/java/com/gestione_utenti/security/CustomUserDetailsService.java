package com.gestione_utenti.security;

import com.gestione_utenti.model.Utente;
import com.gestione_utenti.service.UtenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService 
{
    private final UtenteService utenteService;

    @Override
    public UserDetails loadUserByUsername(String codiceFiscale) throws UsernameNotFoundException 
    {
        try 
        {
            Utente utente = utenteService.loadUserByCodiceFiscale(codiceFiscale);
            return utente;
        } 
        catch (jakarta.persistence.EntityNotFoundException e) 
        {
            throw new UsernameNotFoundException("Utente non trovato con codice fiscale: " + codiceFiscale);
        }
    }
}