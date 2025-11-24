package com.gestione_utenti.controller;

import com.gestione_utenti.dto.request.UtenteRequest;
import com.gestione_utenti.dto.request.UtenteUpdateRequest;
import com.gestione_utenti.dto.response.UtenteResponse;
import com.gestione_utenti.facade.UtenteFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/utenti")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") 
public class UtenteController 
{
    private final UtenteFacade utenteFacade;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UtenteResponse> creaUtente(@Valid @RequestBody UtenteRequest request) 
    {
        UtenteResponse response = utenteFacade.creaNuovoUtente(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtenteResponse> ottieniUtente(@PathVariable Long id) 
    {
        UtenteResponse response = utenteFacade.ottieniUtentePerId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UtenteResponse>> ottieniTuttiGliUtenti() 
    {
        List<UtenteResponse> response = utenteFacade.ottieniTuttiGliUtenti();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UtenteResponse> aggiornaUtente(@PathVariable Long id, @RequestBody UtenteUpdateRequest request) 
    {
        UtenteResponse response = utenteFacade.aggiornaUtente(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UtenteResponse>> cercaUtenti(@RequestParam String query) 
    {
        List<UtenteResponse> response = utenteFacade.cercaUtenti(query);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaUtente(@PathVariable Long id) 
    {
        utenteFacade.eliminaUtente(id);
    }
}