import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.swing.JPanel;

/**
 * Player sprite with keyboard movement (arrow keys/WASD)
 * and animation states (idle, walk).
 */
public class PlayerSprite extends Sprite {
    
    private int worldX;
    private int worldY;
    private int dx = 25;
    private int dy = 25;
    private int baseSpeed = 25; // Store the normal speed
    private boolean speedBoostActive = false;
    private long speedBoostTimer = 0; // Time remaining in milliseconds
    private static final long SPEED_BOOST_DURATION = 1000; // 1 second
    private static final int SPEED_BOOST_MULTIPLIER = 3; // 3x speed
    private int screenX;
    private int screenY;
    
    // Animation states
    public static final int STATE_IDLE = 0;
    public static final int STATE_WALK = 1;
    private int currentState;
    
    // Movement direction
    public static final int DIR_LEFT = 1;
    public static final int DIR_RIGHT = 2;
    public static final int DIR_UP = 3;
    public static final int DIR_DOWN = 4;
    public static final int DIR_UP_LEFT = 5;
    public static final int DIR_UP_RIGHT = 6;
    public static final int DIR_DOWN_LEFT = 7;
    public static final int DIR_DOWN_RIGHT = 8;
    private int facingDirection;
    
    // World bounds
    private int worldWidth;
    private int worldHeight;
    
    // Sprite sheet configuration
    private static final int FRAME_WIDTH = 85;
    private static final int FRAME_HEIGHT = 120;
    private static final int FRAMES_PER_ROW = 16;
    
    // Animation directions
    public static final int ANIM_DIR_LEFT = 0;    // Row 0: running left (side view)
    public static final int ANIM_DIR_DOWN = 1;   // Row 1: running toward screen (front view)
    public static final int ANIM_DIR_DOWN_LEFT = 2; // Row 2: running 3/4 view
    
    // Animations
    private Animation idleAnim;
    private Animation walkLeftAnim;      // LEFT - Row 0, no flip
    private Animation walkRightAnim;      // RIGHT - Row 0, flipped
    private Animation walkUpLeftAnim;    // UP+LEFT - Row 0, no flip
    private Animation walkUpRightAnim;   // UP+RIGHT - Row 0, flipped
    private Animation walkDownAnim;      // DOWN - Row 1, no flip
    private Animation walkDownLeftAnim;  // DOWN+LEFT - Row 2, no flip
    private Animation walkDownRightAnim; // DOWN+RIGHT - Row 2, flipped
    private Animation currentAnimation;
    
    // Sound manager reference
    private SoundManager soundManager;
    
    /**
     * Utility method to clamp a value between min and max.
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
    
    public PlayerSprite(JPanel p, int xPos, int yPos, int worldW, int worldH) {
        super(p, xPos, yPos, 50, 50);
        
        worldX = xPos;
        worldY = yPos;
        screenX = xPos;
        screenY = yPos;
        dx = 25;
        dy = 25;
        width = 50;
        height = 50;
        
        worldWidth = worldW;
        worldHeight = worldH;
        
        currentState = STATE_IDLE;
        facingDirection = DIR_RIGHT;
        
        // Initialize sound manager
        soundManager = SoundManager.getInstance();
        
        // Load player sprite strip and set up animations using auto-detection
        loadSpriteAnimations();
    }
    
    /**
     * Load sprite animations using StripAnimation class.
     */
    private void loadSpriteAnimations() {
        // Use StripAnimation to load sprite strip and get animations
        Map<Integer, Animation> animations = StripAnimation.loadSpriteAnimations("playerRunningStrip.png", 80);
        
        if (animations == null || animations.isEmpty()) {
            System.out.println("Failed to load playerRunningStrip.png, falling back to player.png");
            image = ImageManager.loadImage("player.png");
            return;
        }
        
        System.out.println("Sprite animations loaded successfully");
        
        // Set sprite dimensions
        width = StripAnimation.FRAME_WIDTH;
        height = StripAnimation.FRAME_HEIGHT;
        
        // Get animations from the map
        walkLeftAnim = animations.get(0);      // Left - Row 0, no flip
        walkRightAnim = animations.get(1);     // Right - Row 0, flipped
        walkUpLeftAnim = animations.get(2);    // Up+Left - Row 0, no flip
        walkUpRightAnim = animations.get(3);   // Up+Right - Row 0, flipped
        walkDownAnim = animations.get(4);      // Down - Row 1, no flip
        walkDownLeftAnim = animations.get(5);  // Down+Left - Row 2, no flip
        walkDownRightAnim = animations.get(6); // Down+Right - Row 2, flipped
        
        // Create idle animation using first frame
        idleAnim = new Animation(true);
        if (walkLeftAnim != null) {
            // Get the first frame from walkLeftAnim for idle
            StripAnimation stripAnim = new StripAnimation();
            BufferedImage spriteStrip = ImageManager.loadBufferedImage("playerRunningStrip.png");
            if (spriteStrip != null) {
                BufferedImage[] row0Frames = stripAnim.extractFramesFromRow(spriteStrip, 0);
                if (row0Frames.length > 0) {
                    idleAnim.addFrame(row0Frames[0], 300);
                    image = row0Frames[0];
                }
            }
        }
        
        // Set default animation
        currentAnimation = idleAnim;
        currentAnimation.start();
    }
    
    public void move(int direction) {
        if (!panel.isVisible()) return;
        
        int oldWorldX = worldX;
        int oldWorldY = worldY;
        
        switch (direction) {
            case DIR_LEFT:
                worldX = worldX - dx;
                facingDirection = DIR_LEFT;
                currentState = STATE_WALK;
                // Row 0 (side view running left), no flip
                if (walkLeftAnim != null) {
                    currentAnimation = walkLeftAnim;
                    if (!currentAnimation.isActive()) {
                        currentAnimation.start();
                    }
                }
                // Play footstep sound
                soundManager.startFootstep();
                break;
            case DIR_RIGHT:
                worldX = worldX + dx;
                facingDirection = DIR_RIGHT;
                currentState = STATE_WALK;
                // Row 0 (side view running left), flipped horizontally
                if (walkRightAnim != null) {
                    currentAnimation = walkRightAnim;
                    if (!currentAnimation.isActive()) {
                        currentAnimation.start();
                    }
                }
                // Play footstep sound
                soundManager.startFootstep();
                break;
            case DIR_UP:
                worldY = worldY - dy;
                currentState = STATE_WALK;
                // Use right animation for up (side view)
                if (walkRightAnim != null) {
                    currentAnimation = walkRightAnim;
                    if (!currentAnimation.isActive()) {
                        currentAnimation.start();
                    }
                }
                // Play footstep sound
                soundManager.startFootstep();
                break;
            case DIR_DOWN:
                worldY = worldY + dy;
                currentState = STATE_WALK;
                // Row 1 (front view running toward screen)
                if (walkDownAnim != null) {
                    currentAnimation = walkDownAnim;
                    if (!currentAnimation.isActive()) {
                        currentAnimation.start();
                    }
                }
                // Play footstep sound
                soundManager.startFootstep();
                break;
            case DIR_UP_LEFT:
                worldX = worldX - dx;
                worldY = worldY - dy;
                facingDirection = DIR_UP_LEFT;
                currentState = STATE_WALK;
                // Row 0 (side view running left), no flip
                if (walkUpLeftAnim != null) {
                    currentAnimation = walkUpLeftAnim;
                    if (!currentAnimation.isActive()) {
                        currentAnimation.start();
                    }
                }
                // Play footstep sound
                soundManager.startFootstep();
                break;
            case DIR_UP_RIGHT:
                worldX = worldX + dx;
                worldY = worldY - dy;
                facingDirection = DIR_UP_RIGHT;
                currentState = STATE_WALK;
                // Row 0 (side view running left), flipped horizontally
                if (walkUpRightAnim != null) {
                    currentAnimation = walkUpRightAnim;
                    if (!currentAnimation.isActive()) {
                        currentAnimation.start();
                    }
                }
                // Play footstep sound
                soundManager.startFootstep();
                break;
            case DIR_DOWN_LEFT:
                worldX = worldX - dx;
                worldY = worldY + dy;
                facingDirection = DIR_DOWN_LEFT;
                currentState = STATE_WALK;
                // Row 2 (3/4 view), no flip
                if (walkDownLeftAnim != null) {
                    currentAnimation = walkDownLeftAnim;
                    if (!currentAnimation.isActive()) {
                        currentAnimation.start();
                    }
                }
                // Play footstep sound
                soundManager.startFootstep();
                break;
            case DIR_DOWN_RIGHT:
                worldX = worldX + dx;
                worldY = worldY + dy;
                facingDirection = DIR_DOWN_RIGHT;
                currentState = STATE_WALK;
                // Row 2 (3/4 view), flipped horizontally
                if (walkDownRightAnim != null) {
                    currentAnimation = walkDownRightAnim;
                    if (!currentAnimation.isActive()) {
                        currentAnimation.start();
                    }
                }
                // Play footstep sound
                soundManager.startFootstep();
                break;
        }
        
        // Clamp to world bounds
        worldX = clamp(worldX, 0, worldWidth - width);
        worldY = clamp(worldY, 0, worldHeight - height);
    }
    
    /**
     * Updates the screen position based on world position and camera offset.
     * 
     * Screen position = world position - camera position.
     * The camera is already clamped to world bounds, so this formula correctly
     * centers the player on screen when not at boundaries, and shows player
     * at the edge when camera hits world boundaries.
     */
    public void updateScreenPosition(int cameraX, int cameraY) {
        // Calculate screen position based on camera offset
        screenX = worldX - cameraX;
        screenY = worldY - cameraY;
    }
    
    public void setIdle() {
        currentState = STATE_IDLE;
        if (idleAnim != null) {
            currentAnimation = idleAnim;
            if (!currentAnimation.isActive()) {
                currentAnimation.start();
            }
        }
        // Stop footstep sound when player stops moving
        soundManager.stopFootstep();
    }
    
    /**
     * Update the current animation.
     */
    public void update() {
        if (currentAnimation != null) {
            currentAnimation.update();
        }
    }
    
    /**
     * Draws the player at screen position.
     * 
     * The player is drawn at screenX, screenY which is calculated as:
     * screenX = worldX - cameraX
     * screenY = worldY - cameraY
     * 
     * Since camera is centered on player, player always appears at
     * center of screen (400, 300) while moving through the world.
     * 
     * Each animation frame is drawn at its own natural size rather than
     * being stretched to fit a fixed size.
     */
    public void draw(Graphics2D g2) {
        // Get the current frame image and update dimensions to match
        if (currentAnimation != null && currentAnimation.getImage() != null) {
            Image currentFrame = currentAnimation.getImage();
            width = currentFrame.getWidth(null);
            height = currentFrame.getHeight(null);
            g2.drawImage(currentFrame, screenX, screenY, width, height, null);
        } else if (image != null) {
            width = image.getWidth(null);
            height = image.getHeight(null);
            g2.drawImage(image, screenX, screenY, width, height, null);
        }
    }
    
    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double(worldX, worldY, width, height);
    }
    
    public Rectangle2D.Double getScreenBoundingRectangle() {
        return new Rectangle2D.Double(screenX, screenY, width, height);
    }
    
    public int getWorldX() {
        return worldX;
    }
    
    public int getWorldY() {
        return worldY;
    }
    
    public int getScreenX() {
        return screenX;
    }
    
    public int getScreenY() {
        return screenY;
    }
    
    public int getCurrentState() {
        return currentState;
    }
    
    public int getFacingDirection() {
        return facingDirection;
    }
    
    public int getDx() {
        return dx;
    }
    
    public int getDy() {
        return dy;
    }
    
    public void setDx(int dx) {
        this.dx = dx;
    }
    
    public void setDy(int dy) {
        this.dy = dy;
    }
    
    public void setSpeed(int s) {
        dx = s;
        dy = s;
    }
    
    /**
     * Activates the speed boost (3x speed for 3 seconds).
     * Multiple activations reset the timer.
     */
    public void activateSpeedBoost() {
        baseSpeed = 25; // Ensure baseSpeed is set to normal value
        dx = baseSpeed * SPEED_BOOST_MULTIPLIER;
        dy = baseSpeed * SPEED_BOOST_MULTIPLIER;
        speedBoostActive = true;
        speedBoostTimer = SPEED_BOOST_DURATION;
    }
    
    /**
     * Updates the speed boost timer. Call this in the game loop.
     * @param deltaTime time elapsed since last update in milliseconds
     */
    public void updateSpeedBoost(long deltaTime) {
        if (speedBoostActive) {
            speedBoostTimer -= deltaTime;
            if (speedBoostTimer <= 0) {
                // Speed boost expired, reset to base speed
                speedBoostTimer = 0;
                speedBoostActive = false;
                dx = baseSpeed;
                dy = baseSpeed;
            }
        }
    }
    
    public boolean isSpeedBoostActive() {
        return speedBoostActive;
    }
    
    public long getSpeedBoostTimer() {
        return speedBoostTimer;
    }
    
    public int getBaseSpeed() {
        return baseSpeed;
    }
    
    public void setWorldX(int x) {
        worldX = x;
    }
    
    public void setWorldY(int y) {
        worldY = y;
    }
}
