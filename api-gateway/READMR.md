# api-gateway

Microservice Spring Boot jouant le rôle de point d'entrée unique pour les APIs backend de Medilabo.

Il permet de :
- centraliser les appels vers les microservices internes
- router les requêtes vers le bon service
- appliquer la sécurité sur les APIs
- simplifier l'accès au backend pour le front

## Objectif technique

`api-gateway` sert de façade entre le `front-app` et les services métier.

Au lieu de faire communiquer le front directement avec :
- `patient-service`
- `history-service`
- `assessment-service`

le projet passe par un point d'accès unique :

front-app -> api-gateway -> services internes

## Sécurité
Le gateway protège les APIs du projet.

Il est configuré comme OAuth2 Resource Server avec validation JWT.

Cela signifie que :

- le front appelle les APIs avec un token utilisateur
- le gateway vérifie la validité du token
- si le token est valide, la requête est routée
- sinon, elle est rejeté

## Fonctionnement interne
Le gateway ne contient pas de logique métier comme les autres services.

Son fonctionnement repose sur deux responsabilités principales :

1. Router les requêtes.
   Il analyse le chemin HTTP et choisit le service cible selon les règles déclarées dans la configuration.

2. Sécuriser l'accès
   Il valide le token JWT via Keycloak avant d'autoriser le routage vers les services internes.

Autrement dit, le gateway agit comme une façade sécurisée du backend.

Intégration avec le front-app ne contacte pas directement les microservices métier.

Il appelle le gateway via des clients Feign dédiés, par exemple :

- PatientGatewayClient
- HistoryGatewayClient
- AssessmentGatewayClient

Cela permet au front de :

- s'appuyer sur un point d'accès unique
- éviter de connaître la topologie interne des services
- garder une intégration plus simple
