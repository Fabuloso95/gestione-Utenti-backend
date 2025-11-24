package com.gestione_utenti.facade;

import java.util.List;
import com.gestione_utenti.dto.request.*;
import com.gestione_utenti.dto.response.UtenteResponse;

public interface UtenteFacade 
{
	UtenteResponse creaNuovoUtente(UtenteRequest utenteRequest);
    
    UtenteResponse ottieniUtentePerId(Long id);
    
    List<UtenteResponse> ottieniTuttiGliUtenti();
    
    UtenteResponse aggiornaUtente(Long id, UtenteUpdateRequest utenteUpdateRequest);
    
    void eliminaUtente(Long id);

    List<UtenteResponse> cercaUtenti(String query);
}
