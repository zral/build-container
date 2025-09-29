#!/bin/bash
# Script for å teste oppstartstid, minnebruk og svartid for alle mikrotjenester



SERVICES=(
  "csharp-microservice:8080/health"
  "java-microservice:8081/health"
  "plain-java-microservice:8082/health"
  "golang-microservice:8083/health"
)

# Funksjon for å måle oppstartstid
measure_startup() {
  local service_name=$1
  local port=$2
  local container_id
  local start_time end_time duration

  # Finn riktig date-kommando for millisekunder
  if command -v gdate >/dev/null 2>&1; then
    _date() { gdate +%s%3N; }
    _unit="ms"
  else
    _date() { date +%s; }
    _unit="s (kun sekunder, installer coreutils for ms)"
  fi

  # Start container i bakgrunnen
  docker compose up -d $service_name
  start_time=$(_date)

  # Vent til tjenesten svarer med HTTP 200 på /health
  max_wait=60
  waited=0
  while true; do
    response=$(curl -s -w '\n%{http_code}' http://localhost:$port/health)
    body=$(echo "$response" | head -n1)
    code=$(echo "$response" | tail -n1)
    if [ "$code" = "200" ] && echo "$body" | grep -q 'Healthy'; then
      break
    fi
    sleep 0.5
    waited=$((waited+1))
    if [ $waited -ge $((max_wait*2)) ]; then
      echo "Feil: $service_name svarte ikke med HTTP 200 og 'Healthy' i body innen $max_wait sekunder. Avbryter test."
      exit 1
    fi
  done
  end_time=$(_date)
  duration=$(echo "$end_time - $start_time" | bc)
  echo "$service_name oppstartstid: ${duration} $_unit"
}

# Funksjon for å måle minnebruk
measure_memory() {
  local service_name=$1
  local container_id=$(docker ps -qf "name=${service_name}")
  if [ -z "$container_id" ]; then
    echo "Fant ikke container for $service_name"
    return
  fi
  mem=$(docker stats --no-stream --format "{{.MemUsage}}" $container_id)
  echo "$service_name minnebruk: $mem"
}

# Funksjon for å kjøre 1000 requests og måle tid
run_load_test() {
  local url=$1
  local n=1000
  echo "Kjører $n requests mot $url ..."
  # Kjør og fang opp real-tid fra time
  local t=$( (time (for i in $(seq 1 $n); do curl -s $url > /dev/null; done)) 2>&1 | grep real | awk '{print $2}')
  # Konverter til sekunder med desimaler
  local sec=$(echo "$t" | awk -F'm' '{split($2,s,"s"); print ($1*60)+s[1]}')
  local avg=$(echo "scale=4; $sec/$n" | bc)
  echo "Total tid: $sec s, Gjennomsnitt per kall: $avg s"
}

# Test alle tjenester
services=(csharp-microservice java-microservice plain-java-microservice golang-microservice)
ports=(8080 8081 8082 8083)

for i in ${!services[@]}; do
  svc=${services[$i]}
  port=${ports[$i]}
  echo "\n=== Tester $svc ==="
  docker compose down &> /dev/null
  measure_startup $svc $port
  run_load_test http://localhost:$port/health
  measure_memory $svc
  docker compose stop $svc &> /dev/null
  docker compose down &> /dev/null
  sleep 2
  echo "----------------------"
done
