# Bygg-container: C#, Java Spring Boot, Plain Java og Golang mikrotjenester med Prometheus-målinger

## Oversikt
Dette prosjektet demonstrerer hvordan man bygger og kjører fire "like" mikrotjenester med ulik teknologistakk (C#, Java Spring Boot, Plain Java og Golang) i et containerisert miljø ved hjelp av Docker og docker-compose. 

Løsningen bruker dedikerte build-containere for å bygge applikasjonene, og deployer kun nødvendige runtime-filer i produksjonscontainerne. Alle mikrotjenestene eksponerer `/health`-endpoints for helsesjekk og Prometheus-kompatible metrics-endpoints for overvåkning.

Det er tatt med [målinger av ressursbruk og effektivitet](#byggetid-og-størrelsesforskjell-på-images-og-teknologistakkene), og sammenlignet på tvers av teknologistakkene både for bygg og kjøring.

## Struktur

```
.
├── csharp-microservice/     # C# ASP.NET Core mikrotjeneste
├── java-microservice/       # Java Spring Boot mikrotjeneste  
├── plain-java-microservice/ # Plain Java med Jetty mikrotjeneste
├── golang-microservice/     # Go (Golang) mikrotjeneste
├── docker-compose.yml       # Orchestrering av alle tjenester
├── prometheus.yml           # Prometheus konfigurasjon
└── README.md                # Denne filen
```

## Hvordan fungerer løsningen?


Alle mikrotjenestene eksponerer Prometheus-kompatible metrics-endpoints:
- `/metrics` for C#, Plain Java og Golang
- `/actuator/prometheus` for Java Spring Boot

### 1. C# mikrotjeneste
Ligger i `csharp-microservice/`. Bruker ASP.NET Core og `prometheus-net` for Prometheus-målinger.

**Teknologi:**
- ASP.NET Core 8
- prometheus-net.AspNetCore

**Endpoints:**
- `/health` – Helsesjekk
- `/metrics` – Prometheus metrics

**Beskrivelse:**
En moderne .NET-mikrotjeneste med innebygd støtte for Prometheus-målinger og enkel helsesjekk. Multi-stage Dockerfile gir liten og sikker runtime-container.


### 2. Java Spring Boot mikrotjeneste
Ligger i `java-microservice/`. Bruker Spring Boot med Micrometer og Actuator for Prometheus-målinger.

**Teknologi:**
- Spring Boot 3
- Micrometer
- Spring Boot Actuator

**Endpoints:**
- `/health` – Helsesjekk
- `/actuator/prometheus` – Prometheus metrics

**Beskrivelse:**
Standard Spring Boot-mikrotjeneste med ferdig metrics- og helsesjekk via Actuator. Multi-stage Dockerfile gir liten og sikker runtime-container.



### 3. Plain Java mikrotjeneste
Ligger i `plain-java-microservice/`. Bruker plain Java med Jetty embedded server og Micrometer for Prometheus-målinger.

**Teknologi:**
- Java 21
- Jetty embedded server
- Micrometer

**Endpoints:**
- `/health` – Helsesjekk
- `/metrics` – Prometheus metrics

**Beskrivelse:**
Demonstrerer hvordan man kan bygge en mikrotjeneste uten tunge rammeverk som Spring Boot, kun med embedded HTTP-server og manuell metrics-integrasjon. Tjenesten bygges og kjøres nå med Java 21. Multi-stage Dockerfile gir liten og sikker runtime-container.


### 4. Golang mikrotjeneste
Ligger i `golang-microservice/`. En enkel Go-basert mikrotjeneste med Prometheus-målinger.

**Teknologi:**
- Go 1.22
- prometheus/client_golang

**Endpoints:**
- `/health` – Helsesjekk
- `/metrics` – Prometheus metrics

**Beskrivelse:**
Minimal mikrotjeneste skrevet i Go, med Prometheus-integrasjon og multi-stage Dockerfile for små og sikre containere. Viser hvor enkelt det er å lage en observérbar mikrotjeneste i Go.



## Hva er multi-stage build?

Multi-stage build er en teknikk i Docker hvor man definerer flere "steg" (stages) i én og samme Dockerfile. Hvert steg kan bruke et eget base-image og har sitt eget filsystem. Dette gjør det mulig å bygge applikasjonen i et image med alle nødvendige byggverktøy (f.eks. Maven, .NET SDK), og deretter kopiere kun de ferdige artefaktene (f.eks. JAR, DLL) over i et nytt, mye mindre image som kun inneholder det som trengs for å kjøre applikasjonen (f.eks. JRE, ASP.NET runtime).

**Fordeler med multi-stage build:**
- Sluttbildet (runtime-image) blir mye mindre, fordi det ikke inneholder byggverktøy, SDK-er eller kildekode.
- Redusert angrepsflate og bedre sikkerhet.
- Raskere deploy og mindre båndbreddebruk.
- Man kan bruke forskjellige base-images for bygg og runtime (f.eks. bygge med Maven/JDK, kjøre med kun JRE).

**Eksempel på prinsipp:**
```
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```
I første steg bygges prosjektet, i andre steg kopieres kun den ferdige JAR-filen inn i et rent runtime-image.

Alle fire mikrotjenestene i dette prosjektet bruker multi-stage builds for å sikre små og sikre runtime-containere uten byggeverktøy.

---



### 5. docker-compose
`docker-compose.yml` starter alle mikrotjenestene:
- C#-tjenesten på port 8080
- Java Spring Boot-tjenesten på port 8081
- Plain Java-tjenesten på port 8082
- Golang-tjenesten på port 8083



### 6. Prometheus-integrasjon
`prometheus.yml` viser hvordan Prometheus kan konfigureres til å scrape alle tjenestene:
- C#: `/metrics` på port 8080
- Java Spring Boot: `/actuator/prometheus` på port 8081
- Plain Java: `/metrics` på port 8082
- Golang: `/metrics` på port 8083


## Kom i gang

1. Bygg og start begge mikrotjenestene:
	```bash
	docker-compose up --build
	```
2. Åpne i nettleser:
	- C# helsesjekk: [http://localhost:8080/health](http://localhost:8080/health)
	- C# metrics: [http://localhost:8080/metrics](http://localhost:8080/metrics)
	- Java helsesjekk: [http://localhost:8081/health](http://localhost:8081/health)
	- Java metrics: [http://localhost:8081/actuator/prometheus](http://localhost:8081/actuator/prometheus)
	- Plain Java helsesjekk: [http://localhost:8082/health](http://localhost:8082/health)
	- Plain Java metrics: [http://localhost:8082/metrics](http://localhost:8082/metrics)

3. (Valgfritt) Start Prometheus med `prometheus.yml` for å samle inn målinger fra begge tjenester.

1. Bygg og start alle mikrotjenestene:
```bash
docker-compose up --build
```


2. Åpne i nettleser:
	- **C#**
		- Helsesjekk: [http://localhost:8080/health](http://localhost:8080/health)
		- Metrics: [http://localhost:8080/metrics](http://localhost:8080/metrics)
	- **Java Spring Boot**
		- Helsesjekk: [http://localhost:8081/health](http://localhost:8081/health)
		- Metrics: [http://localhost:8081/actuator/prometheus](http://localhost:8081/actuator/prometheus)
	- **Plain Java**
		- Helsesjekk: [http://localhost:8082/health](http://localhost:8082/health)
		- Metrics: [http://localhost:8082/metrics](http://localhost:8082/metrics)
	- **Golang**
		- Helsesjekk: [http://localhost:8083/health](http://localhost:8083/health)
		- Metrics: [http://localhost:8083/metrics](http://localhost:8083/metrics)

3. (Valgfritt) Start Prometheus med `prometheus.yml` for å samle inn målinger fra alle tre tjenester.

## Læringspunkter
* Multi-stage builds gir små og sikre containere
* Separasjon av bygg og runtime
* Eksponering av helse- og måle-endepunkter
* Enkel integrasjon med Prometheus for overvåkning


## Byggetid og størrelsesforskjell på images og teknologistakkene
Ved å bruke multi-stage builds blir sluttresultatet betydelig mindre. Her er faktiske målinger fra siste build (september 2025):

| Tjeneste                | Byggetid | Build-image | Runtime-image | Reduksjon |
|-------------------------|----------|-------------|---------------|-----------|
| **C#**                  | 25s      | 856MB       | 88MB          | 768MB (90%) |
| **Java Spring Boot**    | 43s      | 587MB       | 121MB         | 466MB (79%) |
| **Plain Java**          | 27s      | 696MB       | 102MB         | 594MB (85%) |
| **Golang**              | 11s      | 345MB       | 11.5MB        | 333.5MB (97%) |

Dette viser hvor mye plass man sparer ved å kun ta med nødvendige runtime-filer i produksjonscontaineren, og utelate alle byggverktøy og SDK-er. I tillegg reduseres angrepsflaten betraktelig, fordi runtime-imaget ikke inneholder byggeverktøy eller utviklingsverktøy som potensielt kan utnyttes av angripere. Dette gir en sikrere produksjonscontainer.

Det er imidlertid store forskjeller mellom de ulike teknologistakkene når det gjelder både byggetid og størrelsen på runtime-image:

- **C#** har rask byggetid (25s) og et svært lite runtime-image (88MB) sammenlignet med build-image (856MB). .NETs publiseringsprosess og multi-stage build gir et effektivt sluttresultat.

- **Java Spring Boot** har lengst byggetid (43s) og et større runtime-image (121MB). Dette skyldes at Spring Boot-applikasjoner inkluderer mange avhengigheter og at JVM-runtime er større enn for Go og .NET. Build-image er også stort (587MB), men multi-stage build kutter mye.

- **Plain Java** har byggetid og runtime-image mellom C# og Spring Boot. Her er det færre rammeverk og mindre overhead enn Spring Boot, men fortsatt JVM-basert.

- **Golang** skiller seg ut med ekstremt liten runtime-image (10.5MB) og rask byggetid (11s). Go kompilerer til én statisk binærfil uten runtime-avhengigheter, og sluttbildet inneholder kun denne og et minimalt base-image (alpine). Build-image er større (~370MB), men dette påvirker kun byggeprosessen, ikke produksjonsmiljøet.

**Oppsummert:**
- Go gir overlegent minst runtime-image og raskest bygg, ideelt for små, raske containere.
- C# og Plain Java gir små og sikre runtime-images, men med noe lengre byggetid.
- Java Spring Boot gir størst runtime-image og lengst byggetid, men er til gjengjeld svært fleksibelt og utvidbart.

Valg av teknologi bør avhenge av krav til ytelse, sikkerhet, image-størrelse og hvor mye rammeverk og funksjonalitet man trenger.

## Ytelsesmålinger: Oppstartstid, minnebruk og svartid

Resultater fra automatisert test (1000 kall mot /health, september 2025):

| Tjeneste                | Oppstartstid | Minnebruk         | Gj.snitt per kall |
|-------------------------|--------------|-------------------|-------------------|
| **C#**                  | 628 ms       | 40.66 MiB         | 0.0066 s          |
| **Java Spring Boot**    | 1669 ms      | 186.1 MiB         | 0.0073 s          |
| **Plain Java**          | 627 ms       | 72.05 MiB         | 0.0072 s          |
| **Golang**              | 20 ms        | 1.9 MiB           | 0.0069 s          |

### Drøfting av resultatene

- **Oppstartstid:**
	- Golang-tjenesten starter ekstremt raskt (20 ms), langt raskere enn de andre. Dette skyldes at Go kompilerer til én statisk binærfil uten JVM eller runtime-overhead.
	- C# og Plain Java har begge lav oppstartstid (~600 ms), mens Java Spring Boot bruker lengst tid (1669 ms) grunnet initialisering av Spring-rammeverket.

- **Minnebruk:**
	- Golang bruker desidert minst minne (~6 MiB). Plain Java og C# bruker moderat minne, mens Java Spring Boot bruker mest (186 MiB), hovedsakelig pga. rammeverk og avhengigheter.

- **Gjennomsnittstid per kall:**
	- Alle tjenester leverer svært lav svartid per kall (~0.007 s), med små forskjeller. Dette viser at alle løsningene håndterer enkle HTTP-kall effektivt under lav belastning.

- **Helhet:**
	- Go utmerker seg på ressursbruk og oppstart, men alle løsningene gir god ytelse for enkle mikrotjenester. Valg av teknologi bør baseres på teamets kompetanse, behov for rammeverk, og krav til ressursbruk og oppstartstid.
