package com.gestione_utenti.facade.impl;

import com.gestione_utenti.dto.request.UtenteRequest;
import com.gestione_utenti.dto.request.UtenteUpdateRequest;
import com.gestione_utenti.dto.response.UtenteResponse;
import com.gestione_utenti.facade.UtenteFacade;
import com.gestione_utenti.service.UtenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.List;

@Component
@RequiredArgsConstructor
@Service
public class UtenteFacadeImpl implements UtenteFacade 
{
    private final UtenteService utenteService;

    @Override
    public UtenteResponse creaNuovoUtente(UtenteRequest utenteRequest) 
    {
        return utenteService.saveNewUtente(utenteRequest);
    }

    @Override
    public UtenteResponse ottieniUtentePerId(Long id) 
    {
        return utenteService.getUtenteById(id);
    }

    @Override
    public List<UtenteResponse> ottieniTuttiGliUtenti() 
    {
        return utenteService.getAllUtenti();
    }

    @Override
    public UtenteResponse aggiornaUtente(Long id, UtenteUpdateRequest utenteUpdateRequest) 
    {
        return utenteService.updateUtente(id, utenteUpdateRequest);
    }

    @Override
    public void eliminaUtente(Long id) 
    {
        utenteService.deleteUtente(id);
    }

    @Override
    public List<UtenteResponse> cercaUtenti(String query) 
    {
        return utenteService.searchUtenti(query);
    }
}