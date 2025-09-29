
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
Prosjektet har en Dockerfile som bruker multi-stage build for å lage et lite og sikkert image. Bygg og kjør slik:

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
