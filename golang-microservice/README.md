---
# Golang mikrotjeneste

En enkel mikrotjeneste skrevet i Go som eksponerer:

- **`/health`** – Helsesjekk-endepunkt (returnerer 200 OK hvis tjenesten lever)
- **`/metrics`** – Prometheus-kompatible målinger (via prometheus/client_golang)

## Bygg og kjør

Bygg og kjør containeren fra denne katalogen:

```bash
docker build -t golang-microservice .
docker run -p 8080:8080 golang-microservice
```

Tjenesten vil da være tilgjengelig på:
- [http://localhost:8080/health](http://localhost:8080/health)
- [http://localhost:8080/metrics](http://localhost:8080/metrics)

## Multi-stage build

Denne tjenesten bruker en multi-stage Dockerfile for å bygge og pakke applikasjonen på en effektiv og sikker måte:

1. **Byggesteg:**
   - Starter fra et image med Go SDK (`golang:1.22-alpine`).
   - Bygger binærfilen for tjenesten.
2. **Runtime-steg:**
   - Starter fra et rent og lite Alpine-image.
   - Kopierer kun den kompilerte binærfilen fra byggesteg.
   - Ingen byggverktøy, kildekode eller unødvendige filer blir med i sluttbildet.

**Eksempel fra Dockerfile:**
```dockerfile
FROM golang:1.22-alpine AS builder
WORKDIR /app
COPY . .
RUN go mod tidy
RUN go build -o golang-microservice main.go

FROM alpine:latest
WORKDIR /app
COPY --from=builder /app/golang-microservice .
EXPOSE 8080
CMD ["./golang-microservice"]
```

## Prometheus-integrasjon

Tjenesten eksponerer Prometheus-metrics på `/metrics`. Legg til følgende i din `prometheus.yml` for å scrape metrics:

```yaml
scrape_configs:
  - job_name: 'golang-microservice'
    static_configs:
      - targets: ['golang-microservice:8080']
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
```# Golang Microservice

A simple Go-based microservice with a `/health` endpoint, designed for Docker multistage builds.

## Build and Run

```sh
# Build the Docker image
cd golang-microservice
docker build -t golang-microservice .

# Run the container
# (use -p 8080:8080 to expose the port)
docker run -p 8080:8080 golang-microservice
```

## Health Check

Visit [http://localhost:8080/health](http://localhost:8080/health) to check the service status.
