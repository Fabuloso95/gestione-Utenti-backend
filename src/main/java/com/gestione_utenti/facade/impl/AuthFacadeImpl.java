package com.gestione_utenti.facade.impl;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.gestione_utenti.dto.request.*;
import com.gestione_utenti.dto.response.*;
import com.gestione_utenti.facade.AuthFacade;
import com.gestione_utenti.service.AuthService;
import lombok.Data;

@Data
@Service
public class AuthFacadeImpl implements AuthFacade
{
	private final AuthService authService;
	
    @Override
    public AuthResponseDTO login(LoginRequest request) 
    {
        return authService.login(request);
    }

	@Override
	public UtenteResponse register(RegistrazioneRequest request)
	{
		return authService.registraUtente(request);
	}

	@Override
	public AuthResponseDTO refreshToken(String refreshToken)
	{
		return authService.refreshToken(refreshToken);
	}

	@Override
	public void logout(String refreshToken)
	{
		authService.logout(refreshToken);
	}

	@Override
	public UtenteResponse getCurrentUserDetails(Authentication authentication)
	{
		return authService.getCurrentUserDetails(authentication);
	}
}
