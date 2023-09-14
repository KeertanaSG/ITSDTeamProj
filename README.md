# ITSDTeamProject-0

## Tactical Card Game Project

## Introduction

The goal of this software development project is to create an online tactical card game that combines card-game mechanics with a 9 x 5 chess-like board. This game serves as a core back-end service that manages and progresses the game based on player actions within a web browser. The final product allows human players to engage in challenging matches against semi-intelligent AI opponents.

## Project Overview

The project is a complex endeavor, involving the integration of various game mechanics and features. These include:

- **Collecting Cards**: Players can collect cards with unique abilities and attributes.
  
- **Building Decks**: The ability to create custom decks from the collected cards, allowing for strategic gameplay.
  
- **Moving Units**: Units can be strategically moved on a 9 x 5 board, similar to chess.
  
- **Casting Spells**: Players can cast spells with various effects.
  
- **Fighting**: Engaging in tactical battles with opponents.

Achieving a balance between these mechanics is essential to create an enjoyable and challenging gaming experience.

## Development Methodologies

To accomplish our goals, we employ modern software development methodologies:

- **Agile/Scrum**: We follow Agile principles, using Scrum practices to facilitate collaboration and flexibility throughout the project.

- **Test-Driven Development (TDD)**: We emphasize writing tests before implementing features, ensuring robust code and reducing bugs.

- **Continuous Integration/Continuous Delivery (CI/CD)**: Our CI/CD pipeline allows for automated testing and seamless deployment, maintaining high-quality code throughout development.


## Getting Started

Follow these steps to build and run the game on your local machine.

1. **Clone the Repository**

   ```shell
   git clone https://github.com/KeertanaSG/ITSDTeamProj.git
   ```
   
2. **Navigate to the Game Directory**

  ```shell
  cd ITSDTeamProj
  ```
  Use sbt to compile the game's source code:
  ```shell
  sbt compile
  ```

3. **Run the Game**
  Once the code is compiled successfully, you can run the game using sbt:

  ```shell
  sbt run
  ```
This command will start the game.

4. **Enjoy Playing**

Open your web browser and navigate to http://localhost:8000 to access the game. You can now enjoy playing!
