package com.gestione_utenti.dto.response;

import com.gestione_utenti.dto.response.RuoloDTO;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class RuoloDTO
{
    private Long id;
    private String nome;
}