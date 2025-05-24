/**
* Interface for main application frames to implement
* This allows panels to work with different frame implementations
*/
public interface MainFrameInterface {
    /**
     * Sets the status message in the status bar
     * @param message The message to display
     */
    void setStatusMessage(String message);
    
    /**
     * Gets the primary accent color used in the UI
     * @return The primary accent color
     */
    java.awt.Color getPrimaryAccentColor();
    
    /**
     * Gets the secondary accent color used in the UI
     * @return The secondary accent color
     */
    java.awt.Color getSecondaryAccentColor();
    
    /**
     * Gets the background color used in the UI
     * @return The background color
     */
    java.awt.Color getBackgroundColor();
    
    /**
     * Gets the field background color used in the UI
     * @return The field background color
     */
    java.awt.Color getFieldBgColor();
    
    /**
     * Gets the text color used in the UI
     * @return The text color
     */
    java.awt.Color getTextColor();
    
    /**
     * Gets the sidebar color used in the UI
     * @return The sidebar color
     */
    java.awt.Color getSidebarColor();
 }
 
 