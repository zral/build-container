

# C# mikrotjeneste (ASP.NET Core)

Denne mikrotjenesten er skrevet i C# med ASP.NET Core og eksponerer følgende endepunkter:

- **`/health`** – Helsesjekk-endepunkt (returnerer 200 OK hvis tjenesten lever)
- **`/metrics`** – Prometheus-kompatible målinger (via prometheus-net)

## Bygg og kjør

Bygg og kjør containeren fra denne katalogen:

```bash
docker build -t csharp-microservice .
docker run -p 8080:80 csharp-microservice
```

Tjenesten vil da være tilgjengelig på:
- [http://localhost:8080/health](http://localhost:8080/health)
- [http://localhost:8080/metrics](http://localhost:8080/metrics)

## Multi-stage build

Denne tjenesten bruker en multi-stage Dockerfile for å bygge og pakke applikasjonen på en effektiv og sikker måte:

1. **Byggesteg:**
	- Starter fra et image med .NET SDK (`mcr.microsoft.com/dotnet/sdk:8.0`).
	- Bygger prosjektet og publiserer ut en "self-contained" mappe med kun nødvendige filer.
2. **Runtime-steg:**
	- Starter fra et rent og lite .NET runtime-image (`mcr.microsoft.com/dotnet/aspnet:8.0`).
	- Kopierer kun de publiserte filene fra byggesteg.
	- Ingen byggverktøy, kildekode eller unødvendige filer blir med i sluttbildet.

**Fordeler:**
- Sluttbildet blir mye mindre og sikrere.
- Kun det som trengs for å kjøre applikasjonen er med.
- Raskere deploy og mindre angrepsflate.

**Eksempel fra Dockerfile:**
```dockerfile
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /app
COPY src/*.csproj ./src/
RUN dotnet restore ./src/build-container.csproj
COPY src/. ./src/
WORKDIR /app/src
RUN dotnet publish -c Release -o /out

FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS runtime
WORKDIR /app
COPY --from=build /out ./
EXPOSE 80
ENTRYPOINT ["dotnet", "build-container.dll"]
```

## Prometheus-integrasjon

Tjenesten eksponerer Prometheus-metrics på `/metrics`. Legg til følgende i din `prometheus.yml` for å scrape metrics:

```yaml
scrape_configs:
  - job_name: 'csharp-microservice'
	 static_configs:
		- targets: ['csharp-microservice:80']
```

## Eksempel på bruk

Helsesjekk:
```bash
curl http://localhost:8080/health
# Output: OK
```

Prometheus-metrics:
```bash
curl http://localhost:8080/metrics
# Output: Prometheus metrics-format
```
