
## Overview

## Vue d'ensemble

Le `ReservationController` gère les opérations liées aux réservations dans l'application Meeting Planner. Il fournit des points de terminaison pour lister toutes les réservations et enregistrer une nouvelle réservation.

## Points de terminaison

### 1. Lister toutes les réservations

- **URL** : `/api/v1/reservations`
- **Méthode** : `GET`
- **Description** : Récupère une liste de toutes les réservations.
- **Réponse en cas de succès** :
  - **Code** : `200 OK`
  - **Contenu** : Une liste d'objets `ReservationDto`.
- **Réponse en cas d'erreur** :
  - **Code** : `400 BAD REQUEST`
  - **Contenu** : Chaîne de message d'erreur.

### 2. Créer une réservation
- **URL** : `/api/v1/reservations`
- **Méthode** : `POST`
- **Description** : Crée une nouvelle réservation en fonction des détails fournis.
- **Corps de la requête** : Objet `StoreReservationRequest` contenant :
  - `attendees` : Nombre de participants.
  - `type` : Type de réunion (`VC`, `SPEC`, `RC`, `RS`).
  - `date` : Date de la réservation.
  - `startTime` : Heure de début de la réservation.
  - `reservedBy` : Identifiant de la personne qui réserve.

- **Réponse en cas de succès** :
  - **Code** : `201 CREATED`
  - **Contenu** : Objet `ReservationDto` représentant la réservation enregistrée.

- **Réponses en cas d'erreur** :
  - **Code** : `400 BAD REQUEST`
  - **Contenu** : Chaîne de message d'erreur pour divers échecs de validation (requête invalide, type de réunion invalide, vérifications de date et d'heure, tentative de réservation le week-end, heure de début non pleine, hors de la plage horaire autorisée).
  - **Code** : `404 NOT FOUND`
  - **Contenu** : "Aucune salle disponible pour cette réservation" si aucune salle appropriée n'est trouvée.

  
## Règles de validation
- Le type de réunion doit être l'un des suivants : `VC`, `SPEC`, `RC`, `RS`.
- La date et l'heure de début de la réservation doivent être futures.
- Les réservations ne peuvent pas être faites le week-end.
- L'heure de début doit être à l'heure pleine et entre 8h00 et 20h00.

## Lien utile
- **H2 Database** :
  - **URL** : `/h2-console`.
  - **JDBC URL** : `jdbc:h2:mem:meetingplanner`.