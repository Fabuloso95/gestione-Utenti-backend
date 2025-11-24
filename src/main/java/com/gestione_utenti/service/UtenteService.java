package com.gestione_utenti.service;

import java.util.List;

import com.gestione_utenti.dto.request.UtenteRequest;
import com.gestione_utenti.dto.request.UtenteUpdateRequest;
import com.gestione_utenti.dto.response.UtenteResponse;
import com.gestione_utenti.model.Utente;

public interface UtenteService 
{
	UtenteResponse saveNewUtente(UtenteRequest utenteRequest);
    
    UtenteResponse getUtenteById(Long id);
    
    List<UtenteResponse> getAllUtenti();
    
    UtenteResponse updateUtente(Long id, UtenteUpdateRequest utenteUpdateRequest);
    
    void deleteUtente(Long id);

    List<UtenteResponse> searchUtenti(String query);
    
    Utente loadUserByCodiceFiscale(String codiceFiscale);
}
