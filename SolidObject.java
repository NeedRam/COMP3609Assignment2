import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Solid collision objects using Rectangle2D.Double.
 * Player cannot pass through these objects.
 * Supports both rectangular and image-based objects.
 */
public class SolidObject {
    
    private int x;
    private int y;
    private int width;
    private int height;
    private Rectangle2D.Double bounds;
    private BufferedImage image;
    
    /**
     * Constructor for rectangular solid objects (legacy support).
     */
    public SolidObject(int xPos, int yPos, int w, int h) {
        x = xPos;
        y = yPos;
        width = w;
        height = h;
        bounds = new Rectangle2D.Double(x, y, width, height);
        image = null;
    }
    
    /**
     * Constructor for image-based solid objects.
     * Width and height are derived from the image dimensions.
     */
    public SolidObject(double xPos, double yPos, BufferedImage img) {
        x = (int) xPos;
        y = (int) yPos;
        image = img;
        
        if (image != null) {
            width = image.getWidth();
            height = image.getHeight();
        } else {
            width = 0;
            height = 0;
        }
        
        bounds = new Rectangle2D.Double(x, y, width, height);
    }
    
    /**
     * Draws the solid object. If an image is set, draws the image;
     * otherwise, draws a rectangle (legacy behavior).
     */
    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        int drawX = x - offsetX;
        int drawY = y - offsetY;
        
        if (image != null) {
            g2.drawImage(image, drawX, drawY, null);
        } else {
            // Legacy behavior: draw a gray rectangle
            g2.fillRect(drawX, drawY, width, height);
        }
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
    
    /**
     * Returns the image associated with this solid object, if any.
     */
    public BufferedImage getImage() {
        return image;
    }
}
