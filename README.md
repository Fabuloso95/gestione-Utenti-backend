Gestione Utenti - Backend (Spring Boot)

Questo modulo contiene l'implementazione del backend RESTful per la gestione degli utenti, sviluppato secondo i requisiti dell'Hands-On Tecnico.

Architettura

Linguaggio: Java

Framework: Spring Boot 3+

Database: Mysql

Sicurezza: Spring Security (JWT, Authentication & Authorization con ruoli ADMIN/USER)

Testing: JUnit 5, Mockito, Spring Boot Test

Funzionalità Implementate

CRUD Completo: Creazione, Lettura, Aggiornamento ed Eliminazione di utenti.

Ricerca: Query per nome, cognome e codice fiscale.

Validazione: Uso di @Valid e gestione degli errori centralizzata tramite @ControllerAdvice.

Sicurezza: Autenticazione con login e token JWT. Endpoint protetti con @PreAuthorize("hasRole('ADMIN')").

Endpoint API Principali

Metodo

URL

Descrizione

Autorizzazione

POST

/api/v1/auth/login

Login utente (restituisce JWT e Refresh Token)

permitAll

POST

/api/v1/auth/register

Registrazione nuovo utente

permitAll

POST

/api/v1/utenti

Crea un nuovo utente

ADMIN

GET

/api/v1/utenti/{id}

Ottieni utente per ID

Authenticated

GET

/api/v1/utenti

Ottieni tutti gli utenti

Authenticated

PUT

/api/v1/utenti/{id}

Aggiorna un utente esistente

ADMIN

DELETE

/api/v1/utenti/{id}

Elimina un utente

ADMIN

GET

/api/v1/utenti/search?query=...

Ricerca per campi (Nome, Cognome, CF)

Authenticated

Credenziali di Default

L'applicazione crea un utente Amministratore all'avvio:

Codice Fiscale: CFADMIN0000X

Password: adminpassword6£

Come eseguire il Backend

1. Esecuzione Locale (Java)

Assicurati di avere Java 17+ e Maven/Gradle installati.

Compila il progetto:

# Se usi Maven
./mvnw clean package

# Se usi Gradle
./gradlew clean build


Esegui il JAR:

java -jar target/gestione-utenti-0.0.1-SNAPSHOT.jar 
# (Adatta il nome del file JAR)


2. Esecuzione con Docker

(Vedi il Dockerfile per i dettagli e i comandi di build/run.)
