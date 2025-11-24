package com.gestione_utenti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestione_utenti.dto.request.UtenteRequest;
import com.gestione_utenti.dto.request.UtenteUpdateRequest;
import com.gestione_utenti.model.Ruolo;
import com.gestione_utenti.model.Utente;
import com.gestione_utenti.repository.UtenteRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UtenteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UtenteRepository utenteRepository;

    private final String ADMIN_CF = "CFADMIN0000X";

    private String generateUniqueCodiceFiscale() {
        String timestampSuffix = String.valueOf(System.nanoTime());
        return "GSTONI" + timestampSuffix.substring(timestampSuffix.length() - 10);
    }

    @BeforeEach
    void setUp() {
        utenteRepository.deleteAll();

        // Crea SOLO l'admin di default
        Utente admin = Utente.builder()
            .nome("Admin")
            .cognome("Default")
            .codiceFiscale(ADMIN_CF)
            .dataNascita(LocalDate.of(1990, 1, 1))
            .ruolo(Ruolo.ADMIN)
            .password("encoded-password")
            .attivo(true)
            .build();
        utenteRepository.save(admin);
    }

    // --- TEST 1: CREAZIONE UTENTE (ADMIN) ---
    @Test
    @Order(1)
    @WithMockUser(username = "CFADMIN0000X", roles = {"ADMIN"})
    void test01_creaUtente_Admin_Success() throws Exception {
        String uniqueCF = generateUniqueCodiceFiscale();
        
        UtenteRequest utenteRequest = UtenteRequest.builder()
            .nome("Mario")
            .cognome("Rossi")
            .codiceFiscale(uniqueCF)
            .dataNascita(LocalDate.of(1980, 10, 25))
            .password("Password123$")
            .ruolo(Ruolo.USER)
            .build();

        String json = objectMapper.writeValueAsString(utenteRequest);

        mockMvc.perform(post("/api/v1/utenti")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Mario"))
                .andExpect(jsonPath("$.cognome").value("Rossi"))
                .andExpect(jsonPath("$.ruolo").value("USER"));
    }

    // --- TEST 2: CREAZIONE UTENTE (USER FORBIDDEN) ---
    @Test
    @Order(2)
    @WithMockUser(username = "USER123", roles = {"USER"})
    void test02_creaUtente_User_Forbidden() throws Exception {
        UtenteRequest utenteRequest = UtenteRequest.builder()
            .nome("Pino")
            .cognome("Verdi")
            .codiceFiscale(generateUniqueCodiceFiscale())
            .dataNascita(LocalDate.of(1990, 1, 1))
            .password("Password123$")
            .ruolo(Ruolo.USER)
            .build();

        String json = objectMapper.writeValueAsString(utenteRequest);

        mockMvc.perform(post("/api/v1/utenti")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden()); // ✅ 403 invece di 500
    }

    // --- TEST 3: GET UTENTE BY ID - CREA PRIMA ---
    @Test
    @Order(3)
    @WithMockUser(username = "CFADMIN0000X", roles = {"ADMIN"})
    void test03_ottieniUtente_Success() throws Exception {
        // PRIMA crea un utente
        String uniqueCF = generateUniqueCodiceFiscale();
        Utente utente = Utente.builder()
            .nome("Test")
            .cognome("User")
            .codiceFiscale(uniqueCF)
            .dataNascita(LocalDate.of(1985, 5, 15))
            .ruolo(Ruolo.USER)
            .password("password")
            .attivo(true)
            .build();
        Utente saved = utenteRepository.save(utente);

        // POI cerca
        mockMvc.perform(get("/api/v1/utenti/{id}", saved.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.nome").value("Test"));
    }

    // --- TEST 4: GET ALL UTENTI ---
    @Test
    @Order(4)
    @WithMockUser(username = "ANY_USER", roles = {"USER"})
    void test04_ottieniTuttiGliUtenti_Success() throws Exception {
        mockMvc.perform(get("/api/v1/utenti")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Solo l'admin di default
                .andExpect(jsonPath("$[0].nome").value("Admin"));
    }

    // --- TEST 5: RICERCA UTENTI ---
    @Test
    @Order(5)
    @WithMockUser(username = "ANY_USER", roles = {"USER"})
    void test05_cercaUtenti_Success() throws Exception {
        // Crea un utente da cercare
        Utente utente = Utente.builder()
            .nome("Andrea")
            .cognome("Bianchi")
            .codiceFiscale(generateUniqueCodiceFiscale())
            .dataNascita(LocalDate.of(1990, 1, 1))
            .ruolo(Ruolo.USER)
            .password("password")
            .attivo(true)
            .build();
        utenteRepository.save(utente);

        // Cerca "Andrea"
        mockMvc.perform(get("/api/v1/utenti/search")
                .param("query", "Andrea")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Andrea"));
    }

    // --- TEST 6: UPDATE UTENTE - CREA PRIMA ---
    @Test
    @Order(6)
    @WithMockUser(username = "CFADMIN0000X", roles = {"ADMIN"})
    void test06_aggiornaUtente_Success() throws Exception {
        // PRIMA crea un utente
        Utente utente = Utente.builder()
            .nome("Mario")
            .cognome("Rossi")
            .codiceFiscale(generateUniqueCodiceFiscale())
            .dataNascita(LocalDate.of(1980, 10, 25))
            .ruolo(Ruolo.USER)
            .password("password")
            .attivo(true)
            .build();
        Utente saved = utenteRepository.save(utente);
        
        // POI aggiorna
        UtenteUpdateRequest updateRequest = UtenteUpdateRequest.builder()
            .nome("Andrea")
            .cognome("Rossi Aggiornato")
            .dataNascita(LocalDate.of(1980, 10, 25))
            .ruolo(Ruolo.ADMIN)
            .build();

        String json = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/api/v1/utenti/{id}", saved.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Andrea"))
                .andExpect(jsonPath("$.ruolo").value("ADMIN"));
    }

    // --- TEST 7: DELETE UTENTE - CREA PRIMA ---
    @Test
    @Order(7)
    @WithMockUser(username = "CFADMIN0000X", roles = {"ADMIN"})
    void test07_eliminaUtente_Success() throws Exception {
        // PRIMA crea un utente
        Utente utente = Utente.builder()
            .nome("DaEliminare")
            .cognome("Test")
            .codiceFiscale(generateUniqueCodiceFiscale())
            .dataNascita(LocalDate.of(1990, 1, 1))
            .ruolo(Ruolo.USER)
            .password("password")
            .attivo(true)
            .build();
        Utente saved = utenteRepository.save(utente);

        // POI elimina
        mockMvc.perform(delete("/api/v1/utenti/{id}", saved.getId())
                .with(csrf()))
                .andExpect(status().isNoContent());

        // Verifica eliminazione
        assertThat(utenteRepository.findById(saved.getId())).isEmpty();
    }

    // --- TEST 8: UTENTE NON TROVATO ---
    @Test
    @Order(8)
    @WithMockUser(username = "ANY_USER", roles = {"USER"})
    void test08_ottieniUtente_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/utenti/{id}", 99999L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // --- TEST 9: DELETE FORBIDDEN PER USER ---
    @Test
    @Order(9)
    @WithMockUser(username = "USER123", roles = {"USER"})
    void test09_eliminaUtente_User_Forbidden() throws Exception {
        // Crea un utente temporaneo
        Utente tempUser = Utente.builder()
            .nome("Temp")
            .cognome("User")
            .codiceFiscale(generateUniqueCodiceFiscale())
            .dataNascita(LocalDate.now())
            .ruolo(Ruolo.USER)
            .password("temp-pass")
            .attivo(true)
            .build();
        Utente savedUser = utenteRepository.save(tempUser);

        mockMvc.perform(delete("/api/v1/utenti/{id}", savedUser.getId())
                .with(csrf()))
                .andExpect(status().isForbidden()); // ✅ 403 invece di 204
    }
}