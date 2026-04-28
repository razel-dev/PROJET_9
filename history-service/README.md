# history-service

Microservice Spring Boot dédié à la gestion de l'historique médical des patients.

Son rôle est de :
- stocker les notes d'observation liées à un patient
- restituer l'historique d'un patient dans l'ordre chronologique décroissant
- permettre l'ajout de nouvelles notes médicales

## Objectif métier

Ce service répond aux user stories du sprint 2 :

- consulter l'historique des informations d'un patient
- ajouter une note à l'historique du patient

L'objectif est de donner au praticien une mémoire clinique du dossier patient, afin de suivre l'évolution d'un patient d'une séance à l'autre.

## Architecture

`history-service` est un microservice spécialisé dans les notes médicales.

## Flux simplifié :

front-app -> api-gateway -> history-service -> MongoDB

## Stack technique
- Java 17
- Spring Boot 3
- Spring Web
- Spring Data MongoDB
- Spring Validation
- Spring Boot Actuator
- Lombok
- JUnit 5 / Mockito

## Fonctionnement interne

Le cœur du service repose sur deux opérations métier : consulter l'historique d'un patient et ajouter une nouvelle note.

### Consultation de l'historique

La méthode :
findByPatientIdDesc(Long patientId)

Récupère les notes d'un patient à partir du repository MongoDB, en les triant du plus récent au plus ancien grâce à :
findByPatientIdOrderByCreatedAtDesc(Long patientId)

Ce choix permet d'afficher directement un historique lisible pour le praticien, sans tri supplémentaire dans la couche service.

Ajout d'une note
La méthode :
create(Note toCreate)

Prépare la note avant sauvegarde :

- Elle rejette une note null,
- elle remet l'id à null pour garantir une création,
- elle trim le contenu,
- elle trim l'auteur,
- elle met l'auteur à null s'il est vide,
- elle renseigne createdAt si aucune date n'est fournie

Cette logique évite d'enregistrer des données incohérentes et garantit qu'une nouvelle note est bien ajoutée à l'historique du patient, plutôt que de modifier une note existante.
