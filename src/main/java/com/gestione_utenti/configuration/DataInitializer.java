package com.gestione_utenti.configuration;

import com.gestione_utenti.model.Ruolo;
import com.gestione_utenti.model.Utente;
import com.gestione_utenti.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner
{
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception 
    {
        if (utenteRepository.findByCodiceFiscale("CFADMIN0000X").isEmpty()) 
        {
            Utente admin = Utente.builder()
                .nome("Admin")
                .cognome("Default")
                .codiceFiscale("CFADMIN0000X")
                .dataNascita(LocalDate.of(1990, 1, 1))
                .ruolo(Ruolo.ADMIN)
                .password(passwordEncoder.encode("adminpassword6£"))
                .attivo(true)
                .build();
            
            utenteRepository.save(admin);
            log.info("Utente ADMIN di default creato con successo: CFADMIN0000X / adminpassword");
        } 
        else 
        {
            log.info("Utente ADMIN di default già esistente. Nessuna azione intrapresa.");
        }
    }
}