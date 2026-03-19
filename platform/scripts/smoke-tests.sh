#!/usr/bin/env bash
set -euo pipefail

echo "[SMOKE] Vérification /actuator/health des services exposés..."
curl -fsS "http://localhost:8088/actuator/health" && echo " - api-gateway OK" || (echo " - api-gateway FAIL" && exit 1)
curl -fsS "http://localhost:8080/actuator/health" && echo " - assessment-service OK" || (echo " - assessment-service FAIL" && exit 1)

echo "[SMOKE] OK"