import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Solid collision objects using Rectangle2D.Double.
 * Player cannot pass through these objects.
 */
public class SolidObject {
    
    private int x;
    private int y;
    private int width;
    private int height;
    private Rectangle2D.Double bounds;
    
    public SolidObject(int xPos, int yPos, int w, int h) {
        x = xPos;
        y = yPos;
        width = w;
        height = h;
        bounds = new Rectangle2D.Double(x, y, width, height);
    }
    
    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        int drawX = x - offsetX;
        int drawY = y - offsetY;
        g2.fillRect(drawX, drawY, width, height);
    }
    
    public Rectangle2D.Double getBoundingRectangle() {
        return bounds;
    }
    
    public boolean intersects(Rectangle2D.Double rect) {
        return bounds.intersects(rect);
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}
