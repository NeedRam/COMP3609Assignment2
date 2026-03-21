import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * StripAnimation - A utility class for extracting and managing animations from sprite sheets.
 * Provides methods to extract frames from sprite sheet rows, create animations,
 * and flip images horizontally.
 */
public class StripAnimation {
    
    // Sprite sheet configuration constants
    public static final int FRAME_WIDTH = 85;
    public static final int FRAME_HEIGHT = 120;
    public static final int FRAMES_PER_ROW = 16;
    
    // Frame dimensions (can be customized via constructor)
    private int frameWidth;
    private int frameHeight;
    private int framesPerRow;
    
    // Animation speed control (in milliseconds per frame)
    private long animationSpeed = 200;
    
    /**
     * Default constructor using default frame dimensions.
     */
    public StripAnimation() {
        this(FRAME_WIDTH, FRAME_HEIGHT, FRAMES_PER_ROW);
    }
    
    /**
     * Constructor with custom frame dimensions.
     * 
     * @param frameWidth The width of each frame in pixels
     * @param frameHeight The height of each frame in pixels
     * @param framesPerRow The number of frames per row in the sprite sheet
     */
    public StripAnimation(int frameWidth, int frameHeight, int framesPerRow) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.framesPerRow = framesPerRow;
    }
    
    /**
     * Get the animation speed (duration per frame in milliseconds).
     * 
     * @return The animation speed in milliseconds
     */
    public long getAnimationSpeed() {
        return animationSpeed;
    }
    
    /**
     * Set the animation speed (duration per frame in milliseconds).
     * The speed must be at least 1 millisecond.
     * 
     * @param speed The animation speed in milliseconds (must be >= 1)
     */
    public void setAnimationSpeed(long speed) {
        if (speed < 1) {
            this.animationSpeed = 1;
        } else {
            this.animationSpeed = speed;
        }
    }
    
    /**
     * Extract frames from a specific row in the sprite sheet.
     * Each row represents a different animation direction.
     * 
     * @param spriteSheet The source sprite sheet image
     * @param rowIndex The row index (0, 1, or 2)
     * @return Array of BufferedImage frames
     */
    public BufferedImage[] extractFramesFromRow(BufferedImage spriteSheet, int rowIndex) {
        if (spriteSheet == null) {
            return new BufferedImage[0];
        }
        
        BufferedImage[] frames = new BufferedImage[framesPerRow];
        int y = rowIndex * frameHeight;
        
        for (int i = 0; i < framesPerRow; i++) {
            int x = i * frameWidth;
            
            // Make sure we don't go beyond the sprite sheet bounds
            if (x + frameWidth <= spriteSheet.getWidth() && y + frameHeight <= spriteSheet.getHeight()) {
                frames[i] = spriteSheet.getSubimage(x, y, frameWidth, frameHeight);
            } else {
                // If we run out of frames, break out of loop
                break;
            }
        }
        
        // Count valid frames
        int validFrames = 0;
        for (BufferedImage frame : frames) {
            if (frame != null) {
                validFrames++;
            }
        }
        
        // Return array with only valid frames
        BufferedImage[] result = new BufferedImage[validFrames];
        System.arraycopy(frames, 0, result, 0, validFrames);
        
        return result;
    }
    
    /**
     * Create an animation from an array of frames, optionally flipped horizontally.
     * 
     * @param frames The source frames
     * @param frameDuration Duration per frame in milliseconds
     * @param flip Whether to flip the frames horizontally
     * @return Animation object
     */
    public Animation createAnimationFromFrames(BufferedImage[] frames, long frameDuration, boolean flip) {
        Animation anim = new Animation(true);
        
        for (BufferedImage frame : frames) {
            if (flip) {
                BufferedImage flippedFrame = flipImageHorizontally(frame);
                if (flippedFrame != null) {
                    anim.addFrame(flippedFrame, frameDuration);
                }
            } else {
                anim.addFrame(frame, frameDuration);
            }
        }
        
        return anim;
    }
    
    /**
     * Flip an image horizontally using AffineTransform.
     * This provides better quality flipping than the traditional method.
     * 
     * @param src The source image to flip
     * @return The horizontally flipped BufferedImage
     */
    public BufferedImage flipImageHorizontally(BufferedImage src) {
        if (src == null) return null;
        
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage flipped = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = flipped.createGraphics();
        
        // Create affine transform for horizontal flip
        AffineTransform transform = new AffineTransform();
        transform.translate(width, 0);
        transform.scale(-1, 1);
        
        g2d.drawImage(src, transform, null);
        g2d.dispose();
        
        return flipped;
    }
    
    /**
     * Load sprite strip and create animations keyed by direction.
     * Returns a Map where keys are direction constants and values are Animation objects.
     * 
     * Direction constants:
     * - 0: Left animation (no flip) - Row 0
     * - 1: Right animation (flipped) - Row 0 flipped
     * - 2: Up Left animation (no flip) - Row 0 (same as left)
     * - 3: Up Right animation (flipped) - Row 0 flipped (same as right)
     * - 4: Down animation (no flip) - Row 1
     * - 5: Down Left animation (no flip) - Row 2
     * - 6: Down Right animation (flipped) - Row 2 flipped
     * 
     * @param spriteSheetPath Path to the sprite sheet image file
     * @param frameDuration Duration per frame in milliseconds
     * @return Map of direction constants to Animation objects
     */
    public static Map<Integer, Animation> loadSpriteAnimations(String spriteSheetPath, long frameDuration) {
        // Create StripAnimation instance and set the animation speed for backward compatibility
        StripAnimation stripAnim = new StripAnimation();
        stripAnim.setAnimationSpeed(frameDuration);
        
        // Use the instance method which uses this.animationSpeed
        return stripAnim.loadSpriteAnimations(spriteSheetPath);
    }
    
    /**
     * Load sprite strip and create animations with custom frame dimensions.
     * 
     * @param spriteSheetPath Path to the sprite sheet image file
     * @param frameWidth Width of each frame
     * @param frameHeight Height of each frame
     * @param framesPerRow Number of frames per row
     * @param frameDuration Duration per frame in milliseconds
     * @return Map of direction constants to Animation objects
     */
    public static Map<Integer, Animation> loadSpriteAnimations(String spriteSheetPath, 
                                                                 int frameWidth, int frameHeight, 
                                                                 int framesPerRow, long frameDuration) {
        // Create StripAnimation instance with custom dimensions
        StripAnimation stripAnim = new StripAnimation(frameWidth, frameHeight, framesPerRow);
        stripAnim.setAnimationSpeed(frameDuration);
        
        // Use the instance method which uses this.animationSpeed
        return stripAnim.loadSpriteAnimations(spriteSheetPath);
    }
    
    /**
     * Load sprite strip and create animations using the instance's animationSpeed.
     * Returns a Map where keys are direction constants and values are Animation objects.
     * This method provides a single point of control for animation speed through
     * the animationSpeed instance variable.
     * 
     * Direction constants:
     * - 0: Left animation (no flip) - Row 0
     * - 1: Right animation (flipped) - Row 0 flipped
     * - 2: Up Left animation (no flip) - Row 0 (same as left)
     * - 3: Up Right animation (flipped) - Row 0 flipped (same as right)
     * - 4: Down animation (no flip) - Row 1
     * - 5: Down Left animation (no flip) - Row 2
     * - 6: Down Right animation (flipped) - Row 2 flipped
     * 
     * @param spriteSheetPath Path to the sprite sheet image file
     * @return Map of direction constants to Animation objects
     */
    public Map<Integer, Animation> loadSpriteAnimations(String spriteSheetPath) {
        Map<Integer, Animation> animations = new HashMap<>();
        
        // Load the sprite strip using ImageManager
        BufferedImage spriteSheet = ImageManager.loadBufferedImage(spriteSheetPath);
        
        if (spriteSheet == null) {
            System.out.println("Failed to load sprite sheet: " + spriteSheetPath);
            return animations;
        }
        
        System.out.println("Sprite sheet loaded: " + spriteSheet.getWidth() + "x" + spriteSheet.getHeight());
        
        // Extract frames for each direction row
        BufferedImage[] row0Frames = this.extractFramesFromRow(spriteSheet, 0); // Row 0: Left running (side view)
        BufferedImage[] row1Frames = this.extractFramesFromRow(spriteSheet, 1); // Row 1: Down (front view)
        BufferedImage[] row2Frames = this.extractFramesFromRow(spriteSheet, 2); // Row 2: Down+Left (3/4 view)
        
        System.out.println("Extracted frames - Row 0: " + row0Frames.length + ", Row 1: " + row1Frames.length + ", Row 2: " + row2Frames.length);
        
        // Create LEFT animation - Row 0, no flip
        if (row0Frames.length > 0) {
            animations.put(0, this.createAnimationFromFrames(row0Frames, this.animationSpeed, false));
        }
        
        // Create RIGHT animation - Row 0, flipped horizontally
        if (row0Frames.length > 0) {
            animations.put(1, this.createAnimationFromFrames(row0Frames, this.animationSpeed, true));
        }
        
        // Create UP+LEFT animation - Row 0, no flip (same as LEFT)
        if (row0Frames.length > 0) {
            animations.put(2, this.createAnimationFromFrames(row0Frames, this.animationSpeed, false));
        }
        
        // Create UP+RIGHT animation - Row 0, flipped horizontally
        if (row0Frames.length > 0) {
            animations.put(3, this.createAnimationFromFrames(row0Frames, this.animationSpeed, true));
        }
        
        // Create DOWN animation - Row 1 (front view), no flip
        if (row1Frames.length > 0) {
            animations.put(4, this.createAnimationFromFrames(row1Frames, this.animationSpeed, false));
        }
        
        // Create DOWN+LEFT animation - Row 2 (3/4 view), no flip
        if (row2Frames.length > 0) {
            animations.put(5, this.createAnimationFromFrames(row2Frames, this.animationSpeed, false));
        }
        
        // Create DOWN+RIGHT animation - Row 2 (3/4 view), flipped horizontally
        if (row2Frames.length > 0) {
            animations.put(6, this.createAnimationFromFrames(row2Frames, this.animationSpeed, true));
        }
        
        return animations;
    }
    
    /**
     * Get the frame width.
     * 
     * @return The frame width in pixels
     */
    public int getFrameWidth() {
        return frameWidth;
    }
    
    /**
     * Get the frame height.
     * 
     * @return The frame height in pixels
     */
    public int getFrameHeight() {
        return frameHeight;
    }
    
    /**
     * Get the number of frames per row.
     * 
     * @return The number of frames per row
     */
    public int getFramesPerRow() {
        return framesPerRow;
    }
    
    /**
     * Set custom frame dimensions.
     * 
     * @param frameWidth The width of each frame in pixels
     * @param frameHeight The height of each frame in pixels
     * @param framesPerRow The number of frames per row in the sprite sheet
     */
    public void setFrameDimensions(int frameWidth, int frameHeight, int framesPerRow) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.framesPerRow = framesPerRow;
    }
}
