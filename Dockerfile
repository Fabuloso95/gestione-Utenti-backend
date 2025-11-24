# Dockerfile per l'applicazione Spring Boot Gestione Utenti

# 1. Fase di Build (Usa un'immagine base con JDK per compilare l'applicazione)
# Utilizziamo Eclipse Temurin per la sua leggerezza e licenza open-source
FROM eclipse-temurin:17-jdk-focal as builder

# Imposta la directory di lavoro
WORKDIR /app

# Copia i file di build (pom.xml per Maven o build.gradle per Gradle)
COPY pom.xml .

# Scarica le dipendenze per velocizzare la build successiva (Layer Caching)
# Se usi Gradle, sostituisci con 'COPY build.gradle settings.gradle . && ./gradlew dependencies'
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

# Copia il codice sorgente
COPY src src

# Compila l'applicazione e crea il JAR
# Usa --mount=type=cache,target=/root/.m2 per caching veloce dei pacchetti Maven
RUN --mount=type=cache,target=/root/.m2 mvn clean install -DskipTests

# 2. Fase di Runtime (Usa un'immagine JRE più piccola per l'esecuzione)
FROM eclipse-temurin:17-jre-focal

# Etichetta il maintainer
LABEL maintainer="[Il tuo nome/email]"

# Imposta la directory di lavoro
WORKDIR /app

# Copia il JAR compilato dalla fase di build
# Il percorso del JAR varia in base al nome del tuo progetto
# Di solito è target/<nome-progetto>-<versione>.jar.
# Assumi che il nome del tuo JAR sia app.jar per semplicità
COPY --from=builder /app/target/*.jar app.jar

# Espone la porta di default di Spring Boot
EXPOSE 8080

# Comando per avviare l'applicazione quando il container parte
# Non usare 'java -jar' per un'immagine JRE, usa il comando 'java $JAVA_OPTS -jar'
# Aggiungiamo JAVA_OPTS per ottimizzazioni della JVM nel container
ENTRYPOINT ["java", "-jar", "app.jar"]
