package com.gestione_utenti.dto.request;

import java.time.LocalDate;
import com.gestione_utenti.model.Ruolo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UtenteUpdateRequest 
{
	private String nome;
	private String cognome;
	private LocalDate dataNascita;
	private Ruolo ruolo;
}
