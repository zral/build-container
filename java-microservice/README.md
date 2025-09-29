
# Java mikrotjeneste med Spring Boot

Dette er et eksempel på en enkel mikrotjeneste skrevet i Java med Spring Boot. Tjenesten har to viktige endepunkter:

- `/health` – Sjekker om tjenesten lever og svarer (helsesjekk)
- `/actuator/prometheus` – Eksponerer måledata (metrics) i Prometheus-format for overvåkning

## Hvordan fungerer det?

Tjenesten er bygget med Spring Boot, som gjør det enkelt å lage webtjenester i Java. Vi bruker også Spring Actuator og Micrometer for å eksponere måledata til Prometheus.

### Endepunkter
- **/health**: Returnerer `{ "status": "Healthy" }` hvis tjenesten kjører som den skal.
- **/actuator/prometheus**: Viser statistikk og måledata som Prometheus (et overvåkningsverktøy) kan hente ut.

## Bygging og kjøring

### 1. Bygg med Maven Wrapper
Prosjektet inkluderer Maven Wrapper (`mvnw` og `.mvn/`). Dette gjør at du kan bygge prosjektet uten å installere Maven selv. Kjør følgende kommandoer fra denne katalogen:

```bash
./mvnw clean package
```

Dette lager en kjørbar JAR-fil i `target/`-mappen.

### 2. Kjør lokalt
Du kan starte tjenesten direkte med:

```bash
./mvnw spring-boot:run
```

### 3. Bygg og kjør med Docker

## Multi-stage build for denne tjenesten

Denne tjenesten bruker en multi-stage Dockerfile for å bygge og pakke applikasjonen på en effektiv og sikker måte:

1. **Byggesteg:**
	- Starter fra et image med Maven og JDK (eclipse-temurin:21-jdk).
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
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/java-microservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Bygg og kjør slik:

```bash
docker build -t java-microservice .
docker run -p 8081:8081 java-microservice
```

### 4. Bruk med docker-compose
Hvis du vil starte både Java- og C#-tjenesten samtidig, bruk `docker-compose` fra rotkatalogen.


## Hva er Maven og Maven Wrapper?

**Maven** er et verktøy som hjelper deg å bygge, kjøre og holde orden på Java-prosjekter. Tenk på det som en "oppskriftsbok" for prosjektet ditt:
- Det vet hvilke biblioteker (eksterne kodepakker) du trenger, og laster dem ned automatisk.
- Det vet hvordan prosjektet skal bygges, pakkes og kjøres.
- Du slipper å laste ned og organisere filer selv – Maven gjør det for deg.

**Maven Wrapper** (`mvnw` og `.mvn/`-mappen) gjør at alle kan bygge prosjektet med samme Maven-versjon, uansett hva som er installert på maskinen. Du trenger bare Java installert – resten ordner wrapperen.

## Oppsummering
- Tjenesten har helsesjekk og Prometheus-metrics
- Kan bygges og kjøres med Maven Wrapper eller Docker
- Maven Wrapper gjør prosjektet enkelt å bygge for alle, også nybegynnere
