
# C# mikrotjeneste (ASP.NET Core)

Denne mikrotjenesten er skrevet i C# med ASP.NET Core og eksponerer:

- `/health` – Helsesjekk-endepunkt
- `/metrics` – Prometheus-kompatible målinger (via prometheus-net)

## Bygg og kjør

Bygg og kjør containeren fra denne katalogen:

```bash
docker build -t csharp-microservice .
docker run -p 8080:80 csharp-microservice
```

Eller bruk `docker-compose` fra rotkatalogen for å starte begge mikrotjenestene samtidig.
