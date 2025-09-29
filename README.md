# Bygg-container og C# mikrotjeneste med Prometheus-målinger

## Oversikt
Dette prosjektet demonstrerer hvordan man bygger og kjører en enkel C# mikrotjeneste i en containerisert miljø ved hjelp av Docker og docker-compose. Løsningen bruker en dedikert build-container for å bygge applikasjonen, og deployer kun nødvendige runtime-filer i produksjonscontaineren. Mikrotjenesten eksponerer en `/health`-endpoint for helsesjekk og en Prometheus-kompatibel `/metrics`-endpoint for overvåkning.

## Struktur

- `src/` – Kildekode for mikrotjenesten (C#/.NET 8)
- `Dockerfile` – Multi-stage Dockerfile som skiller bygg og runtime
- `docker-compose.yml` – Orkestrering av mikrotjenesten
- `prometheus.yml` – Eksempel på Prometheus-konfigurasjon

## Hvordan fungerer løsningen?

### 1. Mikrotjenesten
Applikasjonen er skrevet i C# med ASP.NET Core. Den har to viktige endepunkter:

- `/health` – Returnerer status for tjenesten (brukes til helsesjekk)
- `/metrics` – Eksponerer Prometheus-målinger via `prometheus-net`-biblioteket

### 2. Dockerfile (multi-stage build)
Dockerfile bruker to steg:

1. **Byggesteg**: Bruker `mcr.microsoft.com/dotnet/sdk:8.0` for å bygge og publisere applikasjonen.
2. **Runtime-steg**: Bruker `mcr.microsoft.com/dotnet/aspnet:8.0` og kopierer kun de publiserte filene fra byggesteg. Ingen byggverktøy eller SDK er inkludert i runtime-containeren, kun nødvendige runtime-filer.

Dette gir små, sikre og effektive containere.

### 3. docker-compose
`docker-compose.yml` starter mikrotjenesten og eksponerer port 8080 på vertsmaskinen. Miljøvariabler kan settes for å konfigurere tjenesten.

### 4. Prometheus-integrasjon
`prometheus.yml` viser hvordan Prometheus kan konfigureres til å scrape `/metrics`-endpointen til mikrotjenesten. Dette muliggjør overvåkning og innsikt i tjenestens ytelse og helse.

## Kom i gang

1. Bygg og start tjenesten:
	```bash
	docker-compose up --build
	```
2. Åpne i nettleser:
	- Helsesjekk: [http://localhost:8080/health](http://localhost:8080/health)
	- Prometheus-målinger: [http://localhost:8080/metrics](http://localhost:8080/metrics)

3. (Valgfritt) Start Prometheus med `prometheus.yml` for å samle inn målinger.

## Læringspunkter
- Multi-stage builds gir små og sikre containere
- Separasjon av bygg og runtime
- Eksponering av helse- og måle-endepunkter
- Enkel integrasjon med Prometheus for overvåkning