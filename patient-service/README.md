# patient-service

Microservice Spring Boot dédié à la gestion des informations personnelles des patients.

Il permet de :
- consulter les informations d'un patient
- créer un nouveau patient
- mettre à jour un patient
- supprimer un patient

Ce service représente la base administrative du dossier patient dans l'application Medilabo.

## Objectif métier

Ce service répond aux user stories du sprint 1 :

- voir les informations personnelles des patients
- mettre à jour les informations personnelles
- ajouter un nouveau patient

Les informations gérées sont :
- prénom
- nom
- date de naissance
- genre
- adresse postale
- numéro de téléphone

L'adresse postale et le numéro de téléphone sont optionnels.

## Architecture

`patient-service` est le service de référence pour les données d'identité du patient.

Flux simplifié :
front-app -> api-gateway -> patient-service -> MySQL

## Stack technique
- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Validation
- MySQL
- Lombok
- MapStruct
- Spring Boot Actuator
- JUnit 5 / Mockito

## Fonctionnement interne
Le cœur du service repose sur la consultation, la création et la mise à jour du dossier patient.

### Consultation
La récupération des patients s'appuie sur PatientRepository, qui fournit les opérations CRUD standard via JPA.

### Création
La méthode create(...) remet l'id à null avant sauvegarde afin de garantir qu'une création reste une vraie insertion, même si un id est fourni dans la requête.

### Mise à jour complète
La méthode update(...) remplace les champs métier du patient avec les nouvelles valeurs fournies.

### Mise à jour partielle
La méthode updatePartial(...) applique uniquement les champs non nuls du DTO, ce qui évite d'écraser inutilement les données déjà présentes.

Cette logique permet de distinguer clairement :

une création
une mise à jour complète
une mise à jour partielle
DTO et mapper
Le service sépare :

l'entité Patient
le DTO PatientDto
La conversion est réalisée via PatientMapper.

Ce découplage permet :

de ne pas exposer directement l'entité JPA
de garder un contrat d'API propre
de simplifier la gestion des mises à jour partielles
Sécurité et intégration
Dans l'architecture globale, patient-service est consommé via api-gateway.

Le front n'appelle pas directement ce service.
Le gateway centralise :

le routage
la sécurité
la validation du token JWT
Supervision
Endpoints Actuator utiles :

/actuator/health
/actuator/info
/actuator/prometheus
