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

### Injection par instanciation dynamique

Dans cette version, les noms des classes sont placés dans le fichier `config.txt`.

Le programme lit ce fichier, charge les classes avec `Class.forName`, crée les objets avec `newInstance`, puis injecte la dépendance avec la méthode `setDao`.

Fichiers ajoutés :
- `config.txt`
- `PresentationDynamique`

### Injection avec Spring - Version XML

Dans cette version, Spring crée les objets et injecte les dépendances à partir du fichier `spring-config.xml`.

Fichiers ajoutés :
- `spring-config.xml`
- `PresentationSpringXML`

La dépendance entre `MetierImpl` et `DaoImpl` est configurée dans le fichier XML avec la balise `<property>`.

### Injection avec Spring - Version annotations

Dans cette version, Spring détecte automatiquement les classes annotées avec `@Component`.

L’injection de dépendance est réalisée avec l’annotation `@Autowired`.

Classes modifiées :
- `DaoImpl`
- `MetierImpl`

Classe ajoutée :
- `PresentationSpringAnnotations`

## Partie 2 - Mini Framework IoC

L’objectif de cette partie est de développer un mini framework d’injection des dépendances similaire à Spring IoC.

### Structure initiale du framework

Packages créés :
- `framework.annotations`
- `framework.context`

Annotations créées :
- `@Component`
- `@Autowired`

Interface créée :
- `ApplicationContext`

### AnnotationApplicationContext

`AnnotationApplicationContext` est la première implémentation du mini conteneur IoC.

Il reçoit une liste de classes, crée les objets annotés avec `@Component`, les stocke comme beans, puis injecte les dépendances dans les champs annotés avec `@Autowired`.

Le conteneur supporte maintenant :
- l'injection par attribut
- l'injection par constructeur
- l'injection par setter ou méthode

La récupération des objets se fait avec :
- `getBean(String name)`
- `getBean(Class<T> type)`

### Test du mini framework avec annotations

Un package `demo` a été ajouté pour tester le mini conteneur IoC.

Classes ajoutées :
- `DaoDemo`
- `MetierDemo`
- `PresentationMiniFrameworkAnnotations`

Dans ce test, le conteneur crée les objets annotés avec `@Component` et injecte automatiquement la dépendance `IDao` dans `MetierDemo` grâce à `@Autowired`.
