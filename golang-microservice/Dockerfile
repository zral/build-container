# Start from the official Golang image as the build stage
FROM golang:1.22-alpine AS builder
WORKDIR /app
COPY . .
RUN go mod init golang-microservice || true
RUN go mod tidy
RUN go build -o golang-microservice main.go

# Use a minimal image for the runtime
FROM alpine:latest
WORKDIR /app
COPY --from=builder /app/golang-microservice .
EXPOSE 8080
CMD ["./golang-microservice"]
