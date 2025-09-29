package main

import (
	"fmt"
	"log"
	"net/http"

	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promhttp"
)

var (
	reqCounter = prometheus.NewCounter(
		prometheus.CounterOpts{
			Name: "http_requests_total",
			Help: "Total number of HTTP requests",
		},
	)
)

func healthHandler(w http.ResponseWriter, r *http.Request) {
	reqCounter.Inc()
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	w.Write([]byte(`{"status":"Healthy"}`))
}

func main() {
	prometheus.MustRegister(reqCounter)

	http.HandleFunc("/health", healthHandler)
	http.Handle("/metrics", promhttp.Handler())

	fmt.Println("Golang microservice running on :8080")
	log.Fatal(http.ListenAndServe(":8080", nil))
}
