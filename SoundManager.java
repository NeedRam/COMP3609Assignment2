import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.HashMap;

/**
 * Sound clip management with singleton pattern.
 */
public class SoundManager {
    
    private static SoundManager instance = null;
    public HashMap<String, Clip> clips;
    
    private SoundManager() {
        clips = new HashMap<String, Clip>();
        
        // Load sound clips
        loadClip("footstep", "runningOnGrass.wav");
        loadClip("coinPickup", "coinPickp.wav");
    }
    
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    private void loadClip(String name, String filename) {
        try {
            File file = new File(filename);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL());
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clips.put(name, clip);
        } catch (Exception e) {
            System.out.println("Error loading sound: " + filename + " - " + e);
        }
    }
    
    public void playClip(String name, boolean looping) {
        Clip clip = clips.get(name);
        if (clip != null) {
            clip.setFramePosition(0);
            if (looping) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        }
    }
    
    public void stopClip(String name) {
        Clip clip = clips.get(name);
        if (clip != null) {
            clip.stop();
        }
    }
    
    public void stopAll() {
        for (Clip clip : clips.values()) {
            clip.stop();
        }
    }
    
    public boolean isPlaying(String name) {
        Clip clip = clips.get(name);
        return clip != null && clip.isRunning();
    }
    
    /**
     * Start the footstep sound (looping).
     */
    public void startFootstep() {
        if (!isPlaying("footstep")) {
            playClip("footstep", true);
        }
    }
    
    /**
     * Stop the footstep sound.
     */
    public void stopFootstep() {
        stopClip("footstep");
    }
}
