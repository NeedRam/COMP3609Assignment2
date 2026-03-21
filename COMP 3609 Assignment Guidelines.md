COMP 3609 – Game Programming
2025/2026 Semester 2
Assignment 2
Date Due: Tuesday March 17, 2026 @ 11:30 pm
Theme
A small “visual playground” demonstrating core techniques
covered in the course rather than a full game.
Description
You do not have to develop a game for this assignment. Rather, you must implement concepts covered
in the course to create a “visual playground” where the player’s sprite moves around a background that
is several times larger than the GamePanel. As the player’s sprite moves, it encounters other game
entities which behave in a certain way when a collision takes place.
Your program must implement the following core techniques:
1. Large Background: A world/background image that is several times larger than the GamePanel. The
player’s sprite should be able to smoothly scroll through the background in four directions using the
arrow keys (or WASD). The player’s sprite should not be able to scroll beyond the edges of the
background in any direction.
2. Image Effects: At least three effects implemented via Java2D image processing:
o Disappear/Fade
o Grayscale
o Tint
3. Sprite Animation: The player’s sprite must be frame-animated (e.g., idle, walking in relevant
directions). At least one additional sprite should be animated, ideally using a sprite sheet (e.g.,
decorative entity, collectible, or environmental element).
4. Solid Objects: The playground should feature several solid objects at different locations (to be
discussed in Week 7). The player’s sprite should not be able to go through a solid object from any
direction.
5. Double Buffering and Threads: The playground should implement manual double buffering as
discussed in Week 5. The GamePanel should be the only thread in the game.
6. Sound Clips: Sound clips should be used to enhance the experience of moving through the
playground.
7. Terminating the Game: Although a game is not required, the player should be required to collect a
certain of number of a game entity as it moves around the playground. When this number has been
collected, the program should terminate with a “Game Over” screen completely in grey scale.
8. Information Display: Use the InfoPanel or create an area on the upper left corner of the GamePanel
to display the current FPS, the world coordinates of the player’s sprite, the effects currently taking
place, and the number of game entities already collected / still to be collected by the player.