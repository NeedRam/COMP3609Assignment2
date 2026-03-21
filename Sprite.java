import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

/**
 * Base sprite class with position, size, and image support.
 */
public class Sprite {
    
    protected JPanel panel;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Image image;
    
    public Sprite(JPanel p, int xPos, int yPos, int w, int h) {
        panel = p;
        x = xPos;
        y = yPos;
        width = w;
        height = h;
        image = null;
    }
    
    public void setImage(Image img) {
        image = img;
    }
    
    public void draw(Graphics2D g2) {
        if (image != null) {
            g2.drawImage(image, x, y, width, height, null);
        }
    }
    
    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        if (image != null) {
            int drawX = x - offsetX;
            int drawY = y - offsetY;
            
            // Only draw if visible on screen
            if (drawX + width > 0 && drawX < panel.getWidth() &&
                drawY + height > 0 && drawY < panel.getHeight()) {
                g2.drawImage(image, drawX, drawY, width, height, null);
            }
        }
    }
    
    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double(x, y, width, height);
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
    
    public void setX(int newX) {
        x = newX;
    }
    
    public void setY(int newY) {
        y = newY;
    }
}
