package com.gestione_utenti.model;

import java.time.*;
import java.util.*;
import org.hibernate.annotations.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "utenti")
@ToString(exclude = {"password", "refreshToken", "resetPasswordToken"})
@EqualsAndHashCode
@Builder
public class Utente implements UserDetails
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotBlank
	private String nome;
	
	@NotBlank
	private String cognome;
	
	@NotBlank
	@Column(name = "codice_fiscale", unique = true)
	private String codiceFiscale;
	
	@PastOrPresent
	@NotNull
	@Column(name = "data_di_nascita")
	private LocalDate dataNascita;
	
	@Enumerated(EnumType.STRING)
	private Ruolo ruolo;
	
	private String password;
	
	@Column(length = 255)
    private String resetPasswordToken;

    private LocalDateTime resetPasswordTokenExpiry;

    @Column(length = 500)
    private String refreshToken;
    
    @Builder.Default 
    @Column(nullable = false)
    private Boolean attivo = true;

    @Column(length = 20)
    private String provider;

    @Column(length = 100)
    private String providerId;
    
    @CreationTimestamp 
    private LocalDateTime dataRegistrazione;
    
    @UpdateTimestamp 
    private LocalDateTime dataUltimoAggiornamento;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() 
	{
		return List.of(new SimpleGrantedAuthority("ROLE_" + ruolo.name().toUpperCase()));
	}

	@Override
	public String getUsername()
	{
		return codiceFiscale;
	}

	@Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return attivo; }
    
    @Override
	public @Nullable String getPassword() 
	{
		return password;
	}
}
