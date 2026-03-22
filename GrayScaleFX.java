import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Grayscale conversion effect with gradual fade.
 */
public class GrayScaleFX implements ImageFX {
    
    private int x;
    private int y;
    private int width;
    private int height;
    private BufferedImage originalImage;
    private BufferedImage grayImage;
    private boolean active;
    
    // Fade animation
    private boolean fadingIn;
    private float currentAlpha;
    private float fadeSpeed; // How fast to fade (units per frame)
    private static final float FADE_DURATION = 2.0f; // 2 secs
    private static final int TARGET_FPS = 60;
    
    public GrayScaleFX(int xPos, int yPos, int w, int h, String imagePath) {
        x = xPos;
        y = yPos;
        width = w;
        height = h;
        active = true;
        fadingIn = true;
        currentAlpha = 0.0f;
        
        // Calculate fade speed: full fade (0 to 1) over 2 seconds at 60 FPS
        // 2 seconds * 60 FPS = 120 frames
        fadeSpeed = 1.0f / (FADE_DURATION * TARGET_FPS);
        
        originalImage = ImageManager.loadBufferedImage(imagePath);
        if (originalImage != null) {
            grayImage = convertToGray(ImageManager.copyImage(originalImage));
        }
    }
    
    /**
     * Constructor for creating a full-screen grayscale effect using an existing image.
     */
    public GrayScaleFX(int xPos, int yPos, int w, int h, BufferedImage sourceImage) {
        x = xPos;
        y = yPos;
        width = w;
        height = h;
        active = true;
        fadingIn = true;
        currentAlpha = 0.0f;
        
        // Calculate fade speed: full fade (0 to 1) over 2 seconds at 60 FPS
        fadeSpeed = 1.0f / (FADE_DURATION * TARGET_FPS);
        
        if (sourceImage != null) {
            originalImage = sourceImage;
            grayImage = convertToGray(ImageManager.copyImage(sourceImage));
        }
    }
    
    /**
     * Constructor for instant grayscale effect (no fade animation).
     */
    public GrayScaleFX(int xPos, int yPos, int w, int h, BufferedImage sourceImage, BufferedImage grayscaleImage) {
        x = xPos;
        y = yPos;
        width = w;
        height = h;
        active = true;
        originalImage = sourceImage;
        grayImage = grayscaleImage;
        // Instant - start at full grayscale
        currentAlpha = 1.0f;
        fadingIn = false;
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
        
        // Gradually increase the grayscale effect
        if (fadingIn && currentAlpha < 1.0f) {
            currentAlpha += fadeSpeed;
            if (currentAlpha >= 1.0f) {
                currentAlpha = 1.0f;
                fadingIn = false;
            }
        }
    }
    
    @Override
    public void draw(Graphics2D g2) {
        if (!active) return;
        
        if (currentAlpha > 0 && originalImage != null && grayImage != null) {
            // Draw the original image first
            g2.drawImage(originalImage, x, y, width, height, null);
            
            // Draw the grayscale image with transparency overlay
            if (currentAlpha < 1.0f) {
                // Use alpha composite for smooth blending
                java.awt.AlphaComposite alphaComposite = 
                    java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, currentAlpha);
                g2.setComposite(alphaComposite);
                g2.drawImage(grayImage, x, y, width, height, null);
                // Reset composite
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
            } else {
                // Full grayscale
                g2.drawImage(grayImage, x, y, width, height, null);
            }
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
    
    /**
     * Check if the fade animation is complete.
     */
    public boolean isFadeComplete() {
        return currentAlpha >= 1.0f;
    }
    
    /**
     * Get the current alpha value (0.0 to 1.0).
     */
    public float getCurrentAlpha() {
        return currentAlpha;
    }
}
