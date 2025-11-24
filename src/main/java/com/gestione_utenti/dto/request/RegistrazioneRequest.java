package com.gestione_utenti.dto.request;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrazioneRequest 
{
		@NotBlank(message = "il codice fiscale è obbligatorio")
		@Size(min = 10, max = 16, message = "il codice fiscale deve contenere minimo 10 caratteri e massimo 16")
		private String codiceFiscale;
		
		@NotBlank(message = "La Password è obbligatoria")
	    @Size(min = 8, message = "La password deve essere lunga almeno 8 caratteri")
		@Pattern(
				    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$",
				    message = "La password deve contenere almeno una maiuscola, una minuscola, un numero e un carattere speciale")
	    private String password;
		
		@NotBlank(message = "Il nome non può essere vuoto")
	    private String nome;

	    @NotBlank(message = "Il cognome non può essere vuoto")
	    private String cognome;
	    
	    @NotNull(message = "La data di nascita è obbligatoria")
	    private LocalDate dataNascita;
}
