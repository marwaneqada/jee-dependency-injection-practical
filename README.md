# Dependency Injection Practical

## Activité Pratique N°1 - Injection des dépendances

This project is for practicing dependency injection in Java.

## Current progress

- Maven Java 17 project structure created
- `IDao` interface created
- `DaoImpl` implementation created

## Partie 1 - Injection des dépendances

### Injection par instanciation statique

Dans cette première version, les objets sont créés directement dans la classe de présentation.

Classes créées :
- `IDao`
- `DaoImpl`
- `IMetier`
- `MetierImpl`
- `PresentationStatique`

Cette version montre le principe du couplage faible entre la couche métier et la couche DAO, car `MetierImpl` dépend de l’interface `IDao` et non d’une implémentation concrète.