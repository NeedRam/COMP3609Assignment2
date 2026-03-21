# COMP 3609 Assignment 2 Framework Summary

Based on examination of the Space Invaders example and Week4-Week9 examples, here are the key patterns and techniques to use for the assignment framework:

## 1. GamePanel Implementation

The GamePanel extends JPanel and serves as the main drawing surface for game entities. Key patterns:

- **Double Buffering**: Enable with `setDoubleBuffered(true)` in constructor
- **Entity Management**: Maintain lists of game objects (players, enemies, bullets)
- **Managers**: Reference to SoundManager and ScoreManager (Singleton pattern)
- **Game State**: Track running, paused, and game over states
- **Input Handling**: Track key states for smooth movement
- **Rendering**: Override `paintComponent(Graphics g)` for custom drawing
- **Entity Updates**: Methods like `updateGameEntities()`, `updatePlayerMovement()`
- **Game Loop**: Separate thread for game updates and rendering (implements Runnable)

## 2. GameWindow Implementation

The GameWindow extends JFrame and manages the UI and game loop timing:

- **UI Components**: Labels, text fields, buttons for score/lives/level display
- **Layout Managers**: FlowLayout for main panel, GridLayout for info/button panels
- **Event Handling**: Implements ActionListener, KeyListener, MouseListener
- **Game Timers**: 
  - GameTimer (50ms): Collision checking and score updates
  - AlienFireTimer (500ms): Alien firing control
  - MovementTimer (30ms): Smooth player movement based on key state
- **Focus Management**: Request focus on main panel for keyboard input
- **Window Properties**: Resizable, exit on close, visible

## 3. ImageManager and SoundManager Patterns

### ImageManager:
- **Static Methods**: Utility class with static methods for image loading
- **Image Loading**: `loadImage(String fileName)` using ImageIcon
- **Image Conversion**: `toBufferedImage(Image image)` for pixel manipulation
- **Image Transformations**: `flipImageVertically(Image image)` for sprite orientation
- **Advanced Features** (Week6+): 
  - `loadBufferedImage(String filename)` using ImageIO
  - `copyImage(BufferedImage src)` for creating image copies
  - Support for BufferedImage operations

### SoundManager:
- **Singleton Pattern**: Single instance via `getInstance()`
- **Resource Management**: HashMap<String, Clip> for storing sound clips
- **Special Arrays**: playerHitClips[] for random sound variation
- **Core Methods**:
  - `loadClip(String fileName)`: Loads audio files
  - `playClip(String title, boolean looping)`: Plays sounds with looping option
  - `playRandomPlayerHit()`: Randomly selects from player hit variations
  - `stopClip(String title)`: Stops a specific sound
  - `getClip(String title)`: Retrieves a clip by name

## 4. Double Buffering Implementation

Two main approaches observed:

### Approach 1 (Built-in):
- Call `setDoubleBuffered(true)` in GamePanel constructor
- Override `paintComponent(Graphics g)` for custom drawing
- Use `repaint()` to trigger redraw (automatically uses double buffer)

### Approach 2 (Manual):
- Create BufferedImage for off-screen drawing
- Draw all game entities to the BufferedImage in `gameRender()`
- Draw the BufferedImage to the screen using `getGraphics()`
- Dispose of graphics contexts properly
- Used in Week4-Week9 examples for more control

## 5. Sprite Animation Techniques

### Frame-Based Animation:
- Animation classes implementing ImageFX interface
- Each frame updates sprite properties (position, appearance)
- Draw method renders current frame state

### Sprite Sheets:
- Multiple images for different animation states (left/right/up/down)
- Direction-based sprite selection (Bat.java examples)
- Sleep/jump states with specific images

### Procedural Animation:
- Physics-based movement (jumping/falling with velocity and gravity)
- Time-based position calculations
- Collision detection with solid objects and floors

## 6. Image Effects Implementation

### ImageFX Interface:
```java
public interface ImageFX {
    public void update();
    public void draw(Graphics2D g2d);
}
```

### Effect Examples:
- **BrightnessFX**: Modifies RGB values of pixels over time
- **GrayScaleFX**: Converts images to grayscale based on time intervals
- **DisappearFX**: Gradually makes images transparent
- **RotateFX**: Rotates images continuously
- **TintFX**: Applies color tints to images
- **SepiaFX**: Applies sepia tone effect
- **ContrastFX**: Adjusts image contrast
- **FlipFX**: Flips images horizontally/vertically

### Implementation Pattern:
- Load source image and create working copy
- In `update()`: Modify effect parameters (brightness, time, etc.)
- In `draw()`: Apply effect to copy and render to screen
- Use pixel manipulation for per-pixel effects
- Properly dispose of graphics resources

## 7. Background Scrolling

### Techniques Observed:
- **Static Background**: Single image drawn to fill screen (Space Invaders)
- **Multiple Backgrounds**: Alternating between images for variety
- **Scrolling Effect**: Not explicitly shown in examples, but could be implemented by:
  - Drawing background at offset position
  - Updating offset over time for scrolling effect
  - Using multiple background images for parallax

### Implementation Approach:
- Load background image(s)
- Track scroll offset variables
- Update offset in game update loop
- Draw background with offset in render method
- Reset offset when it exceeds image width for continuous scrolling

## 8. Collision Detection

### Rectangle-Based Collision:
- Use `java.awt.geom.Rectangle2D.Double` for bounding boxes
- `getBoundingRectangle()` method in game entities
- `intersects()` method to check for overlap
- Pixel-perfect not required; rectangle approximation sufficient

### Collision Systems:
- **Bullet-Alien**: Check player bullets against alien bounding boxes
- **Bullet-Player**: Check alien bullets against player bounding box
- **Entity-World**: Check if entities reach screen boundaries (bottom, sides)
- **Entity-Entity**: Check player against solid objects, floors
- **Manager-Based**: SolidObjectManager for environment collision detection

### Implementation Pattern:
```java
// In collision checking method
for (Bullet bullet : playerBullets) {
    if (!bullet.isActive()) continue;
    
    Rectangle2D.Double bulletRect = bullet.getBoundingRectangle();
    
    for (Alien alien : aliens) {
        if (bulletRect.intersects(alien.getBoundingRectangle())) {
            // Handle collision
            bullet.setActive(false);
            alien.takeDamage();
            // ... score, sound effects, etc.
        }
    }
}
```

## 9. Sound Management

### Sound Loading and Playback:
- Load all sounds during initialization (Singleton constructor)
- Store clips in HashMap with string keys
- Support for looping (background music) and one-shot effects
- Volume control capabilities (setVolume method in some examples)

### Special Sound Handling:
- **Random Variations**: Multiple clips for same event (player hit sounds)
- **Background Music**: Looping playback with start/stop/pause
- **Game State Sounds**: Different sounds for game over, shooting, explosions
- **Resource Cleanup**: Properly stopping clips when needed

### SoundManager Methods:
- `playClip(String title, boolean looping)`: Main playback method
- `playRandomPlayerHit()`: For randomized hit sounds
- `stopClip(String title)`: Stop specific sound
- `getClip(String title)`: Retrieve clip by name
- `loadClip(String fileName)`: Internal loading method

## 10. Game Loop and Threading

### Thread-Based Approach (Implements Runnable):
- GamePanel implements Runnable
- `run()` method contains main game loop:
  ```java
  public void run() {
      try {
          isRunning = true;
          while (isRunning) {
              if (!isPaused)
                  gameUpdate();
              gameRender();
              Thread.sleep(50); // Controls frame rate (~20 FPS)
          }
      } catch(InterruptedException e) {}
  }
  ```
- **gameUpdate()**: Updates game state (positions, collisions, etc.)
- **gameRender()**: Renders current state to screen
- **Thread.sleep()**: Controls frame rate and yields CPU

### Timer-Based Approach (GameWindow):
- Separate Timers for different concerns:
  - GameTimer: General game updates (collision, scoring)
  - AlienFireTimer: Controlled alien firing rate
  - MovementTimer: Smooth player movement based on key state
- Each Timer uses ActionListener for callbacks

### Thread Management:
- **startGame()**: Initialize and start game thread
- **pauseGame()**: Toggle pause state
- **endGame()**: Stop game thread and cleanup
- **Proper Synchronization**: Check isRunning/isPaused flags before updates

## Recommended Framework Structure

Based on the examples, the assignment framework should include:

### Core Classes:
1. **GamePanel** (extends JPanel, implements Runnable)
   - Double buffering (built-in or manual)
   - Entity management (player, enemies, bullets)
   - Manager references (SoundManager, ScoreManager)
   - Game state tracking
   - Input state tracking
   - paintComponent override for rendering
   - Game update/render methods
   - Thread management

2. **GameWindow** (extends JFrame)
   - UI components (score/lives/level displays)
   - Button controls (start, pause, exit, etc.)
   - Event listeners (ActionListener, KeyListener, MouseListener)
   - Game timers for different update frequencies
   - Focus management

3. **ImageManager** (utility class)
   - Static image loading methods
   - BufferedImage conversion and copying
   - Basic image transformations (flip, etc.)

4. **SoundManager** (Singleton)
   - Sound clip storage and management
   - Playback control (looping, random variations)
   - Resource loading and cleanup

5. **Game Entities** (Player, Enemy, Bullet, etc.)
   - Position, size, velocity tracking
   - Bounding rectangle for collision
   - Draw and update methods
   - State management (active, alive, etc.)

6. **Managers** (as needed)
   - ScoreManager (Singleton)
   - AlienSwarm/Spawn managers
   - SolidObject/Floor managers for environment
   - Animation/ImageFX managers for effects

### Key Techniques to Implement:
- Double buffering for smooth rendering
- Sprite animation with multiple frames/states
- Image effects using pixel manipulation
- Rectangle-based collision detection
- Sound management with Singleton pattern
- Game loop with proper timing and threading
- Input handling for smooth movement
- Game state management (running, paused, game over)
- Resource loading and cleanup

This framework provides a solid foundation for creating 2D games in Java with Swing, following the patterns demonstrated in the provided examples.