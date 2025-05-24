import java.io.File;
import javax.swing.*;

/**
 * Main application class for the Campus Motor Pool Management System
 */
public class MotorPoolApp {
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create data directories if they don't exist
        createDataDirectories();
        
        // Load application data
        DataManager.getInstance().loadAllData();
        
        // Start the auto-save thread
        AutoSaveThread autoSaveThread = new AutoSaveThread();
        autoSaveThread.start();
        
        // Show login screen
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
    
    private static void createDataDirectories() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
        
        // Create subdirectories for different data types
        new File("data/users").mkdir();
        new File("data/vehicles").mkdir();
        new File("data/reservations").mkdir();
    }
}

