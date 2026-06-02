# MiniGames App

Application Android de mini-jeux développée avec Kotlin et Jetpack Compose, dans le cadre du TP
progressif sur 3 séances (CNAM – 1ère année).

## Binôme

- Matthieu Peterschmitt
- Charles Lesecq

## Branche à tester

`main`

C'est la branche à cloner et à évaluer.

## Comment lancer

Avec un appareil ou un émulateur connecté :

```bash
./gradlew installDebug
```

ou ouvrir le projet dans Android Studio et lancer la configuration `app`.

Prérequis : Android SDK (compileSdk 36), JDK 11+. `minSdk` = 27.

## Ce qui fonctionne

Toutes les fonctionnalités demandées des trois séances sont implémentées et opérationnelles,
ainsi que l'ensemble des bonus listés au barème.

### Séance 1 — Jeu de réaction
- Écran d'accueil et navigation vers le jeu.
- Timer interactif géré par coroutine (`LaunchedEffect` / `viewModelScope`).
- Partie générée aléatoirement : valeur de départ, vitesse et sens. Le timer part toujours du
  côté opposé à la cible pour rester atteignable.
- Phases Prêt → Jeu → Résultat, avec écart à la cible et message de feedback.
- **Bonus** : *Timer aveugle* (le timer se masque à l'approche de la cible) et *Vitesse variable*
  (la vitesse change aléatoirement en cours de partie), activables par interrupteurs avant la partie.

### Séance 2 — Architecture & navigation
- Architecture MVVM : toute la logique métier vit dans les `ViewModel` (`StateFlow` + `collectAsState`).
- Navigation multi-écrans type-safe avec `NavHost` / `NavController` et routes `@Serializable`.
- Jeu *Mot caché* : grille 3×3, sélection/effacement des lettres, validation, passage de grille,
  timer de 60 s, phase de fin avec score et rejouer.
- **Bonus** : *Indice* — révèle la première lettre du mot (une fois par grille, pénalité d'un point).

### Séance 3 — Persistance & leaderboard
- Persistance locale avec Room : entité `Score`, `ScoreDao`, `AppDatabase` (singleton,
  base `minigames_database`) et `ScoreRepository`.
- Saisie du pseudo sur l'accueil ; les boutons de jeu sont désactivés tant que le pseudo est vide.
- Le pseudo est transmis aux écrans via les routes de navigation.
- Sauvegarde automatique du score en fin de partie (réaction et mot caché), via `viewModelScope`.
- Écran *Leaderboard* affichant les 10 meilleurs scores (rang, pseudo, jeu, score) dans une `LazyColumn`.
- **Bonus** : filtre du leaderboard par jeu (Tous / Réaction / Mot caché), statistiques personnelles
  du joueur (nombre de parties et score moyen, via `COUNT`/`AVG`), et réinitialisation de tous les scores.

> Note sur le score du jeu de réaction : l'écart (plus petit = meilleur) est converti en points
> (`10000 − écart`, borné à 0) afin que tous les jeux soient comparables sur un classement trié par
> score décroissant.

## Ce qui ne fonctionne pas

- Aucune fonctionnalité connue n'est cassée : tout ce qui précède a été testé sur émulateur.
- Limitation connue : conformément à l'approche suggérée par le sujet
  (`LaunchedEffect(Unit) { startGame() }`), le jeu *Mot caché* relance une nouvelle partie lorsque
  l'écran ré-entre en composition (par exemple lors d'une rotation de l'écran). Le score en base
  n'est pas affecté, mais la partie en cours redémarre.
