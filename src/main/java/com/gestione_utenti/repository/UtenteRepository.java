package com.gestione_utenti.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gestione_utenti.model.Utente;

public interface UtenteRepository extends JpaRepository<Utente, Long>
{
	List<Utente> findByNomeContainingOrCognomeContainingOrCodiceFiscaleContaining(String nome, String cognome, String codiceFiscale);
	Optional<Utente> findByCodiceFiscale(String codiceFiscale);
}
