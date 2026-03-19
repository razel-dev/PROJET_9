# ADR 0001 — Architecture mono-repo prête à l’extraction

Contexte: démarrer simple, optimiser la DX et permettre l’extraction future en microservices.
Décision: mono-repo avec modules par service, contrats API-first, observabilité unifiée.
Conséquences: CI modulaire, images par service, tests de contrat requis pour évoluer sans casse.