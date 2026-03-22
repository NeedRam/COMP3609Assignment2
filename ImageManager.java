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
     * Apply grayscale conversion to a BufferedImage.
     * 
     * @param src The source image to convert
     * @return A new grayscale BufferedImage, or null if src is null
     */
    public static BufferedImage applyGrayscale(BufferedImage src) {
        if (src == null) return null;
        
        int width = src.getWidth();
        int height = src.getHeight();
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = new int[width * height];
        src.getRGB(0, 0, width, height, pixels, 0, width);
        
        for (int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] >> 24) & 255;
            int red = (pixels[i] >> 16) & 255;
            int green = (pixels[i] >> 8) & 255;
            int blue = pixels[i] & 255;
            
            // Standard grayscale conversion
            int gray = (int)(0.299 * red + 0.587 * green + 0.114 * blue);
            pixels[i] = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
        }
        
        result.setRGB(0, 0, width, height, pixels, 0, width);
        return result;
    }
    
    /**
     * Apply a tint color to a BufferedImage.
     * 
     * @param src The source image to tint
     * @param tintRGB The RGB tint color
     * @return A new tinted BufferedImage, or null if src is null
     */
    public static BufferedImage applyTint(BufferedImage src, int tintRGB) {
        if (src == null) return null;
        
        int width = src.getWidth();
        int height = src.getHeight();
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = new int[width * height];
        src.getRGB(0, 0, width, height, pixels, 0, width);
        
        int tintRed = (tintRGB >> 16) & 255;
        int tintGreen = (tintRGB >> 8) & 255;
        int tintBlue = tintRGB & 255;
        
        for (int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] >> 24) & 255;
            int red = (pixels[i] >> 16) & 255;
            int green = (pixels[i] >> 8) & 255;
            int blue = pixels[i] & 255;
            
            // Blend with tint color
            red = (red + tintRed) / 2;
            green = (green + tintGreen) / 2;
            blue = (blue + tintBlue) / 2;
            
            pixels[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
        }
        
        result.setRGB(0, 0, width, height, pixels, 0, width);
        return result;
    }
    
    /**
     * Apply alpha transparency to a BufferedImage.
     * 
     * @param src The source image
     * @param alphaValue The alpha value (0-255)
     * @return A new BufferedImage with alpha applied, or null if src is null
     */
    public static BufferedImage applyAlpha(BufferedImage src, int alphaValue) {
        if (src == null) return null;
        
        int width = src.getWidth();
        int height = src.getHeight();
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = new int[width * height];
        src.getRGB(0, 0, width, height, pixels, 0, width);
        
        for (int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] >> 24) & 255;
            int red = (pixels[i] >> 16) & 255;
            int green = (pixels[i] >> 8) & 255;
            int blue = pixels[i] & 255;
            
            // Only modify if pixel is not fully transparent
            if (alpha > 0) {
                pixels[i] = blue | (green << 8) | (red << 16) | (alphaValue << 24);
            }
        }
        
        result.setRGB(0, 0, width, height, pixels, 0, width);
        return result;
    }
    
    /**
     * Scale a BufferedImage to the specified width and height.
     * 
     * @param src The source image to scale
     * @param newWidth The desired width
     * @param newHeight The desired height
     * @return The scaled BufferedImage, or null if src is null
     */
    public static BufferedImage scaleImage(BufferedImage src, int newWidth, int newHeight) {
        if (src == null) return null;
        
        BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.drawImage(src, 0, 0, newWidth, newHeight, null);
        g2.dispose();
        
        return scaled;
    }
    
    /**
     * Scale a BufferedImage to fit within the specified max dimensions while maintaining aspect ratio.
     * 
     * @param src The source image to scale
     * @param maxWidth The maximum width
     * @param maxHeight The maximum height
     * @return The scaled BufferedImage, or null if src is null
     */
    public static BufferedImage scaleImageToFit(BufferedImage src, int maxWidth, int maxHeight) {
        if (src == null) return null;
        
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        
        // Calculate scaling ratio to fit within max dimensions
        double widthRatio = (double) maxWidth / srcWidth;
        double heightRatio = (double) maxHeight / srcHeight;
        double ratio = Math.min(widthRatio, heightRatio);
        
        int newWidth = (int) (srcWidth * ratio);
        int newHeight = (int) (srcHeight * ratio);
        
        return scaleImage(src, newWidth, newHeight);
    }
    
    /**
     * Scale a BufferedImage to a specific height while maintaining aspect ratio.
     * 
     * @param src The source image to scale
     * @param targetHeight The desired height
     * @return The scaled BufferedImage, or null if src is null
     */
    public static BufferedImage scaleImageToHeight(BufferedImage src, int targetHeight) {
        if (src == null) return null;
        
        int newWidth = (int) (src.getWidth() * ((double) targetHeight / src.getHeight()));
        return scaleImage(src, newWidth, targetHeight);
    }
    
    /**
     * Scale a BufferedImage to a specific width while maintaining aspect ratio.
     * 
     * @param src The source image to scale
     * @param targetWidth The desired width
     * @return The scaled BufferedImage, or null if src is null
     */
    public static BufferedImage scaleImageToWidth(BufferedImage src, int targetWidth) {
        if (src == null) return null;
        
        int newHeight = (int) (src.getHeight() * ((double) targetWidth / src.getWidth()));
        return scaleImage(src, targetWidth, newHeight);
    }
    
    /**
     * Load and scale tree images to max height.
     * 
     * @param targetHeight The target height for all tree images
     * @return Array of scaled tree BufferedImages
     */
    public static BufferedImage[] loadTreeImages(int targetHeight) {
        BufferedImage[] trees = new BufferedImage[6];
        BufferedImage[] originals = new BufferedImage[6];
        originals[0] = loadBufferedImage("images/trees/Tree1.png");
        originals[1] = loadBufferedImage("images/trees/Tree2.png");
        originals[2] = loadBufferedImage("images/trees/Tree3.png");
        originals[3] = loadBufferedImage("images/trees/Tree4.png");
        originals[4] = loadBufferedImage("images/trees/Tree5.png");
        originals[5] = loadBufferedImage("images/trees/Tree6.png");
        
        for (int i = 0; i < originals.length; i++) {
            if (originals[i] != null) {
                trees[i] = scaleImageToHeight(originals[i], targetHeight);
            }
        }
        
        return trees;
    }
    
    /**
     * Load and scale rock images to max width.
     * 
     * @param targetWidth The target width for all rock images
     * @return Array of scaled rock BufferedImages
     */
    public static BufferedImage[] loadRockImages(int targetWidth) {
        BufferedImage[] rocks = new BufferedImage[9];
        BufferedImage[] originals = new BufferedImage[9];
        originals[0] = loadBufferedImage("images/rocks/Rock1.png");
        originals[1] = loadBufferedImage("images/rocks/Rock2.png");
        originals[2] = loadBufferedImage("images/rocks/Rock3.png");
        originals[3] = loadBufferedImage("images/rocks/Rock4.png");
        originals[4] = loadBufferedImage("images/rocks/Rock5.png");
        originals[5] = loadBufferedImage("images/rocks/Rock6.png");
        originals[6] = loadBufferedImage("images/rocks/Rock7.png");
        originals[7] = loadBufferedImage("images/rocks/Rock8.png");
        originals[8] = loadBufferedImage("images/rocks/Rock9.png");
        
        for (int i = 0; i < originals.length; i++) {
            if (originals[i] != null) {
                rocks[i] = scaleImageToWidth(originals[i], targetWidth);
            }
        }
        
        return rocks;
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
