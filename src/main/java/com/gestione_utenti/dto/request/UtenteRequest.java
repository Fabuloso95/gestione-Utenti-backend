package com.gestione_utenti.dto.request;

import java.time.LocalDate;
import com.gestione_utenti.model.Ruolo;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UtenteRequest 
{
	@NotBlank(message = "il nome è obbligatorio")
	private String nome;
	
	@NotBlank(message = "il cognome è obbligatorio")
	private String cognome;
	
	@NotBlank(message = "il codice fiscale è obbligatorio")
	private String codiceFiscale;
	
	@PastOrPresent(message = "La data di nascita non può essere futura")
    @NotNull(message = "La data di nascita è obbligatoria")
    private LocalDate dataNascita;
	
	@NotNull
	private Ruolo ruolo;
	
	private String password;
}
