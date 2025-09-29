# Start from the official Golang image as the build stage
FROM golang:1.22.3-alpine AS builder
RUN apk add --no-cache binutils
WORKDIR /app

# Kopier go.mod og all kode
COPY go.mod .
COPY . .

RUN go mod tidy
RUN CGO_ENABLED=0 go build -ldflags="-s -w" -o golang-microservice main.go && strip golang-microservice

# Use a minimal image for the runtime
FROM scratch
WORKDIR /app
COPY --from=builder /app/golang-microservice .
EXPOSE 8080
ENTRYPOINT ["/app/golang-microservice"]
