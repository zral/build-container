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
Prosjektet har en Dockerfile som bruker multi-stage build for å lage et lite og sikkert image. Tjenesten bygges og kjøres nå med Java 21. Bygg og kjør slik:

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