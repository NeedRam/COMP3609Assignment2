import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Fade/disappear effect.
 */
public class DisappearFX implements ImageFX {
    
    private int x;
    private int y;
    private int width;
    private int height;
    private BufferedImage originalImage;
    private BufferedImage currentImage;
    private int alpha;
    private int alphaChange;
    private boolean active;
    
    public DisappearFX(int xPos, int yPos, int w, int h, String imagePath) {
        x = xPos;
        y = yPos;
        width = w;
        height = h;
        alpha = 255;
        alphaChange = 5;
        active = true;
        
        originalImage = ImageManager.loadBufferedImage(imagePath);
        if (originalImage != null) {
            currentImage = ImageManager.copyImage(originalImage);
        }
    }
    
    @Override
    public void update() {
        if (!active) return;
        
        alpha -= alphaChange;
        if (alpha <= 0) {
            alpha = 255;  // Reset for continuous effect
        }
        
        // Apply alpha to image
        if (currentImage != null && originalImage != null) {
            int imWidth = originalImage.getWidth();
            int imHeight = originalImage.getHeight();
            
            int[] pixels = new int[imWidth * imHeight];
            originalImage.getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);
            
            for (int i = 0; i < pixels.length; i++) {
                int a = (pixels[i] >> 24);
                int red = (pixels[i] >> 16) & 255;
                int green = (pixels[i] >> 8) & 255;
                int blue = pixels[i] & 255;
                
                int newValue = blue | (green << 8) | (red << 16) | (alpha << 24);
                pixels[i] = newValue;
            }
            
            currentImage.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);
        }
    }
    
    @Override
    public void draw(Graphics2D g2) {
        if (!active || currentImage == null) return;
        g2.drawImage(currentImage, x, y, width, height, null);
    }
    
    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double(x, y, width, height);
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean a) {
        active = a;
    }
    
    /**
     * Set the position for drawing.
     * 
     * @param xPos New X position
     * @param yPos New Y position
     */
    public void setPosition(int xPos, int yPos) {
        x = xPos;
        y = yPos;
    }
    
    /**
     * Get the current image with alpha applied.
     * 
     * @return The BufferedImage with alpha applied
     */
    public BufferedImage getCurrentImage() {
        return currentImage;
    }
    
    /**
     * Set a specific alpha value directly without auto-decrementing.
     * This applies the alpha to the image immediately.
     * 
     * @param alphaValue The alpha value (0-255)
     */
    public void setAlpha(int alphaValue) {
        alpha = Math.max(0, Math.min(255, alphaValue));
        
        // Apply alpha to image
        if (currentImage != null && originalImage != null) {
            int imWidth = originalImage.getWidth();
            int imHeight = originalImage.getHeight();
            
            int[] pixels = new int[imWidth * imHeight];
            originalImage.getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);
            
            for (int i = 0; i < pixels.length; i++) {
                int a = (pixels[i] >> 24);
                int red = (pixels[i] >> 16) & 255;
                int green = (pixels[i] >> 8) & 255;
                int blue = pixels[i] & 255;
                
                // Only modify if pixel is not fully transparent
                if (a > 0) {
                    int newValue = blue | (green << 8) | (red << 16) | (alpha << 24);
                    pixels[i] = newValue;
                }
            }
            
            currentImage.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);
        }
    }
}
