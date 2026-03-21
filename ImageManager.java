import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Image loading and processing with BufferedImage support.
 */
public class ImageManager {
    
    public ImageManager() {
    }
    
    public static Image loadImage(String fileName) {
        return new ImageIcon(fileName).getImage();
    }
    
    public static BufferedImage loadBufferedImage(String filename) {
        BufferedImage bi = null;
        
        File file = new File(filename);
        try {
            bi = ImageIO.read(file);
        } catch (IOException ioe) {
            System.out.println("Error opening file " + filename + ": " + ioe);
        }
        return bi;
    }
    
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        
        BufferedImage bufferedImage = new BufferedImage(
            image.getWidth(null),
            image.getHeight(null),
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        
        return bufferedImage;
    }
    
    public static BufferedImage copyImage(BufferedImage src) {
        if (src == null)
            return null;
        
        int imWidth = src.getWidth();
        int imHeight = src.getHeight();
        
        BufferedImage copy = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(src, 0, 0, null);
        g2d.dispose();
        
        return copy;
    }
    
    /**
     * Check if a pixel is transparent (alpha = 0).
     */
    private static boolean isPixelTransparent(BufferedImage img, int x, int y) {
        if (x < 0 || x >= img.getWidth() || y < 0 || y >= img.getHeight()) {
            return true;
        }
        int pixel = img.getRGB(x, y);
        int alpha = (pixel >> 24) & 0xFF;
        return alpha == 0;
    }
    
    /**
     * Check if a column contains any non-transparent pixels.
     */
    private static boolean columnHasContent(BufferedImage img, int col) {
        int height = img.getHeight();
        for (int y = 0; y < height; y++) {
            if (!isPixelTransparent(img, col, y)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if a row contains any non-transparent pixels.
     */
    private static boolean rowHasContent(BufferedImage img, int row) {
        int width = img.getWidth();
        for (int x = 0; x < width; x++) {
            if (!isPixelTransparent(img, x, row)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Detect frame boundaries in a horizontal sprite strip.
     * Scans columns left-to-right and detects transitions between transparent and non-transparent pixels.
     * 
     * @param strip The sprite strip image (frames arranged horizontally)
     * @return int[] array of x-coordinates where each frame starts
     */
    public static int[] detectFrameBoundaries(BufferedImage strip) {
        if (strip == null) {
            return new int[0];
        }
        
        List<Integer> boundaries = new ArrayList<>();
        int width = strip.getWidth();
        
        boolean inFrame = false;
        for (int x = 0; x < width; x++) {
            boolean hasContent = columnHasContent(strip, x);
            
            if (hasContent && !inFrame) {
                // Start of a new frame
                boundaries.add(x);
                inFrame = true;
            } else if (!hasContent && inFrame) {
                // End of current frame
                inFrame = false;
            }
        }
        
        return boundaries.stream().mapToInt(Integer::intValue).toArray();
    }
    
    /**
     * Detect frame boundaries in a vertical sprite strip.
     * Scans rows top-to-bottom and detects transitions between transparent and non-transparent pixels.
     * 
     * @param strip The sprite strip image (frames arranged vertically)
     * @return int[] array of y-coordinates where each frame starts
     */
    public static int[] detectFrameBoundariesVertical(BufferedImage strip) {
        if (strip == null) {
            return new int[0];
        }
        
        List<Integer> boundaries = new ArrayList<>();
        int height = strip.getHeight();
        
        boolean inFrame = false;
        for (int y = 0; y < height; y++) {
            boolean hasContent = rowHasContent(strip, y);
            
            if (hasContent && !inFrame) {
                // Start of a new frame
                boundaries.add(y);
                inFrame = true;
            } else if (!hasContent && inFrame) {
                // End of current frame
                inFrame = false;
            }
        }
        
        return boundaries.stream().mapToInt(Integer::intValue).toArray();
    }
    
    /**
     * Extract individual frames from a horizontal sprite strip using auto-detection.
     * 
     * @param strip The sprite strip image (frames arranged horizontally)
     * @return BufferedImage[] array of individual frames
     */
    public static BufferedImage[] extractFramesFromStrip(BufferedImage strip) {
        if (strip == null) {
            return new BufferedImage[0];
        }
        
        int[] boundaries = detectFrameBoundaries(strip);
        
        if (boundaries.length == 0) {
            return new BufferedImage[0];
        }
        
        List<BufferedImage> frames = new ArrayList<>();
        int stripWidth = strip.getWidth();
        int stripHeight = strip.getHeight();
        
        for (int i = 0; i < boundaries.length; i++) {
            int startX = boundaries[i];
            int endX;
            
            if (i < boundaries.length - 1) {
                // Use the start of next frame as end
                endX = boundaries[i + 1];
            } else {
                // Last frame extends to the end of strip
                endX = stripWidth;
            }
            
            int frameWidth = endX - startX;
            
            // Create frame image
            BufferedImage frame = new BufferedImage(frameWidth, stripHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = frame.createGraphics();
            g2d.drawImage(strip, 0, 0, frameWidth, stripHeight, startX, 0, endX, stripHeight, null);
            g2d.dispose();
            
            frames.add(frame);
        }
        
        return frames.toArray(new BufferedImage[0]);
    }
    
    /**
     * Extract individual frames from a vertical sprite strip using auto-detection.
     * 
     * @param strip The sprite strip image (frames arranged vertically)
     * @return BufferedImage[] array of individual frames
     */
    public static BufferedImage[] extractFramesFromStripVertical(BufferedImage strip) {
        if (strip == null) {
            return new BufferedImage[0];
        }
        
        int[] boundaries = detectFrameBoundariesVertical(strip);
        
        if (boundaries.length == 0) {
            return new BufferedImage[0];
        }
        
        List<BufferedImage> frames = new ArrayList<>();
        int stripWidth = strip.getWidth();
        int stripHeight = strip.getHeight();
        
        for (int i = 0; i < boundaries.length; i++) {
            int startY = boundaries[i];
            int endY;
            
            if (i < boundaries.length - 1) {
                // Use the start of next frame as end
                endY = boundaries[i + 1];
            } else {
                // Last frame extends to the end of strip
                endY = stripHeight;
            }
            
            int frameHeight = endY - startY;
            
            // Create frame image
            BufferedImage frame = new BufferedImage(stripWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = frame.createGraphics();
            g2d.drawImage(strip, 0, 0, stripWidth, frameHeight, 0, startY, stripWidth, endY, null);
            g2d.dispose();
            
            frames.add(frame);
        }
        
        return frames.toArray(new BufferedImage[0]);
    }
}
