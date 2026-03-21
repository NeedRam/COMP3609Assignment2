import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Grayscale conversion effect.
 */
public class GrayScaleFX implements ImageFX {
    
    private int x;
    private int y;
    private int width;
    private int height;
    private BufferedImage originalImage;
    private BufferedImage grayImage;
    private boolean showGray;
    private int time;
    private int timeChange;
    private boolean active;
    
    public GrayScaleFX(int xPos, int yPos, int w, int h, String imagePath) {
        x = xPos;
        y = yPos;
        width = w;
        height = h;
        showGray = false;
        time = 0;
        timeChange = 1;
        active = true;
        
        originalImage = ImageManager.loadBufferedImage(imagePath);
        if (originalImage != null) {
            grayImage = convertToGray(ImageManager.copyImage(originalImage));
        }
    }
    
    private BufferedImage convertToGray(BufferedImage src) {
        if (src == null) return null;
        
        int imWidth = src.getWidth();
        int imHeight = src.getHeight();
        
        int[] pixels = new int[imWidth * imHeight];
        src.getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);
        
        for (int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] >> 24) & 255;
            int red = (pixels[i] >> 16) & 255;
            int green = (pixels[i] >> 8) & 255;
            int blue = pixels[i] & 255;
            
            // Calculate grayscale value
            int gray = (int)(0.299 * red + 0.587 * green + 0.114 * blue);
            
            pixels[i] = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
        }
        
        src.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);
        return src;
    }
    
    @Override
    public void update() {
        if (!active) return;
        
        time += timeChange;
        
        if (time < 20) {
            showGray = false;
        } else if (time < 40) {
            showGray = true;
        } else {
            time = 0;
        }
    }
    
    @Override
    public void draw(Graphics2D g2) {
        if (!active) return;
        
        if (showGray && grayImage != null) {
            g2.drawImage(grayImage, x, y, width, height, null);
        } else if (originalImage != null) {
            g2.drawImage(originalImage, x, y, width, height, null);
        }
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
}
