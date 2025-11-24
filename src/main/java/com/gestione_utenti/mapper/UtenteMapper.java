package com.gestione_utenti.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import com.gestione_utenti.dto.request.UtenteRequest;
import com.gestione_utenti.dto.request.UtenteUpdateRequest;
import com.gestione_utenti.dto.response.RuoloDTO;
import com.gestione_utenti.dto.response.UtenteResponse;
import com.gestione_utenti.model.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE) 
public interface UtenteMapper 
{
    UtenteMapper INSTANCE = Mappers.getMapper(UtenteMapper.class);
	
    default RuoloDTO toRuoloDTO(Ruolo ruolo) 
    {
        if (ruolo == null) 
        {
            return null;
        }
        Long id;
        switch (ruolo) 
        {
            case USER:
                id = 1L;
                break;
            case ADMIN:
                id = 2L;
                break;
            default:
                id = 99L;
        }
        
        return new RuoloDTO(id, ruolo.name());
    }
	
    UtenteResponse toResponse(Utente utente);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "attivo", ignore = true)
    @Mapping(target = "resetPasswordToken", ignore = true)
    @Mapping(target = "resetPasswordTokenExpiry", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    Utente toEntity(UtenteRequest utenteRequest);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "codiceFiscale", ignore = true)
    @Mapping(target = "resetPasswordToken", ignore = true)
    @Mapping(target = "resetPasswordTokenExpiry", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    @Mapping(target = "attivo", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromUpdateRequest(UtenteUpdateRequest updateRequest, @MappingTarget Utente utente);
}