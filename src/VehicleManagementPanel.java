import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import model.Reservation;
import model.Vehicle;

/**
* Enhanced panel for managing vehicles with modern card-based UI
*/
public class VehicleManagementPanel extends JPanel {
    private MainFrameInterface parentFrame;
    private JPanel vehiclesContainer;
    private JScrollPane scrollPane;
    private JTextField searchField;
    private JComboBox<String> filterTypeComboBox;
    private JButton addButton;
    private JButton refreshButton;
    
    // Vehicle cards currently displayed
    private List<Vehicle> currentVehicles;
    
    // Vehicle type icons
    private ImageIcon sedanIcon;
    private ImageIcon suvIcon;
    private ImageIcon vanIcon;
    private ImageIcon busIcon;
    private ImageIcon truckIcon;
    
    // Add a method to ensure the images directory exists
    private void ensureImagesDirectoryExists() {
        java.io.File imagesDir = new java.io.File("images");
        if (!imagesDir.exists()) {
            boolean created = imagesDir.mkdir();
            if (created) {
                System.out.println("Created images directory");
            } else {
                System.err.println("Failed to create images directory");
            }
        }
    }

    // Modify the constructor to ensure images directory exists
    public VehicleManagementPanel(MainFrameInterface parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(parentFrame.getBackgroundColor());
        
        ensureImagesDirectoryExists();
        loadIcons();
        initComponents();
        loadVehicles();
    }
    
    // Modify the loadIcons method to ensure it loads fresh images
    private void loadIcons() {
        // Load vehicle type icons - using placeholder colors for now
        // In a real application, these would be actual image files
        sedanIcon = createVehicleIcon(Color.BLUE, "SEDAN");
        suvIcon = createVehicleIcon(new Color(0, 150, 0), "SUV");
        vanIcon = createVehicleIcon(new Color(150, 0, 150), "VAN");
        busIcon = createVehicleIcon(new Color(200, 100, 0), "BUS");
        truckIcon = createVehicleIcon(new Color(150, 0, 0), "TRUCK");
    }
    
    // Now update the createVehicleIcon method to check for vehicle-specific images first

    // Replace the existing createVehicleIcon method with this improved version:
    // Modify the createVehicleIcon method to avoid caching and always load fresh images
    private ImageIcon createVehicleIcon(Color color, String type) {
        // First try to load an actual image file for this specific vehicle
        if (type != null && !type.isEmpty()) {
            // Try to load the image from file
            try {
                // Check if we have a vehicle ID to use for the image
                if (type.startsWith("V-")) {
                    java.io.File imageFile = new java.io.File("images/" + type + ".jpg");
                    if (imageFile.exists()) {
                        // Use ImageIO to read the image fresh from disk
                        Image image = javax.imageio.ImageIO.read(imageFile);
                        // Scale the image to appropriate size - landscape orientation
                        Image scaledImage = image.getScaledInstance(320, 180, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);
                    }
                }
            
                // Fall back to vehicle type image if specific vehicle image not found
                java.io.File imageFile = new java.io.File("images/" + type.toLowerCase() + ".jpg");
                if (imageFile.exists()) {
                    // Use ImageIO to read the image fresh from disk
                    Image image = javax.imageio.ImageIO.read(imageFile);
                    // Scale the image to appropriate size - landscape orientation
                    Image scaledImage = image.getScaledInstance(320, 180, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImage);
                }
            } catch (Exception e) {
                System.err.println("Error loading vehicle image: " + e.getMessage());
                e.printStackTrace(); // Add stack trace for better debugging
            }
        }

        // Fallback to colored placeholder with improved design
        int width = 320;
        int height = 180;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Create metallic gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, color.darker().darker(), 
            width, height, color
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, width, height, 15, 15);
        
        // Add highlight effect
        GradientPaint highlightGradient = new GradientPaint(
            0, 0, new Color(255, 255, 255, 100),
            0, height/3, new Color(255, 255, 255, 0)
        );
        g2d.setPaint(highlightGradient);
        g2d.fillRoundRect(3, 3, width-6, height/3, 15, 15);
        
        // Draw vehicle silhouette based on type with improved styling
        g2d.setColor(new Color(255, 255, 255, 180));
        int centerX = width / 2;
        int centerY = height / 2;
        
        if (type.equals("SEDAN")) {
            // Draw sedan silhouette with more detail
            g2d.fillRoundRect(centerX - 80, centerY - 15, 160, 30, 20, 20);
            g2d.fillRoundRect(centerX - 60, centerY - 35, 120, 25, 15, 15);
            
            // Windows
            g2d.setColor(new Color(100, 200, 255, 150));
            g2d.fillRoundRect(centerX - 55, centerY - 32, 30, 20, 5, 5);
            g2d.fillRoundRect(centerX - 20, centerY - 32, 30, 20, 5, 5);
            g2d.fillRoundRect(centerX + 15, centerY - 32, 30, 20, 5, 5);
            
            // Wheels with shadow and highlights
            g2d.setColor(new Color(20, 20, 20));
            g2d.fillOval(centerX - 60, centerY + 10, 25, 25);
            g2d.fillOval(centerX + 35, centerY + 10, 25, 25);
            
            // Wheel highlights
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.fillOval(centerX - 55, centerY + 15, 8, 8);
            g2d.fillOval(centerX + 40, centerY + 15, 8, 8);
            
            // Headlights
            g2d.setColor(new Color(255, 255, 200, 200));
            g2d.fillOval(centerX - 75, centerY - 5, 10, 10);
            g2d.fillOval(centerX + 65, centerY - 5, 10, 10);
            
        } else if (type.equals("SUV")) {
            // Draw SUV silhouette with more detail
            g2d.fillRoundRect(centerX - 80, centerY - 15, 160, 40, 20, 20);
            g2d.fillRoundRect(centerX - 70, centerY - 40, 140, 30, 10, 10);
            
            // Windows
            g2d.setColor(new Color(100, 200, 255, 150));
            g2d.fillRoundRect(centerX - 65, centerY - 37, 35, 25, 5, 5);
            g2d.fillRoundRect(centerX - 25, centerY - 37, 35, 25, 5, 5);
            g2d.fillRoundRect(centerX + 15, centerY - 37, 35, 25, 5, 5);
            
            // Wheels with shadow and highlights
            g2d.setColor(new Color(20, 20, 20));
            g2d.fillOval(centerX - 60, centerY + 15, 30, 30);
            g2d.fillOval(centerX + 30, centerY + 15, 30, 30);
            
            // Wheel highlights
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.fillOval(centerX - 55, centerY + 20, 10, 10);
            g2d.fillOval(centerX + 35, centerY + 20, 10, 10);
            
            // Headlights
            g2d.setColor(new Color(255, 255, 200, 200));
            g2d.fillOval(centerX - 75, centerY, 12, 12);
            g2d.fillOval(centerX + 63, centerY, 12, 12);
            
        } else if (type.equals("VAN")) {
            // Draw van silhouette with more detail
            g2d.fillRoundRect(centerX - 85, centerY - 40, 170, 70, 20, 20);
            
            // Windows
            g2d.setColor(new Color(100, 200, 255, 150));
            g2d.fillRoundRect(centerX - 75, centerY - 35, 40, 30, 5, 5);
            g2d.fillRoundRect(centerX - 30, centerY - 35, 30, 30, 5, 5);
            g2d.fillRoundRect(centerX + 5, centerY - 35, 30, 30, 5, 5);
            g2d.fillRoundRect(centerX + 40, centerY - 35, 30, 30, 5, 5);
            
            // Wheels with shadow and highlights
            g2d.setColor(new Color(20, 20, 20));
            g2d.fillOval(centerX - 60, centerY + 20, 30, 30);
            g2d.fillOval(centerX + 30, centerY + 20, 30, 30);
            
            // Wheel highlights
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.fillOval(centerX - 55, centerY + 25, 10, 10);
            g2d.fillOval(centerX + 35, centerY + 25, 10, 10);
            
            // Headlights
            g2d.setColor(new Color(255, 255, 200, 200));
            g2d.fillOval(centerX - 80, centerY, 12, 12);
            g2d.fillOval(centerX + 68, centerY, 12, 12);
            
        } else if (type.equals("BUS")) {
            // Draw bus silhouette with more detail
            g2d.fillRoundRect(centerX - 100, centerY - 40, 200, 70, 15, 15);
            
            // Windows
            g2d.setColor(new Color(100, 200, 255, 150));
            for (int i = 0; i < 6; i++) {
                g2d.fillRoundRect(centerX - 90 + (i * 30), centerY - 35, 25, 20, 5, 5);
            }
            
            // Wheels with shadow and highlights
            g2d.setColor(new Color(20, 20, 20));
            g2d.fillOval(centerX - 70, centerY + 20, 30, 30);
            g2d.fillOval(centerX + 40, centerY + 20, 30, 30);
            
            // Wheel highlights
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.fillOval(centerX - 65, centerY + 25, 10, 10);
            g2d.fillOval(centerX + 45, centerY + 25, 10, 10);
            
            // Headlights
            g2d.setColor(new Color(255, 255, 200, 200));
            g2d.fillOval(centerX - 95, centerY + 5, 12, 12);
            g2d.fillOval(centerX + 83, centerY + 5, 12, 12);
            
        } else if (type.equals("TRUCK")) {
            // Draw truck silhouette with more detail
            g2d.fillRoundRect(centerX - 40, centerY - 35, 80, 40, 10, 10);
            g2d.fillRect(centerX - 90, centerY - 15, 180, 40);
            
            // Windows
            g2d.setColor(new Color(100, 200, 255, 150));
            g2d.fillRoundRect(centerX - 35, centerY - 32, 70, 25, 5, 5);
            
            // Wheels with shadow and highlights
            g2d.setColor(new Color(20, 20, 20));
            g2d.fillOval(centerX - 60, centerY + 15, 30, 30);
            g2d.fillOval(centerX + 30, centerY + 15, 30, 30);
            
            // Wheel highlights
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.fillOval(centerX - 55, centerY + 20, 10, 10);
            g2d.fillOval(centerX + 35, centerY + 20, 10, 10);
            
            // Headlights
            g2d.setColor(new Color(255, 255, 200, 200));
            g2d.fillOval(centerX - 85, centerY, 12, 12);
            g2d.fillOval(centerX + 73, centerY, 12, 12);
        }
        
        // Draw type text with improved styling
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(type);
        
        // Draw text shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(type, (width - textWidth) / 2 + 2, height - 20 + 2);
        
        // Draw text
        g2d.setColor(Color.WHITE);
        g2d.drawString(type, (width - textWidth) / 2, height - 20);
        
        // Add subtle border
        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(0, 0, width-1, height-1, 15, 15);
        
        g2d.dispose();
        return new ImageIcon(image);
    }
    
    private void initComponents() {
        // Create header panel with search and filters
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(parentFrame.getBackgroundColor());
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Title label with modern styling
        JLabel titleLabel = new JLabel("Vehicle Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        // Search and filter panel
        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchFilterPanel.setOpaque(false);
        
        // Search field with modern styling and rounded corners
        searchField = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded background
                g2d.setColor(parentFrame.getFieldBgColor());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                super.paintComponent(g);
            }
        };
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.WHITE);
        searchField.setOpaque(false);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(1, 1, 1, 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        searchField.setCaretColor(Color.WHITE);
        
        // Add search icon and placeholder
        searchField.setText("Search vehicles...");
        searchField.setForeground(new Color(150, 150, 150));
        
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search vehicles...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.WHITE);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search vehicles...");
                    searchField.setForeground(new Color(150, 150, 150));
                }
            }
        });
        
        // Search button with improved styling
        JButton searchButton = new JButton("Search") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 50, 200), 
                    0, getHeight(), new Color(255, 20, 150)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Add highlight at the top
                g2d.setPaint(new Color(255, 255, 255, 70));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() / 3, 15, 15);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                g2d.drawString(getText(), (getWidth() - textWidth) / 2,
                              (getHeight() - textHeight) / 2 + fm.getAscent());
            }
        };
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setContentAreaFilled(false);
        searchButton.setPreferredSize(new Dimension(100, 40));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> searchVehicles());
        
        // Filter by type dropdown with improved styling
        JLabel filterLabel = new JLabel("Filter by Type:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        filterLabel.setForeground(Color.WHITE);
        
        String[] vehicleTypes = {"All Types", "SEDAN", "SUV", "VAN", "BUS", "TRUCK"};
        filterTypeComboBox = new JComboBox<>(vehicleTypes);
        filterTypeComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        filterTypeComboBox.setForeground(Color.WHITE);
        filterTypeComboBox.setBackground(parentFrame.getFieldBgColor());
        filterTypeComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        filterTypeComboBox.setPreferredSize(new Dimension(150, 40));
        filterTypeComboBox.addActionListener(e -> filterVehicles());
        
        // Add components to search panel
        searchFilterPanel.add(searchField);
        searchFilterPanel.add(searchButton);
        searchFilterPanel.add(Box.createHorizontalStrut(20));
        searchFilterPanel.add(filterLabel);
        searchFilterPanel.add(filterTypeComboBox);
        
        // Add title and search panel to header
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchFilterPanel, BorderLayout.EAST);
        
        // Create vehicles container with grid layout and improved spacing
        vehiclesContainer = new JPanel();
        vehiclesContainer.setLayout(new WrapLayout(FlowLayout.LEFT, 25, 25));
        vehiclesContainer.setBackground(parentFrame.getBackgroundColor());
        vehiclesContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add scroll pane for vehicles container with improved styling
        scrollPane = new JScrollPane(vehiclesContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(parentFrame.getBackgroundColor());
        
        // Customize scrollbar
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 100, 150);
                this.trackColor = new Color(40, 35, 80);
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        
        // Create button panel with glass effect
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw subtle gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(40, 35, 80, 150), 
                    0, getHeight(), new Color(30, 25, 60, 150)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add subtle top border
                g2d.setColor(new Color(80, 70, 120, 100));
                g2d.drawLine(0, 0, getWidth(), 0);
            }
        };
        buttonPanel.setOpaque(false);
        
        // Create modern styled buttons
        addButton = new JButton("Add Vehicle") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient;
                if (getModel().isPressed()) {
                    gradient = new GradientPaint(
                        0, 0, new Color(255, 50, 150).darker(), 
                        0, getHeight(), new Color(255, 20, 100).darker()
                    );
                } else if (getModel().isRollover()) {
                    gradient = new GradientPaint(
                        0, 0, new Color(255, 70, 170), 
                        0, getHeight(), new Color(255, 40, 120)
                    );
                } else {
                    gradient = new GradientPaint(
                        0, 0, new Color(255, 50, 150), 
                        0, getHeight(), new Color(255, 20, 100)
                    );
                }
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Add highlight at the top
                g2d.setPaint(new Color(255, 255, 255, 70));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() / 3, 15, 15);
                
                // Draw text with icon
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                
                // Draw plus icon
                g2d.setStroke(new BasicStroke(2));
                int iconSize = 12;
                int iconX = (getWidth() - textWidth) / 2 - iconSize - 5;
                int iconY = getHeight() / 2;
                g2d.drawLine(iconX, iconY, iconX + iconSize, iconY);
                g2d.drawLine(iconX + iconSize/2, iconY - iconSize/2, iconX + iconSize/2, iconY + iconSize/2);
                
                // Draw text
                g2d.drawString(getText(), (getWidth() - textWidth) / 2 + 5,
                              (getHeight() - textHeight) / 2 + fm.getAscent());
            }
        };
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        addButton.setPreferredSize(new Dimension(150, 45));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        refreshButton = new JButton("Refresh") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient;
                if (getModel().isPressed()) {
                    gradient = new GradientPaint(
                        0, 0, new Color(80, 130, 255).darker(), 
                        0, getHeight(), new Color(60, 100, 220).darker()
                    );
                } else if (getModel().isRollover()) {
                    gradient = new GradientPaint(
                        0, 0, new Color(100, 150, 255), 
                        0, getHeight(), new Color(80, 120, 240)
                    );
                } else {
                    gradient = new GradientPaint(
                        0, 0, new Color(80, 130, 255), 
                        0, getHeight(), new Color(60, 100, 220)
                    );
                }
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Add highlight at the top
                g2d.setPaint(new Color(255, 255, 255, 70));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() / 3, 15, 15);
                
                // Draw text with refresh icon
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                
                // Draw refresh icon (circular arrow)
                g2d.setStroke(new BasicStroke(2));
                int iconSize = 14;
                int iconX = (getWidth() - textWidth) / 2 - iconSize - 5;
                int iconY = getHeight() / 2 - iconSize/2;
                
                // Draw circle part
                g2d.drawArc(iconX, iconY, iconSize, iconSize, 45, 270);
                
                // Draw arrow head
                int arrowX = iconX + iconSize - 2;
                int arrowY = iconY + iconSize/2 - 2;
                g2d.drawLine(arrowX, arrowY, arrowX - 5, arrowY - 3);
                g2d.drawLine(arrowX, arrowY, arrowX - 3, arrowY + 5);
                
                // Draw text
                g2d.drawString(getText(), (getWidth() - textWidth) / 2 + 5,
                              (getHeight() - textHeight) / 2 + fm.getAscent());
            }
        };
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setContentAreaFilled(false);
        refreshButton.setPreferredSize(new Dimension(120, 45));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        
        // Add action listeners
        addButton.addActionListener(e -> showAddVehicleDialog());
        refreshButton.addActionListener(e -> loadVehicles());
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background
                g2d.setColor(getModel().isPressed() ? color.darker() : color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                g2d.drawString(getText(), (getWidth() - textWidth) / 2,
                              (getHeight() - textHeight) / 2 + fm.getAscent());
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(120, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    public void loadVehicles() {
        SwingWorker<List<Vehicle>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Vehicle> doInBackground() {
                // Load vehicles
                return DataManager.getInstance().getAllVehicles();
            }
            
            @Override
            protected void done() {
                try {
                    currentVehicles = get();
                    displayVehicles(currentVehicles);
                    parentFrame.setStatusMessage("Vehicles loaded successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(VehicleManagementPanel.this,
                        "Error loading vehicles: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    // Modify the displayVehicles method to ensure it uses fresh images
    private void displayVehicles(List<Vehicle> vehicles) {
        // Clear existing vehicle cards
        vehiclesContainer.removeAll();
        
        if (vehicles.isEmpty()) {
            JLabel noVehiclesLabel = new JLabel("No vehicles found");
            noVehiclesLabel.setFont(new Font("Arial", Font.BOLD, 18));
            noVehiclesLabel.setForeground(Color.WHITE);
            vehiclesContainer.add(noVehiclesLabel);
        } else {
            // Reload icons to ensure fresh images
            loadIcons();
        
            // Add vehicle cards
            for (Vehicle vehicle : vehicles) {
                // Create vehicle card with fresh image
                JPanel cardPanel = createVehicleCard(vehicle);
                vehiclesContainer.add(cardPanel);
            }
        }

        // Refresh UI
        vehiclesContainer.revalidate();
        vehiclesContainer.repaint();
    }
    
    // Add a new method to create vehicle cards
    private JPanel createVehicleCard(Vehicle vehicle) {
        JPanel cardPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create glass-like gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(40, 35, 80, 240),
                        0, getHeight(), new Color(25, 20, 60, 240)
                );
                g2d.setPaint(gradient);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));

                // Add subtle highlight at the top
                g2d.setPaint(new Color(255, 255, 255, 30));
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), 2, 2, 2));
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(350, 350)); // Taller card for better layout
        cardPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Create a main content panel with BorderLayout
        JPanel contentPanel = new JPanel(new BorderLayout(10, 12));
        contentPanel.setOpaque(false);

        // Create improved image panel with shadow effect
        JPanel imagePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw subtle shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(3, 3, getWidth() - 4, getHeight() - 4, 15, 15);

                // Draw border
                g2d.setColor(new Color(80, 70, 120, 100));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        imagePanel.setOpaque(false);
        imagePanel.setPreferredSize(new Dimension(320, 180)); // Larger, landscape-oriented image

        // Get appropriate icon based on vehicle type or ID
        ImageIcon vehicleIcon = null;

        // Try to load vehicle-specific image first
        try {
            java.io.File imageFile = new java.io.File("images/" + vehicle.getVehicleId() + ".jpg");
            if (imageFile.exists()) {
                // Use ImageIO to read the image fresh from disk
                Image image = javax.imageio.ImageIO.read(imageFile);
                // Scale the image to appropriate size - landscape orientation
                Image scaledImage = image.getScaledInstance(320, 180, Image.SCALE_SMOOTH);
                vehicleIcon = new ImageIcon(scaledImage);
            } else {
                // Fall back to type-based icon
                switch (vehicle.getType()) {
                    case SEDAN:
                        vehicleIcon = sedanIcon;
                        break;
                    case SUV:
                        vehicleIcon = suvIcon;
                        break;
                    case VAN:
                        vehicleIcon = vanIcon;
                        break;
                    case BUS:
                        vehicleIcon = busIcon;
                        break;
                    case TRUCK:
                        vehicleIcon = truckIcon;
                        break;
                    default:
                        vehicleIcon = sedanIcon;
                }
            }
        } catch (Exception e) {
            // Fall back to type-based icon on error
            switch (vehicle.getType()) {
                case SEDAN:
                    vehicleIcon = sedanIcon;
                    break;
                case SUV:
                    vehicleIcon = suvIcon;
                    break;
                case VAN:
                    vehicleIcon = vanIcon;
                    break;
                case BUS:
                    vehicleIcon = busIcon;
                    break;
                case TRUCK:
                    vehicleIcon = truckIcon;
                    break;
                default:
                    vehicleIcon = sedanIcon;
            }
            e.printStackTrace();
        }

        // Create vehicle image label with larger icon
        JLabel imageLabel = new JLabel(vehicleIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Create title panel with vehicle name
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        // Add make and model text with improved styling
        JLabel titleLabel = new JLabel(vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Create info panel with two columns for better organization
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Add vehicle details with improved styling
        addCardDetailRow(infoPanel, "Type:", vehicle.getType().toString());
        addCardDetailRow(infoPanel, "License:", vehicle.getLicensePlate());
        addCardDetailRow(infoPanel, "Capacity:", String.valueOf(vehicle.getCapacity()));
        addCardDetailRow(infoPanel, "Fuel:", vehicle.getFuelType().toString());
        addCardDetailRow(infoPanel, "Mileage:", String.format("%.1f", vehicle.getMileage()));
        addCardDetailRow(infoPanel, "Location:", vehicle.getCurrentLocation());

        // Create status indicator with improved styling
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        // Check if the vehicle has any active reservations for the current time
        boolean hasActiveReservation = checkForActiveReservations(vehicle);

        // Determine actual availability based on both the vehicle's available flag and current reservations
        final boolean actuallyAvailable = vehicle.isAvailable() && !hasActiveReservation;

        JPanel statusIndicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw glowing circle with color based on availability
                Color baseColor = actuallyAvailable ? new Color(0, 200, 0) : new Color(200, 0, 0);

                // Draw outer glow
                for (int i = 5; i > 0; i--) {
                    g2d.setColor(new Color(
                            baseColor.getRed(),
                            baseColor.getGreen(),
                            baseColor.getBlue(),
                            50 - i * 8
                    ));
                    g2d.fillOval(-i, -i, getWidth() + (i * 2), getHeight() + (i * 2));
                }

                // Draw main circle
                g2d.setColor(baseColor);
                g2d.fillOval(0, 0, getWidth(), getHeight());

                // Draw highlight
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillOval(3, 3, 4, 4);
            }
        };
        statusIndicator.setPreferredSize(new Dimension(12, 12));
        statusIndicator.setOpaque(false);

        JLabel availabilityLabel = new JLabel(actuallyAvailable ? "Available" : "Not Available");
        availabilityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        availabilityLabel.setForeground(actuallyAvailable ? new Color(100, 255, 100) : new Color(255, 100, 100));

        statusPanel.add(statusIndicator);
        statusPanel.add(availabilityLabel);

        // Create action buttons panel with improved styling
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JButton editButton = createActionButton("Edit", new Color(64, 120, 255));
        JButton detailsButton = createActionButton("Details", new Color(64, 180, 64));
        JButton deleteButton = createActionButton("Delete", new Color(255, 64, 64));

        // Add action listeners
        editButton.addActionListener(e -> showEditVehicleDialog(vehicle));
        detailsButton.addActionListener(e -> showVehicleDetails(vehicle));
        deleteButton.addActionListener(e -> deleteVehicle(vehicle));

        buttonsPanel.add(editButton);
        buttonsPanel.add(detailsButton);
        buttonsPanel.add(deleteButton);

        // Add components to content panel
        contentPanel.add(imagePanel, BorderLayout.NORTH);
        contentPanel.add(titlePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(infoPanel, BorderLayout.NORTH);
        bottomPanel.add(statusPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add content panel to card
        cardPanel.add(contentPanel, BorderLayout.CENTER);

        return cardPanel;
    }

    // Add a helper method to check for active reservations
    private boolean checkForActiveReservations(Vehicle vehicle) {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> vehicleReservations = DataManager.getInstance().getVehicleReservations(vehicle.getVehicleId());

        for (Reservation res : vehicleReservations) {
            // Only consider active reservations (not cancelled or rejected)
            if (res.getStatus() != Reservation.ReservationStatus.CANCELLED &&
                    res.getStatus() != Reservation.ReservationStatus.REJECTED) {

                // Check if the current time falls within the reservation period
                if (now.isAfter(res.getStartDateTime()) && now.isBefore(res.getEndDateTime())) {
                    System.out.println("Vehicle " + vehicle.getVehicleId() + " has an active reservation: " + res.getReservationId());
                    return true;
                }
            }
        }

        return false;
    }

    private void addCardDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 13));
        labelComponent.setForeground(new Color(180, 180, 255));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 13));
        valueComponent.setForeground(new Color(220, 220, 220));

        panel.add(labelComponent);
        panel.add(valueComponent);
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create glass-like button effect
                if (getModel().isPressed()) {
                    // Pressed state
                    GradientPaint gradient = new GradientPaint(
                            0, 0, color.darker().darker(),
                            0, getHeight(), color.darker()
                    );
                    g2d.setPaint(gradient);
                } else if (getModel().isRollover()) {
                    // Hover state
                    GradientPaint gradient = new GradientPaint(
                            0, 0, color.brighter(),
                            0, getHeight(), color
                    );
                    g2d.setPaint(gradient);
                } else {
                    // Normal state
                    GradientPaint gradient = new GradientPaint(
                            0, 0, color,
                            0, getHeight(), color.darker()
                    );
                    g2d.setPaint(gradient);
                }

                // Draw rounded button background
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Add subtle border
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                // Add highlight at the top
                g2d.setPaint(new Color(255, 255, 255, 100));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() / 3, 15, 15);

                // Draw text with shadow
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();

                // Draw text shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(getText(), (getWidth() - textWidth) / 2 + 1,
                        (getHeight() - textHeight) / 2 + fm.getAscent() + 1);

                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), (getWidth() - textWidth) / 2,
                        (getHeight() - textHeight) / 2 + fm.getAscent());
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(90, 32));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void searchVehicles() {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.equals("search vehicles...")) {
            searchTerm = "";
        }

        final String term = searchTerm;

        if (term.isEmpty() && filterTypeComboBox.getSelectedIndex() == 0) {
            // If no search term and no filter, load all vehicles
            loadVehicles();
            return;
        }

        // Filter vehicles based on search term
        List<Vehicle> filteredVehicles = currentVehicles.stream()
                .filter(vehicle ->
                        (term.isEmpty() ||
                                vehicle.getMake().toLowerCase().contains(term) ||
                                vehicle.getModel().toLowerCase().contains(term) ||
                                vehicle.getLicensePlate().toLowerCase().contains(term) ||
                                String.valueOf(vehicle.getYear()).contains(term)) &&
                                (filterTypeComboBox.getSelectedIndex() == 0 ||
                                        vehicle.getType().toString().equals(filterTypeComboBox.getSelectedItem()))
                )
                .collect(Collectors.toList());

        displayVehicles(filteredVehicles);
        parentFrame.setStatusMessage("Found " + filteredVehicles.size() + " vehicles matching search criteria");
    }

    private void filterVehicles() {
        // This will apply both the search term and the type filter
        searchVehicles();
    }

    private void showAddVehicleDialog() {
        // Create a modern dialog for adding a new vehicle
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add New Vehicle", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        // Create content panel with dark background
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(parentFrame.getBackgroundColor());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(parentFrame.getFieldBgColor());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Create form fields with modern styling
        JTextField makeField = createStyledTextField("");
        JTextField modelField = createStyledTextField("");
        JTextField yearField = createStyledTextField("");
        JTextField licensePlateField = createStyledTextField("");

        JComboBox<Vehicle.VehicleType> typeComboBox = new JComboBox<>(Vehicle.VehicleType.values());
        styleComboBox(typeComboBox);

        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));
        styleSpinner(capacitySpinner);

        JComboBox<Vehicle.FuelType> fuelTypeComboBox = new JComboBox<>(Vehicle.FuelType.values());
        styleComboBox(fuelTypeComboBox);

        JCheckBox availableCheckBox = new JCheckBox();
        availableCheckBox.setSelected(true);
        availableCheckBox.setBackground(parentFrame.getFieldBgColor());
        availableCheckBox.setForeground(Color.WHITE);

        // Add form fields to panel
        addFormField(formPanel, gbc, 0, "Make:", makeField);
        addFormField(formPanel, gbc, 1, "Model:", modelField);
        addFormField(formPanel, gbc, 2, "Year:", yearField);
        addFormField(formPanel, gbc, 3, "License Plate:", licensePlateField);
        addFormField(formPanel, gbc, 4, "Vehicle Type:", typeComboBox);
        addFormField(formPanel, gbc, 5, "Capacity:", capacitySpinner);
        addFormField(formPanel, gbc, 6, "Fuel Type:", fuelTypeComboBox);
        addFormField(formPanel, gbc, 7, "Available:", availableCheckBox);

        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton saveButton = createStyledButton("Save", parentFrame.getPrimaryAccentColor());
        JButton cancelButton = createStyledButton("Cancel", new Color(150, 150, 150));

        // Add action listeners
        saveButton.addActionListener(e -> {
            // Validate input
            if (makeField.getText().trim().isEmpty() ||
                    modelField.getText().trim().isEmpty() ||
                    licensePlateField.getText().trim().isEmpty() ||
                    yearField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please fill in all required fields.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int year;
            try {
                year = Integer.parseInt(yearField.getText().trim());
                if (year < 1900 || year > 2100) {
                    throw new NumberFormatException("Year out of range");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a valid year (1900-2100).",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create new vehicle
            String vehicleId = "V-" + UUID.randomUUID().toString().substring(0, 8);
            Vehicle vehicle = new Vehicle(
                    vehicleId,
                    makeField.getText().trim(),
                    modelField.getText().trim(),
                    year,
                    licensePlateField.getText().trim(),
                    (Vehicle.VehicleType) typeComboBox.getSelectedItem(),
                    (Integer) capacitySpinner.getValue(),
                    (Vehicle.FuelType) fuelTypeComboBox.getSelectedItem()
            );

            vehicle.setAvailable(availableCheckBox.isSelected());

            // Save vehicle
            DataManager.getInstance().addVehicle(vehicle);

            // Close dialog and refresh
            dialog.dispose();
            loadVehicles();

            // Upload image for the vehicle
            uploadVehicleImage(vehicle);
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        // Add components to content panel
        contentPanel.add(new JLabel("Add New Vehicle", JLabel.CENTER) {{
            setFont(new Font("Arial", Font.BOLD, 20));
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        }}, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Set dialog content and show
        dialog.setContentPane(contentPanel);
        dialog.setVisible(true);
    }

    private JTextField createStyledTextField(String text) {
        JTextField textField = new JTextField(text, 20);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setForeground(Color.WHITE);
        textField.setBackground(parentFrame.getBackgroundColor().brighter());
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        textField.setCaretColor(Color.WHITE);
        return textField;
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setForeground(Color.WHITE);
        comboBox.setBackground(parentFrame.getBackgroundColor().brighter());
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true));
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Arial", Font.PLAIN, 14));
        spinner.getEditor().setBackground(parentFrame.getBackgroundColor().brighter());
        spinner.getEditor().getComponent(0).setForeground(Color.WHITE);
        spinner.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true));

        // Style the text field inside the spinner
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor) editor;
            defaultEditor.getTextField().setBackground(parentFrame.getBackgroundColor().brighter());
            defaultEditor.getTextField().setForeground(Color.WHITE);
            defaultEditor.getTextField().setCaretColor(Color.WHITE);
        }
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    // Modify the showEditVehicleDialog method to update the image preview when the Upload Image button is clicked
    private void showEditVehicleDialog(Vehicle vehicle) {
        // Create a modern dialog for editing a vehicle
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit Vehicle", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        // Create content panel with dark background
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(parentFrame.getBackgroundColor());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(parentFrame.getFieldBgColor());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Create form fields with modern styling and pre-filled values
        JTextField makeField = createStyledTextField(vehicle.getMake());
        JTextField modelField = createStyledTextField(vehicle.getModel());
        JTextField yearField = createStyledTextField(String.valueOf(vehicle.getYear()));
        JTextField licensePlateField = createStyledTextField(vehicle.getLicensePlate());

        JComboBox<Vehicle.VehicleType> typeComboBox = new JComboBox<>(Vehicle.VehicleType.values());
        typeComboBox.setSelectedItem(vehicle.getType());
        styleComboBox(typeComboBox);

        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(vehicle.getCapacity(), 1, 50, 1));
        styleSpinner(capacitySpinner);

        JComboBox<Vehicle.FuelType> fuelTypeComboBox = new JComboBox<>(Vehicle.FuelType.values());
        fuelTypeComboBox.setSelectedItem(vehicle.getFuelType());
        styleComboBox(fuelTypeComboBox);

        JCheckBox availableCheckBox = new JCheckBox();
        availableCheckBox.setSelected(vehicle.isAvailable());
        availableCheckBox.setBackground(parentFrame.getFieldBgColor());
        availableCheckBox.setForeground(Color.WHITE);

        // Add form fields to panel
        addFormField(formPanel, gbc, 0, "Make:", makeField);
        addFormField(formPanel, gbc, 1, "Model:", modelField);
        addFormField(formPanel, gbc, 2, "Year:", yearField);
        addFormField(formPanel, gbc, 3, "License Plate:", licensePlateField);
        addFormField(formPanel, gbc, 4, "Vehicle Type:", typeComboBox);
        addFormField(formPanel, gbc, 5, "Capacity:", capacitySpinner);
        addFormField(formPanel, gbc, 6, "Fuel Type:", fuelTypeComboBox);
        addFormField(formPanel, gbc, 7, "Available:", availableCheckBox);

        // Add vehicle image preview panel
        JPanel imagePreviewPanel = createVehicleImagePreviewPanel(vehicle);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(imagePreviewPanel, gbc);

        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton saveButton = createStyledButton("Save", parentFrame.getPrimaryAccentColor());
        JButton uploadImageButton = createStyledButton("Upload Image", parentFrame.getSecondaryAccentColor());
        uploadImageButton.setPreferredSize(new Dimension(150, 40));
        
        // Modified upload image action to update the preview immediately
        uploadImageButton.addActionListener(e -> {
            // Create a custom uploader that will update this specific dialog
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Vehicle Image");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png", "gif"));
    
            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                try {
                    // Get selected file
                    java.io.File selectedFile = fileChooser.getSelectedFile();
            
                    // Create images directory if it doesn't exist
                    ensureImagesDirectoryExists();
            
                    // Load the image and resize it to standard dimensions for consistency
                    BufferedImage originalImage = javax.imageio.ImageIO.read(selectedFile);
                    if (originalImage == null) {
                        throw new IOException("Failed to read image file");
                    }
            
                    // Create a new BufferedImage with the desired dimensions
                    BufferedImage resizedImage = new BufferedImage(640, 360, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = resizedImage.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.setColor(Color.BLACK); // Set background color
                    g.fillRect(0, 0, 640, 360); // Fill background
            
                    // Calculate dimensions to maintain aspect ratio
                    double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
                    int targetWidth, targetHeight;
                    int x = 0, y = 0;
            
                    if (aspectRatio > (640.0 / 360.0)) {
                        // Image is wider than target aspect ratio
                        targetWidth = 640;
                        targetHeight = (int) (640 / aspectRatio);
                        y = (360 - targetHeight) / 2;
                    } else {
                        // Image is taller than target aspect ratio
                        targetHeight = 360;
                        targetWidth = (int) (360 * aspectRatio);
                        x = (640 - targetWidth) / 2;
                    }
            
                    // Draw the image centered
                    g.drawImage(originalImage, x, y, targetWidth, targetHeight, null);
                    g.dispose();
            
                    // Create destination file with vehicle ID as filename to make it unique
                    java.io.File destFile = new java.io.File("images/" + vehicle.getVehicleId() + ".jpg");
            
                    // Save the resized image
                    boolean success = javax.imageio.ImageIO.write(resizedImage, "jpg", destFile);
                    if (!success) {
                        throw new IOException("Failed to write image file");
                    }
            
                    // Also save a type-specific image if it doesn't exist yet
                    java.io.File typeFile = new java.io.File("images/" + vehicle.getType().toString().toLowerCase() + ".jpg");
                    if (!typeFile.exists()) {
                        javax.imageio.ImageIO.write(resizedImage, "jpg", typeFile);
                        System.out.println("Created type-specific image: " + typeFile.getPath());
                }
                
                // Clear the icon cache
                clearIconCache(vehicle);
            
                // Update the image preview panel immediately
                imagePreviewPanel.removeAll();
                
                // Create a new image icon from the freshly saved file
                Image image = javax.imageio.ImageIO.read(destFile);
                Image scaledImage = image.getScaledInstance(320, 180, Image.SCALE_SMOOTH);
                ImageIcon newIcon = new ImageIcon(scaledImage);
                
                JLabel imageLabel = new JLabel(newIcon);
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                imagePreviewPanel.add(imageLabel, BorderLayout.CENTER);
                imagePreviewPanel.revalidate();
                imagePreviewPanel.repaint();
            
                JOptionPane.showMessageDialog(dialog,
                    "Vehicle image uploaded and resized successfully.",
                    "Upload Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error uploading image: " + ex.getMessage(),
                    "Upload Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    });
    
    JButton cancelButton = createStyledButton("Cancel", new Color(150, 150, 150));

    // Add action listeners
    saveButton.addActionListener(e -> {
        // Validate input
        if (makeField.getText().trim().isEmpty() ||
                modelField.getText().trim().isEmpty() ||
                licensePlateField.getText().trim().isEmpty() ||
                yearField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(dialog,
                    "Please fill in all required fields.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearField.getText().trim());
            if (year < 1900 || year > 2100) {
                throw new NumberFormatException("Year out of range");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog,
                    "Please enter a valid year (1900-2100).",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update vehicle
        vehicle.setMake(makeField.getText().trim());
        vehicle.setModel(modelField.getText().trim());
        vehicle.setYear(year);
        vehicle.setLicensePlate(licensePlateField.getText().trim());
        vehicle.setType((Vehicle.VehicleType) typeComboBox.getSelectedItem());
        vehicle.setCapacity((Integer) capacitySpinner.getValue());
        vehicle.setFuelType((Vehicle.FuelType) fuelTypeComboBox.getSelectedItem());
        vehicle.setAvailable(availableCheckBox.isSelected());

        // Save vehicle
        DataManager.getInstance().updateVehicle(vehicle);

        // Close dialog and refresh
        dialog.dispose();
        loadVehicles();
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    buttonsPanel.add(uploadImageButton);
    buttonsPanel.add(saveButton);
    buttonsPanel.add(cancelButton);

    // Create a panel to hold both the form and image preview
    JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
    mainPanel.setOpaque(false);
    mainPanel.add(formPanel, BorderLayout.CENTER);
    mainPanel.add(imagePreviewPanel, BorderLayout.EAST);

    contentPanel.add(new JLabel("Edit Vehicle", JLabel.CENTER) {{
        setFont(new Font("Arial", Font.BOLD, 20));
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
    }}, BorderLayout.NORTH);
    contentPanel.add(mainPanel, BorderLayout.CENTER);
    contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

    // Set dialog content and show
    dialog.setContentPane(contentPanel);
    dialog.setVisible(true);
}

// Modify the createVehicleImagePreviewPanel method to ensure it always loads the latest image
private JPanel createVehicleImagePreviewPanel(Vehicle vehicle) {
    JPanel imagePanel = new JPanel(new BorderLayout());
    imagePanel.setOpaque(false);
    imagePanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
        "Vehicle Image",
        javax.swing.border.TitledBorder.LEFT,
        javax.swing.border.TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14),
        Color.WHITE
    ));
    
    // Get appropriate icon based on vehicle type or ID
    ImageIcon vehicleIcon;
    
    // Try to load vehicle-specific image first, with cache-busting
    try {
        java.io.File imageFile = new java.io.File("images/" + vehicle.getVehicleId() + ".jpg");
        if (imageFile.exists()) {
            // Use ImageIO to read the image fresh from disk
            Image image = javax.imageio.ImageIO.read(imageFile);
            // Scale the image to appropriate size for preview - landscape orientation
            Image scaledImage = image.getScaledInstance(320, 180, Image.SCALE_SMOOTH);
            vehicleIcon = new ImageIcon(scaledImage);
        } else {
            // Fall back to type-based icon
            switch (vehicle.getType()) {
                case SEDAN:
                    vehicleIcon = sedanIcon;
                    break;
                case SUV:
                    vehicleIcon = suvIcon;
                    break;
                case VAN:
                    vehicleIcon = vanIcon;
                    break;
                case BUS:
                    vehicleIcon = busIcon;
                    break;
                case TRUCK:
                    vehicleIcon = truckIcon;
                    break;
                default:
                    vehicleIcon = sedanIcon;
            }
        }
    } catch (Exception e) {
        // Fall back to type-based icon on error
        switch (vehicle.getType()) {
            case SEDAN:
                vehicleIcon = sedanIcon;
                break;
            case SUV:
                vehicleIcon = suvIcon;
                break;
            case VAN:
                vehicleIcon = vanIcon;
                break;
            case BUS:
                vehicleIcon = busIcon;
                break;
            case TRUCK:
                vehicleIcon = truckIcon;
                break;
            default:
                vehicleIcon = sedanIcon;
        }
        e.printStackTrace();
    }
    
    // Create image label with larger preview
    JLabel imageLabel = new JLabel(vehicleIcon);
    imageLabel.setHorizontalAlignment(JLabel.CENTER);
    imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    imagePanel.add(imageLabel, BorderLayout.CENTER);
    return imagePanel;
}

private void showVehicleDetails(Vehicle vehicle) {
        // Create a modern dialog for showing vehicle details
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Vehicle Details", true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);

        // Create content panel with dark background
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(parentFrame.getBackgroundColor());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create header with vehicle name and image
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Get appropriate icon based on vehicle type
        ImageIcon vehicleIcon;
        switch (vehicle.getType()) {
            case SEDAN:
                vehicleIcon = sedanIcon;
                break;
            case SUV:
                vehicleIcon = suvIcon;
                break;
            case VAN:
                vehicleIcon = vanIcon;
            case BUS:
                vehicleIcon = busIcon;
                break;
            case TRUCK:
                vehicleIcon = truckIcon;
                break;
            default:
                vehicleIcon = sedanIcon;
        }

        // Create vehicle image label with larger icon
        ImageIcon largeIcon = new ImageIcon(vehicleIcon.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH));
        JLabel imageLabel = new JLabel(largeIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true));

        JLabel titleLabel = new JLabel(vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(imageLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Create details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(parentFrame.getFieldBgColor());
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Add vehicle details
        addDetailRow(detailsPanel, gbc, 0, "Vehicle ID:", vehicle.getVehicleId());
        addDetailRow(detailsPanel, gbc, 1, "Make:", vehicle.getMake());
        addDetailRow(detailsPanel, gbc, 2, "Model:", vehicle.getModel());
        addDetailRow(detailsPanel, gbc, 3, "Year:", String.valueOf(vehicle.getYear()));
        addDetailRow(detailsPanel, gbc, 4, "License Plate:", vehicle.getLicensePlate());
        addDetailRow(detailsPanel, gbc, 5, "Type:", vehicle.getType().toString());
        addDetailRow(detailsPanel, gbc, 6, "Capacity:", String.valueOf(vehicle.getCapacity()));
        addDetailRow(detailsPanel, gbc, 7, "Fuel Type:", vehicle.getFuelType().toString());
        addDetailRow(detailsPanel, gbc, 8, "Mileage:", String.format("%.1f", vehicle.getMileage()));
        
        // Add availability status with colored indicator
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        
        JLabel availabilityLabel = new JLabel("Availability:");
        availabilityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        availabilityLabel.setForeground(Color.WHITE);
        detailsPanel.add(availabilityLabel, gbc);
        
        gbc.gridx = 1;
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusPanel.setOpaque(false);
        
        // Check if the vehicle has any active reservations for the current time
        boolean hasActiveReservation = false;
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> vehicleReservations = DataManager.getInstance().getVehicleReservations(vehicle.getVehicleId());
        for (Reservation res : vehicleReservations) {
            if (res.getStatus() != Reservation.ReservationStatus.CANCELLED && 
                res.getStatus() != Reservation.ReservationStatus.REJECTED &&
                now.isAfter(res.getStartDateTime()) && 
                now.isBefore(res.getEndDateTime())) {
                hasActiveReservation = true;
                break;
            }
        }
        
        // Determine actual availability based on both the vehicle's available flag and current reservations
        final boolean actuallyAvailable = vehicle.isAvailable() && !hasActiveReservation;
        
        JPanel statusIndicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circle with color based on availability
                g2d.setColor(actuallyAvailable ? new Color(0, 200, 0) : new Color(200, 0, 0));
                g2d.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        statusIndicator.setPreferredSize(new Dimension(15, 15));
        statusIndicator.setOpaque(false);
        
        JLabel statusLabel = new JLabel(actuallyAvailable ? "Available" : "Not Available");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(actuallyAvailable ? new Color(0, 200, 0) : new Color(200, 0, 0));
        
        statusPanel.add(statusIndicator);
        statusPanel.add(statusLabel);
        
        detailsPanel.add(statusPanel, gbc);
        
        // Add current location
        addDetailRow(detailsPanel, gbc, 10, "Current Location:", vehicle.getCurrentLocation());
        
        // Add notes section
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setOpaque(false);
        notesPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
            "Notes",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            Color.WHITE
        ));
        
        JTextArea notesArea = new JTextArea(vehicle.getNotes());
        notesArea.setFont(new Font("Arial", Font.PLAIN, 14));
        notesArea.setForeground(Color.WHITE);
        notesArea.setBackground(parentFrame.getBackgroundColor().brighter());
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setEditable(false);
        notesArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setBorder(null);
        
        notesPanel.add(notesScrollPane, BorderLayout.CENTER);
        
        detailsPanel.add(notesPanel, gbc);
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);
        
        JButton closeButton = createStyledButton("Close", parentFrame.getSecondaryAccentColor());
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonsPanel.add(closeButton);
        
        // Add components to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(detailsPanel, BorderLayout.CENTER);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Set dialog content and show
        dialog.setContentPane(contentPanel);
        dialog.setVisible(true);
    }
    
    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        valueLabel.setForeground(new Color(200, 200, 200));
        panel.add(valueLabel, gbc);
    }
    
    private void deleteVehicle(Vehicle vehicle) {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the vehicle: " + vehicle.getYear() + " " +
            vehicle.getMake() + " " + vehicle.getModel() + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (choice == JOptionPane.YES_OPTION) {
            DataManager.getInstance().deleteVehicle(vehicle.getVehicleId());
            loadVehicles();
        }
    }
    
    // Add this method to the VehicleManagementPanel class to handle image uploads
    // Modify the uploadVehicleImage method to refresh the UI immediately after upload
    private void uploadVehicleImage(Vehicle vehicle) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Vehicle Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // Get selected file
                java.io.File selectedFile = fileChooser.getSelectedFile();
        
                // Create images directory if it doesn't exist
                ensureImagesDirectoryExists();
        
                // Load the image and resize it to standard dimensions for consistency
                BufferedImage originalImage = javax.imageio.ImageIO.read(selectedFile);
                if (originalImage == null) {
                    throw new IOException("Failed to read image file");
                }
        
                // Create a new BufferedImage with the desired dimensions
                BufferedImage resizedImage = new BufferedImage(640, 360, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = resizedImage.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.setColor(Color.BLACK); // Set background color
                g.fillRect(0, 0, 640, 360); // Fill background
        
                // Calculate dimensions to maintain aspect ratio
                double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
                int targetWidth, targetHeight;
                int x = 0, y = 0;
        
                if (aspectRatio > (640.0 / 360.0)) {
                    // Image is wider than target aspect ratio
                    targetWidth = 640;
                    targetHeight = (int) (640 / aspectRatio);
                    y = (360 - targetHeight) / 2;
                } else {
                    // Image is taller than target aspect ratio
                    targetHeight = 360;
                    targetWidth = (int) (360 * aspectRatio);
                    x = (640 - targetWidth) / 2;
                }
        
                // Draw the image centered
                g.drawImage(originalImage, x, y, targetWidth, targetHeight, null);
                g.dispose();
        
                // Create destination file with vehicle ID as filename to make it unique
                java.io.File destFile = new java.io.File("images/" + vehicle.getVehicleId() + ".jpg");
        
                // Save the resized image
                boolean success = javax.imageio.ImageIO.write(resizedImage, "jpg", destFile);
                if (!success) {
                    throw new IOException("Failed to write image file");
                }
        
                // Also save a type-specific image if it doesn't exist yet
                java.io.File typeFile = new java.io.File("images/" + vehicle.getType().toString().toLowerCase() + ".jpg");
                if (!typeFile.exists()) {
                    javax.imageio.ImageIO.write(resizedImage, "jpg", typeFile);
                    System.out.println("Created type-specific image: " + typeFile.getPath());
                }
        
                // Clear the icon cache to force reload of images
                clearIconCache(vehicle);
            
                JOptionPane.showMessageDialog(this,
                    "Vehicle image uploaded and resized successfully.",
                    "Upload Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        
                // Refresh the display
                loadVehicles();
            
                // If we're in an edit dialog, update the image preview immediately
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window instanceof JDialog) {
                    // Find the image preview panel in the dialog and update it
                    updateImagePreviewInDialog((JDialog)window, vehicle);
                }
        
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error uploading image: " + e.getMessage(),
                    "Upload Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    

    
    // Custom FlowLayout that supports wrapping and proper alignment
    private class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }
        
        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }
        
        @Override
        public Dimension minimumLayoutSize(Container target) {
            return layoutSize(target, false);
        }
        
        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();
                
                if (targetWidth == 0) {
                    targetWidth = Integer.MAX_VALUE;
                }
                
                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
                int maxWidth = targetWidth - horizontalInsetsAndGap;
                
                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;
                
                int nmembers = target.getComponentCount();
                
                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);
                    
                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                        
                        if (rowWidth + d.width > maxWidth) {
                            addRow(dim, rowWidth, rowHeight);
                            rowWidth = 0;
                            rowHeight = 0;
                        }
                        
                        if (rowWidth != 0) {
                            rowWidth += hgap;
                        }
                        
                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
                
                addRow(dim, rowWidth, rowHeight);
                
                dim.width += horizontalInsetsAndGap;
                dim.height += insets.top + insets.bottom + vgap * 2;
                
                return dim;
            }
        }
        
        private void addRow(Dimension dim, int rowWidth, int rowHeight) {
            dim.width = Math.max(dim.width, rowWidth);
            
            if (dim.height > 0) {
                dim.height += getVgap();
            }
            
            dim.height += rowHeight;
        }
    }

    // Add a method to clear the icon cache
    private void clearIconCache(Vehicle vehicle) {
        // Reset the specific vehicle icon in the cache
        switch (vehicle.getType()) {
            case SEDAN:
                sedanIcon = createVehicleIcon(Color.BLUE, "SEDAN");
                break;
            case SUV:
                suvIcon = createVehicleIcon(new Color(0, 150, 0), "SUV");
                break;
            case VAN:
                vanIcon = createVehicleIcon(new Color(150, 0, 150), "VAN");
                break;
            case BUS:
                busIcon = createVehicleIcon(new Color(200, 100, 0), "BUS");
                break;
            case TRUCK:
                truckIcon = createVehicleIcon(new Color(150, 0, 0), "TRUCK");
                break;
        }
    
        // Also create a specific icon for this vehicle ID to ensure it's refreshed
        createVehicleIcon(Color.BLUE, vehicle.getVehicleId());
    }

    // Add a method to update the image preview in an open dialog
    private void updateImagePreviewInDialog(JDialog dialog, Vehicle vehicle) {
        // Look for components in the dialog
        Container contentPane = dialog.getContentPane();
        if (contentPane instanceof JPanel) {
            updateImagePreviewInPanel((JPanel)contentPane, vehicle);
        }
    }

    // Recursively search for and update image preview panels
    private void updateImagePreviewInPanel(JPanel panel, Vehicle vehicle) {
        // Check if this panel is a titled border panel with "Vehicle Image" title
        if (panel.getBorder() instanceof TitledBorder) {
            TitledBorder border = (TitledBorder)panel.getBorder();
            if (border.getTitle() != null && border.getTitle().equals("Vehicle Image")) {
                // This is likely our image preview panel, update it
                panel.removeAll();
            
                // Create a new image label with the updated image
                ImageIcon vehicleIcon = null;
                try {
                    // Force reload the image from disk
                    java.io.File imageFile = new java.io.File("images/" + vehicle.getVehicleId() + ".jpg");
                    if (imageFile.exists()) {
                        // Use ImageIO to read the image fresh from disk
                        Image image = javax.imageio.ImageIO.read(imageFile);
                        // Scale the image to appropriate size for preview
                        Image scaledImage = image.getScaledInstance(320, 180, Image.SCALE_SMOOTH);
                        vehicleIcon = new ImageIcon(scaledImage);
                    } else {
                        // Fall back to type-based icon
                        switch (vehicle.getType()) {
                            case SEDAN: vehicleIcon = sedanIcon; break;
                            case SUV: vehicleIcon = suvIcon; break;
                            case VAN: vehicleIcon = vanIcon; break;
                            case BUS: vehicleIcon = busIcon; break;
                            case TRUCK: vehicleIcon = truckIcon; break;
                            default: vehicleIcon = sedanIcon;
                        }
                    }
                } catch (Exception e) {
                    // Fall back to type-based icon on error
                    switch (vehicle.getType()) {
                        case SEDAN: vehicleIcon = sedanIcon; break;
                        case SUV: vehicleIcon = suvIcon; break;
                        case VAN: vehicleIcon = vanIcon; break;
                        case BUS: vehicleIcon = busIcon; break;
                        case TRUCK: vehicleIcon = truckIcon; break;
                        default: vehicleIcon = sedanIcon;
                    }
                    e.printStackTrace();
                }
            
                // Create image label with larger preview
                JLabel imageLabel = new JLabel(vehicleIcon);
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
                panel.add(imageLabel, BorderLayout.CENTER);
                panel.revalidate();
                panel.repaint();
                return;
            }
        }
    
        // If we didn't find the image panel, search through child components
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                updateImagePreviewInPanel((JPanel)comp, vehicle);
            }
        }
    }
}
