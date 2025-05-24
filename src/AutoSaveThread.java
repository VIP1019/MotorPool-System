/**
 * Background thread for auto-saving application data
 */
public class AutoSaveThread extends Thread {
    private static final int SAVE_INTERVAL = 60000; // 1 minute
    private boolean running = true;
    
    public AutoSaveThread() {
        setDaemon(true);
        setName("AutoSaveThread");
    }
    
    @Override
    public void run() {
        System.out.println("Auto-save thread started");
        while (running) {
            try {
                Thread.sleep(SAVE_INTERVAL);
                
                // Save data if changes have been made
                if (DataManager.getInstance().isDataChanged()) {
                    System.out.println("Auto-saving data...");
                    try {
                        DataManager.getInstance().saveAllData();
                        System.out.println("Auto-save completed successfully");
                    } catch (Exception e) {
                        System.err.println("Error during auto-save: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
                System.out.println("Auto-save thread interrupted");
            } catch (Exception e) {
                System.err.println("Unexpected error in auto-save thread: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Auto-save thread stopped");
    }
    
    public void stopRunning() {
        running = false;
        interrupt();
    }
}
