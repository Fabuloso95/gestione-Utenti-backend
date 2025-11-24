package com.gestione_utenti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gestione_utenti.dto.request.LoginRequest;
import com.gestione_utenti.dto.request.RefreshTokenRequestDTO;
import com.gestione_utenti.dto.request.RegistrazioneRequest;
import com.gestione_utenti.model.Utente;
import com.gestione_utenti.repository.UtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc 
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test") 
public class AuthControllerTest 
{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UtenteRepository utenteRepository; 

    private static final String ADMIN_CF = "CFADMIN0000X";
    private static final String ADMIN_PASS = "adminpassword6£";
    private static final String NEW_USER_CF = "LNRFBA95E07B114X";
    private static String globalRefreshToken;

    @BeforeEach
    void setUp() 
    {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @Order(1)
    public void testLogin_Success_Admin() throws Exception 
    {
        LoginRequest loginRequest = new LoginRequest(ADMIN_CF, ADMIN_PASS);
        String json = objectMapper.writeValueAsString(loginRequest);

        String responseContent = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.codiceFiscale").value(ADMIN_CF))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ruolo").value("ADMIN"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        globalRefreshToken = objectMapper.readTree(responseContent).get("refreshToken").asText();
    }
    
    @Test
    @Order(2)
    public void testLogin_Failure_WrongPassword() throws Exception 
    {
        LoginRequest loginRequest = new LoginRequest(ADMIN_CF, "wrongpassword");
        String json = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("INVALID_CREDENTIALS"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(3)
    public void testRegister_Success() throws Exception 
    {
        // Utilizziamo i dati forniti dall'utente e il CF unico
        RegistrazioneRequest registrationRequest = RegistrazioneRequest.builder()
                .nome("Fabio")
                .cognome("Leonardi")
                .codiceFiscale(NEW_USER_CF) // Utilizza il CF unico definito sopra
                .dataNascita(LocalDate.of(1995, 5, 7)) // La data corretta
                .password("Fabiosky95$")
                .build();
        String json = objectMapper.writeValueAsString(registrationRequest);

        // Eseguiamo la richiesta e stampiamo sempre la risposta per diagnosi
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print()) // Stampa il risultato PRIMA dell'asserzione
                .andExpect(MockMvcResultMatchers.status().isCreated()) // Ci aspettiamo 201
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Fabio"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ruolo").value("USER"));
        
        // Verifica del salvataggio nel database
        Utente newUser = utenteRepository.findByCodiceFiscale(NEW_USER_CF).orElseThrow(() -> new AssertionError("Utente non trovato dopo la registrazione riuscita."));
        assertThat(newUser).isNotNull();
        // Aggiungi qui altre asserzioni come isAttivo(), ecc.
    }
    
    @Test
    @Order(4)
    @WithUserDetails(value = ADMIN_CF, userDetailsServiceBeanName = "customUserDetailsService")
    public void testGetCurrentUser_AsAdmin_Success() throws Exception 
    {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/me"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Admin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ruolo").value("ADMIN"))
                .andDo(MockMvcResultHandlers.print());
    }
    
    @Test
    @Order(5)
    public void testRefreshToken_Success() throws Exception 
    {
        RefreshTokenRequestDTO refreshTokenRequest = new RefreshTokenRequestDTO(globalRefreshToken);
        String json = objectMapper.writeValueAsString(refreshTokenRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").exists())
                .andDo(MockMvcResultHandlers.print());
    }
    
    @Test
    @Order(6)
    public void testLogout_Success() throws Exception 
    {
        RefreshTokenRequestDTO refreshTokenRequest = new RefreshTokenRequestDTO(globalRefreshToken);
        String json = objectMapper.writeValueAsString(refreshTokenRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/logout")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}