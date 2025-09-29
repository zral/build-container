# Start from the official Golang image as the build stage
FROM golang:1.22.3-alpine AS builder
WORKDIR /app

# Kopier go.mod og go.sum først for bedre cache
COPY go.mod .
COPY go.sum .
RUN go mod download

# Kopier resten av koden
COPY . .

# Bygg statisk binær (unngå C-avhengigheter)
RUN CGO_ENABLED=0 go build -o golang-microservice main.go

# Use a minimal image for the runtime
FROM alpine:latest
WORKDIR /app
COPY --from=builder /app/golang-microservice .
EXPOSE 8080
CMD ["./golang-microservice"]
