package com.gestione_utenti.dto.response;

import java.time.LocalDate;
import com.gestione_utenti.model.Ruolo;
import lombok.*;

@Data
@Builder
public class UtenteResponse 
{
	private Long id;
	private String nome;
	private String cognome;
	private LocalDate dataNascita;
	private Ruolo ruolo;
}
