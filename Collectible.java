import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

/**
 * Collectible game entities that can be picked up by the player.
 * Supports both static images and animated sprites.
 */
public class Collectible {
    
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean collected;
    private Image image;
    private int screenX;
    private int screenY;
    
    // Animated sprite for coin animation
    private AnimatedSprite animatedSprite;
    
    // Flag to determine if this collectible uses animation
    private boolean isAnimated;
    
    // Default constructor for static collectibles
    public Collectible(int xPos, int yPos, int w, int h) {
        x = xPos;
        y = yPos;
        width = w;
        height = h;
        collected = false;
        image = ImageManager.loadImage("collectible.png");
        isAnimated = false;
    }
    
    // Constructor for animated collectibles
    public Collectible(int xPos, int yPos, int w, int h, AnimatedSprite animSprite) {
        x = xPos;
        y = yPos;
        width = w;
        height = h;
        collected = false;
        image = null;
        animatedSprite = animSprite;
        isAnimated = true;
    }
    
    public void updateScreenPosition(int cameraX, int cameraY) {
        screenX = x - cameraX;
        screenY = y - cameraY;
        
        // Update animated sprite position if present
        if (animatedSprite != null) {
            animatedSprite.updateScreenPosition(cameraX, cameraY);
        }
    }
    
    // Update animation for animated collectibles
    public void update() {
        if (isAnimated && animatedSprite != null) {
            animatedSprite.update();
        }
    }
    
    public void draw(Graphics2D g2) {
        if (collected) return;
        
        // Only draw if visible on screen
        if (screenX + width > 0 && screenX < 800 &&
            screenY + height > 0 && screenY < 600) {
            
            if (isAnimated && animatedSprite != null) {
                // Draw animated sprite
                animatedSprite.draw(g2);
            } else if (image != null) {
                // Draw static image
                g2.drawImage(image, screenX, screenY, width, height, null);
            } else {
                // Draw placeholder
                g2.setColor(java.awt.Color.YELLOW);
                g2.fillOval(screenX, screenY, width, height);
            }
        }
    }
    
    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double(x, y, width, height);
    }
    
    public Rectangle2D.Double getScreenBoundingRectangle() {
        return new Rectangle2D.Double(screenX, screenY, width, height);
    }
    
    public boolean isCollected() {
        return collected;
    }
    
    public void collect() {
        collected = true;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getScreenX() {
        return screenX;
    }
    
    public int getScreenY() {
        return screenY;
    }
    
    public boolean isAnimated() {
        return isAnimated;
    }
}
