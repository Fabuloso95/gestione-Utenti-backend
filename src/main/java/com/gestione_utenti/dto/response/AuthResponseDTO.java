package com.gestione_utenti.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class AuthResponseDTO 
{
	private String accessToken;
    private String refreshToken;
    private String codiceFiscale;
    private String ruolo;
}