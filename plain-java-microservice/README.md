# Plain Java mikrotjeneste

Dette er et eksempel på en enkel mikrotjeneste skrevet i plain Java uten rammeverk som Spring Boot. Tjenesten bruker Jetty som embedded HTTP server og Micrometer for metrics. Tjenesten har to viktige endepunkter:

- `/health` – Sjekker om tjenesten lever og svarer (helsesjekk)
- `/metrics` – Eksponerer måledata (metrics) i Prometheus-format for overvåkning

## Hvordan fungerer det?

Tjenesten er bygget med plain Java og bruker Jetty som embedded webserver. Vi bruker Micrometer og Prometheus registry for å eksponere måledata.

### Endepunkter
- **/health**: Returnerer `{ "status": "Healthy" }` hvis tjenesten kjører som den skal. Teller også antall requests.
- **/metrics**: Viser statistikk og måledata i Prometheus-format.

## Teknologier
- Java 21
- Jetty 11 som embedded server
- Micrometer for metrics
- Maven for bygging
- Docker for containerisering

## Bygging og kjøring

### 1. Bygg med Maven
```bash
mvn clean package
```

Dette lager en kjørbar JAR-fil i `target/`-mappen.

### 2. Kjør lokalt
Du kan starte tjenesten direkte med:

```bash
java -jar target/plain-java-microservice-1.0-SNAPSHOT.jar
```

### 3. Bygg og kjør med Docker

## Multi-stage build for denne tjenesten

Denne tjenesten bruker en multi-stage Dockerfile for å bygge og pakke applikasjonen på en effektiv og sikker måte:

1. **Byggesteg:**
	- Starter fra et image med Maven og JDK (maven:3-eclipse-temurin-21).
	- Bygger prosjektet og lager en JAR-fil.
2. **Runtime-steg:**
	- Starter fra et rent og lite JRE-image (eclipse-temurin:21-jre).
	- Kopierer kun den ferdige JAR-filen fra byggesteg.
	- Ingen byggverktøy, kildekode eller unødvendige filer blir med i sluttbildet.

**Fordeler:**
- Sluttbildet blir mye mindre og sikrere.
- Kun det som trengs for å kjøre applikasjonen er med.
- Raskere deploy og mindre angrepsflate.

**Eksempel fra Dockerfile:**
```dockerfile
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/plain-java-microservice-1.0-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Bygg og kjør slik:

```bash
docker build -t plain-java-microservice .
docker run -p 8082:8082 plain-java-microservice
```

### 4. Bruk med docker-compose
Hvis du vil starte alle tjenestene samtidig, bruk `docker-compose` fra rotkatalogen.

## Docker Image størrelser

- Build stage: Inkluderer Maven og JDK for kompilering
- Runtime stage: Kun JRE og JAR-filen for minimal størrelse

## Sammenligning med Spring Boot

Denne plain Java versjonen demonstrerer hvordan man kan bygge en mikrotjeneste uten tunge rammeverk, noe som kan føre til mindre og raskere images, men krever mer manuell kode for funksjonalitet som Spring Boot gir ut av boksen.