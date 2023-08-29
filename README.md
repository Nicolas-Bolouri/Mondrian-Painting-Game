# Mondrian-Painting-Game

## Introduction

This repository contains the implementation of a visual game inspired by Mondrian paintings. Players interact with a recursive quad-tree structure to achieve specific goals. Derived from an assignment created by Diane Horton and David Liu from the University of Toronto.

## Game Overview

### Game Board

* The game board, termed a "block", can be:
  * A square of a single color.
  * A square subdivided into 4 equal-sized blocks.

* The largest block, which contains the entire structure, is termed the top-level block.
  * The top-level block is at level 0 (the root of the quad-tree).
  * If it is subdivided, its sub-blocks are at level 1, and so forth.

* Boards have a maximum allowed depth indicating how many levels it can go down. 

### Moves

Players can perform the following moves:

1. **Rotating** a block clockwise or counterclockwise.
2. **Reflecting** the block horizontally or vertically.
3. **Smashing** a block to give it four brand-new, randomly generated sub-blocks.

_Note:_ Smashing the top-level block or a unit cell (at the maximum depth) is not allowed.

### Goals and Scoring

Players are assigned a randomly-generated goal:

* **Blob Goal:** Aim for the largest "blob" of a given color. A blob is a group of orthogonally connected blocks of the same color.
* **Perimeter Goal:** Put the most units of a given color on the outer perimeter of the board. Corner cells count twice towards the score.

Each goal pertains to a specific target color.

## How to Play

1. Start the game to get a randomly generated board and goal (navigate to `src/game/BlockGame.java` and build + run the file).
2. Use the described moves to achieve your goal within a set number of turns.
3. Score is calculated based on the chosen goal and the current state of the board.

## Credits

Adapted from an assignment by Diane Horton and David Liu from the University of Toronto.
