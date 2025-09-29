
# Bygg-container: C#, Java og Plain Java mikrotjenester med Prometheus-målinger

## Oversikt
Dette prosjektet demonstrerer hvordan man bygger og kjører tre forskjellige mikrotjenester (C#, Java Spring Boot og Plain Java) i et containerisert miljø ved hjelp av Docker og docker-compose. Løsningen bruker dedikerte build-containere for å bygge applikasjonene, og deployer kun nødvendige runtime-filer i produksjonscontainerne. Alle mikrotjenestene eksponerer `/health`-endpoints for helsesjekk og Prometheus-kompatible metrics-endpoints for overvåkning.

## Struktur

```
.
├── csharp-microservice/     # C# ASP.NET Core mikrotjeneste
├── java-microservice/       # Java Spring Boot mikrotjeneste  
├── plain-java-microservice/ # Plain Java med Jetty mikrotjeneste
├── docker-compose.yml       # Orchestrering av alle tjenester
├── prometheus.yml           # Prometheus konfigurasjon
└── README.md                # Denne filen
```

## Hvordan fungerer løsningen?

Alle mikrotjenestene eksponerer Prometheus-kompatible metrics-endpoints:
- `/metrics` for C# og Plain Java
- `/actuator/prometheus` for Java Spring Boot


### 1. C# mikrotjenesten

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

### 3. Plain Java mikrotjenesten

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


### 3. Dockerfile (multi-stage build)
Alle tre mikrotjenestene bruker multi-stage builds for å sikre små og sikre runtime-containere uten byggeverktøy.


### 4. docker-compose
`docker-compose.yml` starter alle mikrotjenestene:
- C#-tjenesten på port 8080
- Java Spring Boot-tjenesten på port 8081
- Plain Java-tjenesten på port 8082


### 5. Prometheus-integrasjon
`prometheus.yml` viser hvordan Prometheus kan konfigureres til å scrape alle tjenestene:
- C#: `/metrics` på port 8080
- Java Spring Boot: `/actuator/prometheus` på port 8081
- Plain Java: `/metrics` på port 8082


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

3. (Valgfritt) Start Prometheus med `prometheus.yml` for å samle inn målinger fra alle tre tjenester.

## Læringspunkter
* Multi-stage builds gir små og sikre containere
* Separasjon av bygg og runtime
* Eksponering av helse- og måle-endepunkter
* Enkel integrasjon med Prometheus for overvåkning

## Størrelsesforskjell på images
Ved å bruke multi-stage builds blir sluttresultatet betydelig mindre:


- **C# bygge-image** (`build-container:build`): 856MB
- **C# runtime-image** (`build-container:runtime`): 218MB

- **Java bygge-image** (`java-microservice:build`): 587MB
- **Java runtime-image** (`java-microservice:runtime`): 312MB

- **Plain Java bygge-image**: 696MB
- **Plain Java runtime-image**: 267MB

Dette viser hvor mye plass man sparer ved å kun ta med nødvendige runtime-filer i produksjonscontaineren, og utelate alle byggverktøy og SDK-er.
 I tillegg reduseres angrepsflaten betraktelig, fordi runtime-imaget ikke inneholder byggeverktøy eller utviklingsverktøy som potensielt kan utnyttes av angripere. Dette gir en sikrere produksjonscontainer.

**Sammenligning av space-besparelser med multi-stage builds:**
- C#: 856MB → 218MB (638MB spart, 75% reduksjon)
- Java Spring Boot: 587MB → 312MB (275MB spart, 47% reduksjon)  
- Plain Java: 696MB → 267MB (429MB spart, 62% reduksjon)