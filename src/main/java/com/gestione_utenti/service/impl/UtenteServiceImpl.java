package com.gestione_utenti.service.impl;

import com.gestione_utenti.dto.request.*;
import com.gestione_utenti.dto.response.UtenteResponse;
import com.gestione_utenti.mapper.UtenteMapper;
import com.gestione_utenti.model.*;
import com.gestione_utenti.repository.UtenteRepository;
import com.gestione_utenti.service.UtenteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtenteServiceImpl implements UtenteService 
{
    private final UtenteRepository utenteRepository;
    private final UtenteMapper utenteMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UtenteResponse saveNewUtente(UtenteRequest utenteRequest) 
    {
        Utente utente = utenteMapper.toEntity(utenteRequest);

        utente.setPassword(passwordEncoder.encode(utenteRequest.getPassword()));

        if (utenteRequest.getRuolo() == null) 
        {
             utente.setRuolo(Ruolo.USER); 
        }

        Utente savedUtente = utenteRepository.save(utente);
        
        return utenteMapper.toResponse(savedUtente);
    }

    @Override
    public UtenteResponse getUtenteById(Long id) 
    {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utente con ID " + id + " non trovato."));
        return utenteMapper.toResponse(utente);
    }

    @Override
    public List<UtenteResponse> getAllUtenti() 
    {
        return utenteRepository.findAll().stream()
                .map(utenteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UtenteResponse updateUtente(Long id, UtenteUpdateRequest utenteUpdateRequest) 
    {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Impossibile aggiornare. Utente con ID " + id + " non trovato."));

        utenteMapper.updateEntityFromUpdateRequest(utenteUpdateRequest, utente);
        
        Utente updatedUtente = utenteRepository.save(utente);

        return utenteMapper.toResponse(updatedUtente);
    }

    @Override
    public void deleteUtente(Long id) 
    {
        if (!utenteRepository.existsById(id)) 
        {
            throw new EntityNotFoundException("Impossibile eliminare. Utente con ID " + id + " non trovato.");
        }
        utenteRepository.deleteById(id);
    }

    @Override
    public List<UtenteResponse> searchUtenti(String query) 
    {
        return utenteRepository.findByNomeContainingOrCognomeContainingOrCodiceFiscaleContaining(query, query, query).stream()
                .map(utenteMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public Utente loadUserByCodiceFiscale(String codiceFiscale) 
    {
        return utenteRepository.findByCodiceFiscale(codiceFiscale)
                .orElseThrow(() -> new EntityNotFoundException("Utente con Codice Fiscale " + codiceFiscale + " non trovato."));
    }
}