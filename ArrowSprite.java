import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Arrow sprite that positions itself 250px from the player in the direction
 * of the nearest uncollected collectible coin, pointing towards it.
 * Uses DisappearFX for the fading effect.
 */
public class ArrowSprite {
    
    private BufferedImage originalImage;
    private BufferedImage currentImage;
    private DisappearFX disappearFX;
    private int screenX;
    private int screenY;
    private double rotationAngle;
    private float currentAlpha;
    private static final double ORBIT_DISTANCE = 250;
    private static final int MIN_FADE_DISTANCE = 300;
    
    // Arrow dimensions
    private int width;
    private int height;
    
    /**
     * Constructor that loads the arrow image and creates DisappearFX instance.
     */
    public ArrowSprite() {
        // Load the arrow image using ImageManager.loadBufferedImage()
        originalImage = ImageManager.loadBufferedImage("images/Arrow.png");
        
        if (originalImage != null) {
            width = originalImage.getWidth();
            height = originalImage.getHeight();
            // Create a copy for pixel manipulation
            currentImage = ImageManager.copyImage(originalImage);
        } else {
            // Fallback dimensions if image fails to load
            width = 32;
            height = 32;
        }
        
        // Create DisappearFX instance for alpha manipulation
        // Use 0, 0 for x, y since we'll set position in update()
        disappearFX = new DisappearFX(0, 0, width, height, "images/Arrow.png");
        
        rotationAngle = 0;
        screenX = 0;
        screenY = 0;
        currentAlpha = 1.0f;
    }
    
    /**
     * Update the arrow position and rotation.
     * 
     * @param playerScreenX Player's screen X position
     * @param playerScreenY Player's screen Y position
     * @param collectibles ArrayList of Collectible objects
     */
    public void update(int playerScreenX, int playerScreenY, ArrayList<Collectible> collectibles) {
        // Find the nearest uncollected collectible relative to the player
        Collectible nearestCoin = findNearestUncollectedCoin(playerScreenX, playerScreenY, collectibles);
        
        if (nearestCoin != null) {
            // Calculate angle from player to nearest coin using Math.atan2
            double dx = nearestCoin.getScreenX() - playerScreenX;
            double dy = nearestCoin.getScreenY() - playerScreenY;
            
            // Calculate the distance from player to nearest coin
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            // Calculate alpha based on distance:
            // distance >= 500: alpha = 1.0 (fully visible)
            // distance <= 300: alpha = 0.0 (fully invisible)
            // Between 300 and 500: alpha = (distance - 300) / (500 - 300), clamped to [0, 1]
            if (distance >= 500) {
                currentAlpha = 1.0f;
            } else if (distance <= MIN_FADE_DISTANCE) {
                currentAlpha = 0.0f;
            } else {
                currentAlpha = (float)((distance - MIN_FADE_DISTANCE) / 200);
            }
            
            // Use AlphaComposite in draw() for proper alpha rendering
            // No need to call disappearFX.setAlpha() - we use composite instead
            
            // Calculate the angle from player to coin (in radians)
            double angleToCoin = Math.atan2(dy, dx);
            
            // Position the arrow at 250px from player in the direction of the coin
            screenX = playerScreenX + (int)(Math.cos(angleToCoin) * ORBIT_DISTANCE);
            screenY = playerScreenY + (int)(Math.sin(angleToCoin) * ORBIT_DISTANCE);
            
            // Update DisappearFX position
            disappearFX.setPosition(screenX, screenY);
            
            // Set rotation angle (Arrow.png points right, so we use the angle directly)
            // Convert to degrees for AffineTransform
            rotationAngle = Math.toDegrees(angleToCoin);
        } else {
            // No uncollected coins - hide the arrow at player's position
            screenX = playerScreenX;
            screenY = playerScreenY;
            rotationAngle = 0;
            
            // Use alpha = 0 to hide the arrow (handled by AlphaComposite in draw())
            currentAlpha = 0.0f;
            
            // Update DisappearFX position
            disappearFX.setPosition(screenX, screenY);
        }
    }
    
    /**
     * Find the nearest uncollected collectible from the list relative to a position.
     * 
     * @param fromX X position to calculate distance from
     * @param fromY Y position to calculate distance from
     * @param collectibles ArrayList of Collectible objects
     * @return The nearest uncollected Collectible, or null if none available
     */
    private Collectible findNearestUncollectedCoin(int fromX, int fromY, ArrayList<Collectible> collectibles) {
        if (collectibles == null || collectibles.isEmpty()) {
            return null;
        }
        
        Collectible nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Collectible collectible : collectibles) {
            // Only consider uncollected coins
            if (!collectible.isCollected()) {
                double dx = collectible.getScreenX() - fromX;
                double dy = collectible.getScreenY() - fromY;
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = collectible;
                }
            }
        }
        
        return nearest;
    }
    
    /**
     * Draw the arrow with rotation applied.
     * Uses AffineTransform for proper rotation around the arrow's center.
     * Uses AlphaComposite for proper alpha/fade effect based on distance to coin.
     * 
     * @param g2 The Graphics2D object to draw with
     */
    public void draw(Graphics2D g2) {
        // Get the alpha-applied image from DisappearFX
        BufferedImage imageToDraw = disappearFX.getCurrentImage();
        
        if (imageToDraw == null || currentAlpha <= 0.0f) {
            return;
        }
        
        // Save the current transform and composite
        AffineTransform originalTransform = g2.getTransform();
        Composite originalComposite = g2.getComposite();
        
        // Calculate center of the arrow
        int centerX = screenX + width / 2;
        int centerY = screenY + height / 2;
        
        // Create transform for rotation around center
        AffineTransform transform = new AffineTransform();
        transform.translate(centerX, centerY);
        transform.rotate(Math.toRadians(rotationAngle));
        transform.translate(-width / 2, -height / 2);
        
        // Apply the transform
        g2.setTransform(transform);
        
        // Enable alpha blending for proper transparency rendering
        // This ensures the per-pixel alpha from DisappearFX is respected
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentAlpha));
        
        // Draw the image with alpha composite applied
        g2.drawImage(imageToDraw, 0, 0, width, height, null);
        
        // Restore the original transform and composite
        g2.setTransform(originalTransform);
        g2.setComposite(originalComposite);
    }
    
    /**
     * Get the screen X position of the arrow.
     * @return Screen X position
     */
    public int getScreenX() {
        return screenX;
    }
    
    /**
     * Get the screen Y position of the arrow.
     * @return Screen Y position
     */
    public int getScreenY() {
        return screenY;
    }
    
    /**
     * Get the width of the arrow image.
     * @return Width in pixels
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Get the height of the arrow image.
     * @return Height in pixels
     */
    public int getHeight() {
        return height;
    }
}
