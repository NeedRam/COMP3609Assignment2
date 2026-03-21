import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.Image;

/**
 * Collectible game entities that can be picked up by the player.
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
    
    public Collectible(int xPos, int yPos, int w, int h) {
        x = xPos;
        y = yPos;
        width = w;
        height = h;
        collected = false;
        image = ImageManager.loadImage("collectible.png");
    }
    
    public void updateScreenPosition(int cameraX, int cameraY) {
        screenX = x - cameraX;
        screenY = y - cameraY;
    }
    
    public void draw(Graphics2D g2) {
        if (collected) return;
        
        // Only draw if visible on screen
        if (screenX + width > 0 && screenX < 800 &&
            screenY + height > 0 && screenY < 600) {
            if (image != null) {
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
}
