#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
OPENAPI_DIR="$ROOT_DIR/docs/openapi"
CLIENTS_DIR="$ROOT_DIR/libs/clients"

echo "[GEN] Génération des clients OpenAPI depuis $OPENAPI_DIR -> $CLIENTS_DIR"
# Exemple: placez ici votre commande openapi-generator-cli si utilisée.
# openapi-generator-cli generate -i $OPENAPI_DIR/patient-service.yaml -g java -o $CLIENTS_DIR/patient
echo "[GEN] (placeholder) Ajoutez vos commandes de génération ici."