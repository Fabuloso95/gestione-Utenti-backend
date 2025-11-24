package com.gestione_utenti.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class LoginRequest 
{
    @NotBlank(message = "Il codice fiscale è obbligatorio")
    private String codiceFiscale;

    @NotBlank(message = "La password è obbligatoria")
    private String password;
}