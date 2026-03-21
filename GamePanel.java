import javax.swing.JPanel;
import javax.swing.JComponent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Main game panel with double buffering, game loop, 
 * large background scrolling, and collision detection.
 */
public class GamePanel extends JPanel {
    
    // Game state
    private boolean gameRunning;
    private boolean gamePaused;
    private boolean gameOver;
    
    // World dimensions (larger than panel) - set to 2500x2500
    private int WORLD_WIDTH = 2500;
    private int WORLD_HEIGHT = 2500;
    
    // Player and sprites
    private PlayerSprite player;
    private ArrayList<AnimatedSprite> animatedSprites;
    private ArrayList<SolidObject> solidObjects;
    private ArrayList<Collectible> collectibles;
    
    // Camera position
    private int cameraX;
    private int cameraY;
    
    // Key states
    private boolean leftKeyPressed;
    private boolean rightKeyPressed;
    private boolean upKeyPressed;
    private boolean downKeyPressed;
    
    // Background
    private BufferedImage backgroundImage;
    
    // Double buffering
    private BufferedImage doubleBufferImage;
    private Graphics2D doubleBufferG2;
    
    // Image effects
    private ArrayList<ImageFX> effects;
    private String activeEffectName;
    
    // Collectibles tracking
    private int collectedCount;
    private int totalCollectibles;
    private static final int WIN_COLLECTIBLES = 10;
    
    // FPS tracking
    private long lastFrameTime;
    private int fps;
    
    // Sound
    private SoundManager soundManager;
    
    // Info panel reference
    private InfoPanel infoPanel;
    
    public GamePanel() {
        this(null);
    }
    
    public GamePanel(InfoPanel info) {
        infoPanel = info;
        
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(800, 600));
        
        gameRunning = false;
        gamePaused = false;
        gameOver = false;
        
        leftKeyPressed = false;
        rightKeyPressed = false;
        upKeyPressed = false;
        downKeyPressed = false;
        
        cameraX = 0;
        cameraY = 0;
        
        collectedCount = 0;
        totalCollectibles = 0;
        
        animatedSprites = new ArrayList<AnimatedSprite>();
        solidObjects = new ArrayList<SolidObject>();
        collectibles = new ArrayList<Collectible>();
        effects = new ArrayList<ImageFX>();
        
        activeEffectName = "None";
        
        soundManager = SoundManager.getInstance();
        
        // Initialize double buffering
        doubleBufferImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        doubleBufferG2 = doubleBufferImage.createGraphics();
        
        // Load background image
        backgroundImage = ImageManager.loadBufferedImage("worldBackgroundSmall.png");
        if (backgroundImage != null) {
            System.out.println("World background loaded: " + WORLD_WIDTH + "x" + WORLD_HEIGHT);
        } else {
            System.out.println("Failed to load worldBackgroundSmall.png, using default dimensions");
        }
        
        lastFrameTime = System.currentTimeMillis();
        fps = 0;
    }
    
    public void createGameEntities() {
        // Create player at world center
        int playerStartX = WORLD_WIDTH / 2 - 25;  // Center of world minus half player width
        int playerStartY = WORLD_HEIGHT / 2 - 25; // Center of world minus half player height
        player = new PlayerSprite(this, playerStartX, playerStartY, WORLD_WIDTH, WORLD_HEIGHT);
        
        // Create solid objects (walls, obstacles)
        createSolidObjects();
        
        // Create collectibles
        createCollectibles();
        
        // Create animated sprites
        createAnimatedSprites();
        
        // Reset camera
        cameraX = 0;
        cameraY = 0;
        
        // Reset counters
        collectedCount = 0;
        totalCollectibles = collectibles.size();
    }
    
    private void createSolidObjects() {
        solidObjects.clear();
        // Add some solid objects
        solidObjects.add(new SolidObject(300, 200, 100, 50));
        solidObjects.add(new SolidObject(600, 400, 150, 30));
        solidObjects.add(new SolidObject(900, 150, 50, 200));
        solidObjects.add(new SolidObject(1200, 300, 100, 100));
        solidObjects.add(new SolidObject(400, 600, 200, 40));
    }
    
    private void createCollectibles() {
        collectibles.clear();
        // Add collectibles scattered around the world
        int[] positions = {200, 400, 600, 800, 1000, 1200, 1400, 1600, 1800, 2000};
        for (int i = 0; i < positions.length; i++) {
            collectibles.add(new Collectible(positions[i], 200 + (i * 50), 30, 30));
        }
    }
    
    private void createAnimatedSprites() {
        animatedSprites.clear();
        // Add some animated sprites
        AnimatedSprite sprite = new AnimatedSprite(this, 500, 300, 50, 50);
        
        // Create animation with frames
        Animation anim = new Animation(true);
        anim.addFrame(ImageManager.loadImage("sprite1.png"), 200);
        anim.addFrame(ImageManager.loadImage("sprite2.png"), 200);
        sprite.setAnimation(anim);
        
        animatedSprites.add(sprite);
    }
    
    public void startGame() {
        if (gameRunning) return;
        
        gameRunning = true;
        gamePaused = false;
        gameOver = false;
        activeEffectName = "None";
        
        createGameEntities();
        
        // Start background music
        soundManager.playClip("background", true);
        
        repaint();
    }
    
    public void resetGame() {
        stopGame();
        gameOver = false;
        gameRunning = false;
        gamePaused = false;
        activeEffectName = "None";
        
        createGameEntities();
        
        startGame();
    }
    
    public void pauseGame() {
        gamePaused = !gamePaused;
        
        if (gamePaused) {
            soundManager.stopClip("background");
        } else {
            soundManager.playClip("background", true);
        }
    }
    
    public void stopGame() {
        gameRunning = false;
        soundManager.stopClip("background");
    }
    
    public void triggerGameOver(boolean won) {
        gameOver = true;
        gameRunning = false;
        soundManager.stopAll();
        
        if (won) {
            activeEffectName = "GrayScale";
        }
        
        repaint();
    }
    
    public void updatePlayer() {
        if (player == null || !gameRunning || gamePaused) return;
        
        // Store old world position for collision reversion
        int oldWorldX = player.getWorldX();
        int oldWorldY = player.getWorldY();
        
        // Update player movement - check DIAGONAL directions FIRST (most specific)
        // Then check BASE directions
        int moveDirection = 0;
        
        // Check diagonal directions first (most specific)
        if (upKeyPressed && leftKeyPressed) {
            moveDirection = PlayerSprite.DIR_UP_LEFT;
        } else if (upKeyPressed && rightKeyPressed) {
            moveDirection = PlayerSprite.DIR_UP_RIGHT;
        } else if (downKeyPressed && leftKeyPressed) {
            moveDirection = PlayerSprite.DIR_DOWN_LEFT;
        } else if (downKeyPressed && rightKeyPressed) {
            moveDirection = PlayerSprite.DIR_DOWN_RIGHT;
        } else if (leftKeyPressed && !rightKeyPressed) {
            moveDirection = PlayerSprite.DIR_LEFT;
        } else if (rightKeyPressed && !leftKeyPressed) {
            moveDirection = PlayerSprite.DIR_RIGHT;
        } else if (upKeyPressed && !downKeyPressed) {
            moveDirection = PlayerSprite.DIR_UP;
        } else if (downKeyPressed && !upKeyPressed) {
            moveDirection = PlayerSprite.DIR_DOWN;
        }
        
        // Move player in the determined direction
        if (moveDirection != 0) {
            player.move(moveDirection);
            
            // Check collision after movement
            Rectangle2D.Double playerBounds = player.getBoundingRectangle();
            for (SolidObject solid : solidObjects) {
                if (playerBounds.intersects(solid.getBoundingRectangle())) {
                    // Revert position
                    player.setWorldX(oldWorldX);
                    player.setWorldY(oldWorldY);
                    break;
                }
            }
        }
        
        if (!leftKeyPressed && !rightKeyPressed && !upKeyPressed && !downKeyPressed) {
            player.setIdle();
        }
        
        // Update player animation
        player.update();
        
        // Update camera to follow player
        updateCamera();
        
        // Update player screen position
        player.updateScreenPosition(cameraX, cameraY);
        
        // Update animated sprites
        for (AnimatedSprite sprite : animatedSprites) {
            sprite.updateScreenPosition(cameraX, cameraY);
            sprite.update();
        }
        
        // Update collectibles screen positions
        for (Collectible collectible : collectibles) {
            collectible.updateScreenPosition(cameraX, cameraY);
        }
        
        // Play movement sound
        if ((leftKeyPressed || rightKeyPressed || upKeyPressed || downKeyPressed) && 
            soundManager != null && !soundManager.isPlaying("footstep")) {
            // Sound would play here
        }
        
        repaint();
    }
    
    /**
     * Updates the camera position to follow the player.
     * 
     * Camera System:
     * - When player can move freely, player stays centered on screen
     * - When camera would go past world edges (0,0 or WORLD_WIDTH/WORLD_HEIGHT), 
     *   camera is clamped and player appears at edge instead of centered
     * - Camera is clamped to world boundaries so we can't see outside the world
     */
    private void updateCamera() {
        // Calculate ideal camera position to center player on screen
        int playerWidth = player.getWidth();
        int playerHeight = player.getHeight();
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        // Center camera on player
        cameraX = player.getWorldX() - panelWidth / 2 + playerWidth / 2;
        cameraY = player.getWorldY() - panelHeight / 2 + playerHeight / 2;
        
        // Clamp camera to world boundaries
        // This ensures we can't see outside the world (2500x2500)
        // and when at boundaries, player appears at edge instead of going off-screen
        cameraX = Math.max(0, Math.min(cameraX, WORLD_WIDTH - panelWidth));
        cameraY = Math.max(0, Math.min(cameraY, WORLD_HEIGHT - panelHeight));
    }
    
    public void checkCollisions() {
        if (player == null || !gameRunning || gamePaused) return;
        
        Rectangle2D.Double playerBounds = player.getBoundingRectangle();
        
        // Check collectible collisions
        for (Collectible collectible : collectibles) {
            if (!collectible.isCollected() && 
                playerBounds.intersects(collectible.getBoundingRectangle())) {
                collectible.collect();
                collectedCount++;
                soundManager.playClip("collect", false);
                
                // Check win condition
                if (collectedCount >= WIN_COLLECTIBLES) {
                    triggerGameOver(true);
                }
            }
        }
    }
    
    public void updateEffects() {
        for (ImageFX effect : effects) {
            effect.update();
        }
    }
    
    public void drawGameEntities() {
        repaint();
    }
    
    public void applyEffect(String effectName) {
        activeEffectName = effectName;
        
        // Create appropriate effect
        switch (effectName) {
            case "disappear":
                effects.add(new DisappearFX(100, 100, 100, 100, "effect.png"));
                break;
            case "grayscale":
                effects.add(new GrayScaleFX(300, 100, 100, 100, "effect.png"));
                break;
            case "tint":
                effects.add(new TintFX(500, 100, 100, 100, "effect.png", 0xFF0000));
                break;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Use double buffering
        if (doubleBufferImage != null) {
            // Draw to buffer
            drawToBuffer(doubleBufferG2);
            
            // Draw buffer to screen
            g.drawImage(doubleBufferImage, 0, 0, null);
        } else {
            // Fallback to direct drawing
            drawToBuffer(g2);
        }
        
        // Update FPS
        updateFPS();
    }
    
    private void drawToBuffer(Graphics2D g2) {
        // Clear background
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw background with camera scrolling
        // The background image (2500x2500) is drawn offset by -cameraX, -cameraY
        // This creates the scrolling effect - as player moves right, camera moves right,
        // and background moves left (opposite direction)
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, -cameraX, -cameraY, WORLD_WIDTH, WORLD_HEIGHT, null);
        }
        
        if (!gameRunning && !gameOver) {
            // Draw start screen
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            g2.drawString("Visual Playground", 250, 280);
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            g2.drawString("Press Start to begin", 320, 330);
            return;
        }
        
        // Draw solid objects
        g2.setColor(new Color(100, 100, 100));
        for (SolidObject solid : solidObjects) {
            solid.draw(g2, cameraX, cameraY);
        }
        
        // Draw collectibles
        for (Collectible collectible : collectibles) {
            collectible.draw(g2);
        }
        
        // Draw animated sprites
        for (AnimatedSprite sprite : animatedSprites) {
            sprite.draw(g2);
        }
        
        // Draw player
        if (player != null) {
            player.draw(g2);
        }
        
        // Draw image effects
        for (ImageFX effect : effects) {
            effect.draw(g2);
        }
        
        // Draw game over screen
        if (gameOver) {
            // Apply grayscale effect to entire screen
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            
            if (collectedCount >= WIN_COLLECTIBLES) {
                g2.drawString("You Win!", 280, 280);
            } else {
                g2.drawString("Game Over", 270, 280);
            }
            
            g2.setFont(new Font("Arial", Font.PLAIN, 24));
            g2.drawString("Collectibles: " + collectedCount + " / " + WIN_COLLECTIBLES, 280, 330);
        }
    }
    
    private void updateFPS() {
        long currentTime = System.currentTimeMillis();
        long delta = currentTime - lastFrameTime;
        
        if (delta > 0) {
            fps = (int)(1000 / delta);
        }
        
        lastFrameTime = currentTime;
    }
    
    // Key state setters
    public void setLeftKeyPressed(boolean pressed) {
        leftKeyPressed = pressed;
    }
    
    public void setRightKeyPressed(boolean pressed) {
        rightKeyPressed = pressed;
    }
    
    public void setUpKeyPressed(boolean pressed) {
        upKeyPressed = pressed;
    }
    
    public void setDownKeyPressed(boolean pressed) {
        downKeyPressed = pressed;
    }
    
    // Game state getters
    public boolean isGameRunning() {
        return gameRunning;
    }
    
    public boolean isGamePaused() {
        return gamePaused;
    }
    
    public boolean isGameOver() {
        return gameOver;
    }
    
    public PlayerSprite getPlayer() {
        return player;
    }
    
    public int getFPS() {
        return fps;
    }
    
    public int getCollectedCount() {
        return collectedCount;
    }
    
    public int getTotalCollectibles() {
        return totalCollectibles;
    }
    
    public String getActiveEffectName() {
        return activeEffectName;
    }
}
