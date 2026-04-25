Service Spring Boot dédié à l'évaluation du risque de diabète d'un patient.

Ce microservice ne stocke pas de données. Son rôle est de :
- récupérer les informations d'un patient depuis `patient-service`
- récupérer ses notes médicales depuis `history-service`
- analyser les termes déclencheurs présents dans les notes
- calculer un niveau de risque
- renvoyer un rapport structuré au front ou au gateway

## Objectif métier

Le service répond au besoin suivant :

> En tant que praticien, je veux pouvoir consulter le risque de diabète pour un patient afin de le prévenir si sa santé est potentiellement en danger.

Le rapport retourne un des 4 niveaux suivants :
- `NONE`
- `BORDERLINE`
- `IN_DANGER`
- `EARLY_ONSET`

## Architecture

`assessment-service` est un service d'orchestration métier.

Il dépend de :
- `patient-service` pour récupérer les données du patient
- `history-service` pour récupérer les notes médicales

Flux simplifié :

front-app -> api-gateway -> assessment-service
                               |-> patient-service
                               |-> history-service

## Stack technique
- Java 17
- Spring Boot 3.2.5
- Spring Web
- Spring Cloud OpenFeign
- Spring Boot Actuator
- Spring Security
- Lombok
- JUnit / Mockito

## Fonctionnement interne
Le calcul principal est réalisé dans AssessmentService.

Étapes :

- récupération du patient via PatientClient
- calcul de l'âge à partir de la date de naissance
- récupération des notes via HistoryClient
- comptage des termes déclencheurs dans toutes les notes
- application des règles métier
- construction d'un AssessmentDto