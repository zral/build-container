# syntax=docker/dockerfile:1

# Build stage
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /app
COPY src/*.csproj ./src/
RUN dotnet restore ./src/build-container.csproj
COPY src/. ./src/
WORKDIR /app/src
RUN dotnet publish -c Release -o /out

# Runtime stage
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS runtime
WORKDIR /app
COPY --from=build /out ./
EXPOSE 80
ENTRYPOINT ["dotnet", "build-container.dll"]
