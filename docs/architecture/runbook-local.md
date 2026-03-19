# Runbook local

- Prérequis: Docker, Java 17, Maven Wrapper.
- Démarrer la stack: `make up` (profils d’observabilité avec `--profile` Docker si nécessaire).
- Arrêter et nettoyer: `make down`.
- Lancer un service en dev: `make run svc=assessment-service`.
- Smoke tests: `make smoke`.