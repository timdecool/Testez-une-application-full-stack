# P5 - Testez une application full-stack
**Auteur :** Timothé DECOOL
**Date : 18/05/2026**

## Présentation projet
Projet réalisé dans le cadre de la formation "Développeur Full-Stack - Java et Angular" sur OpenClassrooms. Le code applicatif a été réalisé en amont pour une application de gestion et réservation de sessions de yoga.

J'ai réalisé dans ce contexte une suite de tests pour valider la qualité de l'application.
- **front-end :** tests unitaires et d'intégration avec Jest et tests de bout en bout avec Cypress ;
- **back-end :** tests unitaires et d'intégration avec JUnit et Mockito.

## Installation
Clonez le projet depuis le dépôt distant.

> git clone https://github.com/timdecool/Testez-une-application-full-stack

Rendez vous dans le dossier du projet et installez les dépendances front-end.

> cd Testez-une-application-full-stack  
cd front  
npm install

Depuis la racine du projet, se rendre dans le répertoire back pour installer les dépendances du projet :
> cd back   
mvn clean install -DskipTests

Pour installer la base de donnée, accéder à mysql via ligne de commande ou phpMyAdmin et créer une base de données nommée "test", puis exécuter le fichier SQL présent au chemin suivant :

> back/src/test/resources.data.sql

## Lancement
Pour lancer l'application, lancer les commandes suivantes.

### Front-end
> cd front  
npm run start

### Back-end
> cd back  
mvn spring-boot:run

Les identifiants pour accéder à l'application en tant qu'administrateur sont les suivants :
- login: yoga@studio.com
- password: test!1234

## Tests
### Tests front-end E2E

Lancer les tests end-to-end via la ligne de commande, puis générer le coverage.

> npm run e2e:ci  
npm run e2e:coverage

Le rapport de couverture de code est disponible en suivant ce chemin :

> front/coverage/lcov-report/index.html

### Tests front-end unitaires et d'intégration

Générer le coverage des tests.
> npx jest --coverage

Le rapport de couverture généré est disponible en suivant ce chemin :
> front/coverage/jest/lcov-report/index.html

## Tests back-end
___
Lancer les tests et générer le coverage.
> mvn clean test

Le rapport de couverture généré est disponible en suivant ce chemin :
> back/target/site/jacoco/index.html