import model.Vehicle;
import model.Reservation;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.UUID;

/**
* Enhanced panel for displaying available vehicles with modern, interactive UI
*/
public class AvailableVehiclesPanel extends JPanel {
    private MainFrameInterface parentFrame;
    private JPanel vehiclesContainer;
    private JScrollPane scrollPane;
    private JTextField searchField;
    private JComboBox<String> filterTypeComboBox;
    private JSlider capacitySlider;
    private JLabel capacityValueLabel;
    private JButton refreshButton;
    private JCheckBox showAllVehiclesCheckbox;
    
    // Vehicle cards currently displayed
    private List<Vehicle> currentVehicles;
    
    // Vehicle type icons
    private ImageIcon sedanIcon;
    private ImageIcon suvIcon;
    private ImageIcon vanIcon;
    private ImageIcon busIcon;
    private ImageIcon truckIcon;
    
    // Animation components
    private Timer fadeInTimer;
    private float fadeInAlpha = 0.0f;
    
    // Flag to show all vehicles or only available ones
    private boolean showAllVehicles = false;
    
    public AvailableVehiclesPanel(MainFrameInterface parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(parentFrame.getBackgroundColor());
        
        loadIcons();
        initComponents();
        loadVehicles();
        
        // Start fade-in animation
        startFadeInAnimation();
    }

// Add the missing loadIcons method
private void loadIcons() {
    // Load vehicle type icons - using placeholder colors for now
    // In a real application, these would be actual image files
    sedanIcon = createVehicleIcon(Color.BLUE, "SEDAN");
    suvIcon = createVehicleIcon(new Color(0, 150, 0), "SUV");
    vanIcon = createVehicleIcon(new Color(150, 0, 150), "VAN");
    busIcon = createVehicleIcon(new Color(200, 100, 0), "BUS");
    truckIcon = createVehicleIcon(new Color(150, 0, 0), "TRUCK");
}
    
    // Modify the createVehicleIcon method to better handle vehicle type-specific images
    private ImageIcon createVehicleIcon(Color color, String type) {
        // First try to load an actual image file for this specific vehicle
        if (type != null && !type.isEmpty()) {
            // Try to load the image from file
            try {
                // Check if we have a vehicle ID to use for the image
                if (type.startsWith("V-")) {
                    java.io.File imageFile = new java.io.File("images/" + type + ".jpg");
                    if (imageFile.exists()) {
                        Image image = javax.imageio.ImageIO.read(imageFile);
                        // Scale the image to appropriate size - landscape orientation
                        Image scaledImage = image.getScaledInstance(320, 180, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);
                    }
                }
        
            // Fall back to vehicle type image if specific vehicle image not found
            java.io.File imageFile = new java.io.File("images/" + type.toLowerCase() + ".jpg");
            if (imageFile.exists()) {
                Image image = javax.imageio.ImageIO.read(imageFile);
                // Scale the image to appropriate size - landscape orientation
                Image scaledImage = image.getScaledInstance(320, 180, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading vehicle image: " + e.getMessage());
        }
    }

    // Fallback to colored placeholder if image file not found - improved design
    int width = 320;
    int height = 180;
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    // Create gradient background
    GradientPaint gradient = new GradientPaint(
        0, 0, color.darker(), 
        width, height, color
    );
    g2d.setPaint(gradient);
    g2d.fillRoundRect(0, 0, width, height, 10, 10);
    
    // Draw vehicle silhouette based on type
    g2d.setColor(new Color(255, 255, 255, 180));
    int centerX = width / 2;
    int centerY = height / 2;
    
    if (type.equals("SEDAN")) {
        // Draw sedan silhouette
        g2d.fillRoundRect(centerX - 80, centerY - 15, 160, 30, 20, 20);
        g2d.fillRoundRect(centerX - 60, centerY - 35, 120, 25, 15, 15);
        // Wheels
        g2d.setColor(new Color(40, 40, 40));
        g2d.fillOval(centerX - 60, centerY + 10, 25, 25);
        g2d.fillOval(centerX + 35, centerY + 10, 25, 25);
    } else if (type.equals("SUV")) {
        // Draw SUV silhouette
        g2d.fillRoundRect(centerX - 80, centerY - 15, 160, 40, 20, 20);
        g2d.fillRoundRect(centerX - 70, centerY - 40, 140, 30, 10, 10);
        // Wheels
        g2d.setColor(new Color(40, 40, 40));
        g2d.fillOval(centerX - 60, centerY + 15, 30, 30);
        g2d.fillOval(centerX + 30, centerY + 15, 30, 30);
    } else if (type.equals("VAN")) {
        // Draw van silhouette
        g2d.fillRoundRect(centerX - 85, centerY - 40, 170, 70, 20, 20);
        // Wheels
        g2d.setColor(new Color(40, 40, 40));
        g2d.fillOval(centerX - 60, centerY + 20, 30, 30);
        g2d.fillOval(centerX + 30, centerY + 20, 30, 30);
    } else if (type.equals("BUS")) {
        // Draw bus silhouette
        g2d.fillRoundRect(centerX - 100, centerY - 40, 200, 70, 15, 15);
        // Windows
        g2d.setColor(new Color(100, 200, 255, 150));
        for (int i = 0; i < 5; i++) {
            g2d.fillRoundRect(centerX - 85 + (i * 35), centerY - 30, 25, 20, 5, 5);
        }
        // Wheels
        g2d.setColor(new Color(40, 40, 40));
        g2d.fillOval(centerX - 70, centerY + 20, 30, 30);
        g2d.fillOval(centerX + 40, centerY + 20, 30, 30);
    } else if (type.equals("TRUCK")) {
        // Draw truck silhouette
        g2d.fillRoundRect(centerX - 40, centerY - 35, 80, 40, 10, 10);
        g2d.fillRect(centerX - 90, centerY - 15, 180, 40);
        // Wheels
        g2d.setColor(new Color(40, 40, 40));
        g2d.fillOval(centerX - 60, centerY + 15, 30, 30);
        g2d.fillOval(centerX + 30, centerY + 15, 30, 30);
    }
    
    // Draw type text
    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font("Arial", Font.BOLD, 24));
    FontMetrics fm = g2d.getFontMetrics();
    int textWidth = fm.stringWidth(type);
    g2d.drawString(type, (width - textWidth) / 2, height - 20);
    
    g2d.dispose();
    return new ImageIcon(image);
}
    
    private void initComponents() {
        // Create header panel with title, search and filters
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(parentFrame.getBackgroundColor());
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Title label with animation effect
        JLabel titleLabel = new JLabel("Available Vehicles");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        // Create search and filter panel
        JPanel searchFilterPanel = new JPanel();
        searchFilterPanel.setLayout(new BoxLayout(searchFilterPanel, BoxLayout.Y_AXIS));
        searchFilterPanel.setOpaque(false);
        
        // Top row with search field and button
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchRow.setOpaque(false);
        
        // Search field with modern styling
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.WHITE);
        searchField.setBackground(parentFrame.getFieldBgColor());
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
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
        
        // Search button
        JButton searchButton = createStyledButton("Search", parentFrame.getPrimaryAccentColor());
        searchButton.addActionListener(e -> searchVehicles());
        
        // Add search components to row
        searchRow.add(searchField);
        searchRow.add(searchButton);
        
        // Bottom row with filters
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        filterRow.setOpaque(false);
        
        // Filter by type dropdown
        JLabel typeFilterLabel = new JLabel("Vehicle Type:");
        typeFilterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        typeFilterLabel.setForeground(Color.WHITE);
        
        String[] vehicleTypes = {"All Types", "SEDAN", "SUV", "VAN", "BUS", "TRUCK"};
        filterTypeComboBox = new JComboBox<>(vehicleTypes);
        filterTypeComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        filterTypeComboBox.setBackground(parentFrame.getFieldBgColor());
        filterTypeComboBox.setForeground(Color.WHITE);
        filterTypeComboBox.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true));
        filterTypeComboBox.setPreferredSize(new Dimension(120, 35));
        filterTypeComboBox.addActionListener(e -> filterVehicles());
        
        // Filter by capacity slider
        JLabel capacityFilterLabel = new JLabel("Min Capacity:");
        capacityFilterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        capacityFilterLabel.setForeground(Color.WHITE);
        
        capacitySlider = new JSlider(JSlider.HORIZONTAL, 1, 20, 1);
        capacitySlider.setOpaque(false);
        capacitySlider.setPreferredSize(new Dimension(150, 35));
        capacitySlider.setForeground(Color.WHITE);
        capacitySlider.setMajorTickSpacing(5);
        capacitySlider.setMinorTickSpacing(1);
        capacitySlider.setPaintTicks(true);
        capacitySlider.setSnapToTicks(true);
        
        // Custom slider UI
        capacitySlider.setUI(new javax.swing.plaf.basic.BasicSliderUI(capacitySlider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(parentFrame.getPrimaryAccentColor());
                g2d.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
                g2d.setColor(Color.WHITE);
                g2d.drawOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }
            
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(80, 70, 120));
                g2d.fillRoundRect(trackRect.x, trackRect.y + trackRect.height/2 - 2, 
                                 trackRect.width, 4, 4, 4);
            }
        });
        
        capacityValueLabel = new JLabel("1+");
        capacityValueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        capacityValueLabel.setForeground(Color.WHITE);
        capacityValueLabel.setPreferredSize(new Dimension(30, 35));
        
        capacitySlider.addChangeListener(e -> {
            capacityValueLabel.setText(capacitySlider.getValue() + "+");
            filterVehicles();
        });
        
        // Show all vehicles checkbox
        showAllVehiclesCheckbox = new JCheckBox("Show All Vehicles");
        showAllVehiclesCheckbox.setFont(new Font("Arial", Font.BOLD, 14));
        showAllVehiclesCheckbox.setForeground(Color.WHITE);
        showAllVehiclesCheckbox.setBackground(null);
        showAllVehiclesCheckbox.setOpaque(false);
        showAllVehiclesCheckbox.setFocusPainted(false);
        showAllVehiclesCheckbox.addActionListener(e -> {
            showAllVehicles = showAllVehiclesCheckbox.isSelected();
            loadVehicles();
        });
        
        // Refresh button
        refreshButton = createStyledButton("Refresh", parentFrame.getSecondaryAccentColor());
        refreshButton.addActionListener(e -> loadVehicles());
        
        // Add filter components to row
        filterRow.add(showAllVehiclesCheckbox);
        filterRow.add(Box.createHorizontalStrut(15));
        filterRow.add(typeFilterLabel);
        filterRow.add(filterTypeComboBox);
        filterRow.add(Box.createHorizontalStrut(15));
        filterRow.add(capacityFilterLabel);
        filterRow.add(capacitySlider);
        filterRow.add(capacityValueLabel);
        filterRow.add(Box.createHorizontalStrut(15));
        filterRow.add(refreshButton);
        
        // Add rows to search filter panel
        searchFilterPanel.add(searchRow);
        searchFilterPanel.add(filterRow);
        
        // Add components to header panel
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchFilterPanel, BorderLayout.EAST);
        
        // Create vehicles container with grid layout
        vehiclesContainer = new JPanel();
        vehiclesContainer.setLayout(new WrapLayout(FlowLayout.LEFT, 20, 20));
        vehiclesContainer.setBackground(parentFrame.getBackgroundColor());
        
        // Add scroll pane for vehicles container
        scrollPane = new JScrollPane(vehiclesContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(parentFrame.getBackgroundColor());
        
        // Style the scrollbar
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI(parentFrame.getPrimaryAccentColor()));
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
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
                if (showAllVehicles) {
                    // Load all vehicles
                    return DataManager.getInstance().getAllVehicles();
                } else {
                    // Get current date/time
                    LocalDateTime now = LocalDateTime.now();
                    
                    // Load available vehicles for the next 7 days
                    return DataManager.getInstance().getAvailableVehicles(now, now.plusDays(7));
                }
            }
            
            @Override
            protected void done() {
                try {
                    currentVehicles = get();
                    displayVehicles(currentVehicles);
                    parentFrame.setStatusMessage(showAllVehicles ? 
                        "All vehicles loaded successfully" : 
                        "Available vehicles loaded successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AvailableVehiclesPanel.this,
                        "Error loading vehicles: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void displayVehicles(List<Vehicle> vehicles) {
        // Clear existing vehicle cards
        vehiclesContainer.removeAll();
        
        if (vehicles.isEmpty()) {
            JLabel noVehiclesLabel = new JLabel("No vehicles found");
            noVehiclesLabel.setFont(new Font("Arial", Font.BOLD, 18));
            noVehiclesLabel.setForeground(Color.WHITE);
            
            JPanel centerPanel = new JPanel(new GridBagLayout());
            centerPanel.setOpaque(false);
            centerPanel.add(noVehiclesLabel);
            
            vehiclesContainer.add(centerPanel);
        } else {
            // Add vehicle cards
            for (Vehicle vehicle : vehicles) {
                vehiclesContainer.add(createVehicleCard(vehicle));
            }
        }
        
        // Refresh UI
        vehiclesContainer.revalidate();
        vehiclesContainer.repaint();
    }
    
    // Modify the createVehicleCard method to correctly handle vehicle images
    private JPanel createVehicleCard(Vehicle vehicle) {
        // Check if the vehicle is currently reserved
        boolean isCurrentlyReserved = checkForActiveReservations(vehicle);
        
        // Create card panel with rounded corners and hover effect
        JPanel cardPanel = new JPanel(new BorderLayout()) {
            private boolean isHovered = false;
            
            {
                // Add mouse listeners for hover effect
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background with hover effect
                if (isHovered) {
                    g2d.setColor(parentFrame.getFieldBgColor().brighter());
                } else {
                    g2d.setColor(parentFrame.getFieldBgColor());
                }
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                
                // Draw subtle border
                g2d.setColor(new Color(80, 70, 120));
                g2d.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(350, 320));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Create image panel with hover effect and border
        JPanel imagePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw subtle gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(40, 40, 60), 
                    0, getHeight(), new Color(30, 30, 50)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        imagePanel.setOpaque(false);
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true));
        imagePanel.setPreferredSize(new Dimension(320, 180));
        
        // Get appropriate icon based on vehicle type or ID
        ImageIcon vehicleIcon = null;
        
        // First try to load vehicle-specific image
        try {
            java.io.File imageFile = new java.io.File("images/" + vehicle.getVehicleId() + ".jpg");
            if (imageFile.exists()) {
                Image image = javax.imageio.ImageIO.read(imageFile);
                Image scaledImage = image.getScaledInstance(320, 180, Image.SCALE_SMOOTH);
                vehicleIcon = new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading vehicle-specific image: " + e.getMessage());
        }
        
        // If no vehicle-specific image, try to load type-specific image
        if (vehicleIcon == null) {
            try {
                java.io.File typeImageFile = new java.io.File("images/" + vehicle.getType().toString().toLowerCase() + ".jpg");
                if (typeImageFile.exists()) {
                    Image image = javax.imageio.ImageIO.read(typeImageFile);
                    Image scaledImage = image.getScaledInstance(320, 180, Image.SCALE_SMOOTH);
                    vehicleIcon = new ImageIcon(scaledImage);
                }
            } catch (Exception e) {
                System.err.println("Error loading type-specific image: " + e.getMessage());
            }
        }
        
        // If still no image, use generated icon based on vehicle type
        if (vehicleIcon == null) {
            switch (vehicle.getType()) {
                case SEDAN:
                    vehicleIcon = createVehicleIcon(Color.BLUE, "SEDAN");
                    break;
                case SUV:
                    vehicleIcon = createVehicleIcon(new Color(0, 150, 0), "SUV");
                    break;
                case VAN:
                    vehicleIcon = createVehicleIcon(new Color(150, 0, 150), "VAN");
                    break;
                case BUS:
                    vehicleIcon = createVehicleIcon(new Color(200, 100, 0), "BUS");
                    break;
                case TRUCK:
                    vehicleIcon = createVehicleIcon(new Color(150, 0, 0), "TRUCK");
                    break;
                default:
                    vehicleIcon = createVehicleIcon(Color.GRAY, vehicle.getType().toString());
            }
        }
        
        // Create vehicle image label
        JLabel imageLabel = new JLabel(vehicleIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Add availability status overlay
        if (!vehicle.isAvailable() || isCurrentlyReserved) {
            JPanel statusOverlay = new JPanel(new BorderLayout());
            statusOverlay.setOpaque(false);
            
            // Create semi-transparent overlay
            JPanel overlay = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw semi-transparent overlay
                    g2d.setColor(new Color(0, 0, 0, 150));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            overlay.setOpaque(false);
            
            // Create "Not Available" label
            JLabel notAvailableLabel = new JLabel("NOT AVAILABLE");
            notAvailableLabel.setFont(new Font("Arial", Font.BOLD, 24));
            notAvailableLabel.setForeground(new Color(255, 100, 100));
            notAvailableLabel.setHorizontalAlignment(JLabel.CENTER);
            
            statusOverlay.add(overlay, BorderLayout.CENTER);
            statusOverlay.add(notAvailableLabel, BorderLayout.CENTER);
            
            imagePanel.add(statusOverlay, BorderLayout.CENTER);
        }
        
        // Create info panel with details
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Vehicle name with larger font
        JLabel nameLabel = new JLabel(vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Create details panel with two columns
        JPanel detailsPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        detailsPanel.setOpaque(false);
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add vehicle details with icons
        addDetailWithIcon(detailsPanel, "Type:", vehicle.getType().toString(), "type");
        addDetailWithIcon(detailsPanel, "Capacity:", String.valueOf(vehicle.getCapacity()), "capacity");
        addDetailWithIcon(detailsPanel, "Fuel:", vehicle.getFuelType().toString(), "fuel");
        addDetailWithIcon(detailsPanel, "License:", vehicle.getLicensePlate(), "license");
        addDetailWithIcon(detailsPanel, "Location:", vehicle.getCurrentLocation(), "location");
        addDetailWithIcon(detailsPanel, "Mileage:", String.format("%.1f", vehicle.getMileage()), "mileage");
        
        // Add components to info panel
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(detailsPanel);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        // Create availability status indicator
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statusPanel.setOpaque(false);
        
        JPanel statusIndicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw glowing circle with color based on availability
                Color baseColor = vehicle.isAvailable() && !isCurrentlyReserved ? 
                    new Color(0, 200, 0) : new Color(200, 0, 0);
                
                // Draw outer glow
                for (int i = 5; i > 0; i--) {
                    g2d.setColor(new Color(
                        baseColor.getRed(), 
                        baseColor.getGreen(), 
                        baseColor.getBlue(), 
                        50 - i * 8
                    ));
                    g2d.fillOval(-i, -i, getWidth() + (i*2), getHeight() + (i*2));
                }
                
                // Draw main circle
                g2d.setColor(baseColor);
                g2d.fillOval(0, 0, getWidth(), getHeight());
                
                // Draw highlight
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillOval(3, 3, 4, 4);
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(12, 12);
            }
        };
        
        JLabel statusLabel = new JLabel(vehicle.isAvailable() && !isCurrentlyReserved ? 
            "Available" : "Not Available");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(vehicle.isAvailable() && !isCurrentlyReserved ? 
            new Color(100, 255, 100) : new Color(255, 100, 100));
        
        statusPanel.add(statusIndicator);
        statusPanel.add(statusLabel);
        
        // Create reserve button with animation effect
        JButton reserveButton = new JButton(vehicle.isAvailable() && !isCurrentlyReserved ? 
            "Reserve Now" : "Not Available") {
            private boolean isHovered = false;
            
            {
                // Add mouse listeners for hover effect
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (isEnabled()) {
                            isHovered = true;
                            repaint();
                        }
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background with hover effect
                if (!isEnabled()) {
                    g2d.setColor(new Color(150, 150, 150)); // Gray for disabled
                } else if (isHovered) {
                    g2d.setColor(parentFrame.getPrimaryAccentColor().brighter());
                } else if (getModel().isPressed()) {
                    g2d.setColor(parentFrame.getPrimaryAccentColor().darker());
                } else {
                    g2d.setColor(parentFrame.getPrimaryAccentColor());
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Draw text with shadow
                if (isHovered && isEnabled()) {
                    g2d.setColor(new Color(0, 0, 0, 50));
                    g2d.setFont(getFont());
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(getText());
                    int textHeight = fm.getHeight();
                    g2d.drawString(getText(), (getWidth() - textWidth) / 2 + 1, 
                                  (getHeight() - textHeight) / 2 + fm.getAscent() + 1);
                }
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                g2d.drawString(getText(), (getWidth() - textWidth) / 2, 
                              (getHeight() - textHeight) / 2 + fm.getAscent());
            }
        };
        reserveButton.setFont(new Font("Arial", Font.BOLD, 14));
        reserveButton.setForeground(Color.WHITE);
        reserveButton.setBackground(parentFrame.getPrimaryAccentColor());
        reserveButton.setFocusPainted(false);
        reserveButton.setBorderPainted(false);
        reserveButton.setContentAreaFilled(false);
        reserveButton.setPreferredSize(new Dimension(150, 40));
        reserveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reserveButton.setEnabled(vehicle.isAvailable() && !isCurrentlyReserved);
        
        // Add action listener to open reservation dialog
        reserveButton.addActionListener(e -> showReservationDialog(vehicle));
        
        buttonPanel.add(statusPanel);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(reserveButton);
        
        // Add components to card
        cardPanel.add(imagePanel, BorderLayout.NORTH);
        cardPanel.add(infoPanel, BorderLayout.CENTER);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add click listener to the entire card
        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showVehicleDetails(vehicle);
            }
        });
        
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

    // Add the missing methods
    private void addDetailWithIcon(JPanel panel, String label, String value, String iconType) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 14));
        labelComponent.setForeground(Color.WHITE);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 14));
        valueComponent.setForeground(new Color(200, 200, 200));
        
        panel.add(labelComponent);
        panel.add(valueComponent);
    }
    
    private void searchVehicles() {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.equals("search vehicles...")) {
            searchTerm = "";
        }
        
        final String term = searchTerm;
        
        if (term.isEmpty() && filterTypeComboBox.getSelectedIndex() == 0 && capacitySlider.getValue() == 1) {
            // If no search term and no filters, load all vehicles
            loadVehicles();
            return;
        }
        
        // Filter vehicles based on search term and filters
        List<Vehicle> filteredVehicles = currentVehicles.stream()
            .filter(vehicle ->
                (term.isEmpty() ||
                 vehicle.getMake().toLowerCase().contains(term) ||
                 vehicle.getModel().toLowerCase().contains(term) ||
                 vehicle.getLicensePlate().toLowerCase().contains(term) ||
                 String.valueOf(vehicle.getYear()).contains(term)) &&
                (filterTypeComboBox.getSelectedIndex() == 0 ||
                 vehicle.getType().toString().equals(filterTypeComboBox.getSelectedItem())) &&
                vehicle.getCapacity() >= capacitySlider.getValue()
            )
            .collect(Collectors.toList());
        
        displayVehicles(filteredVehicles);
        parentFrame.setStatusMessage("Found " + filteredVehicles.size() + " vehicles matching search criteria");
    }
    
    private void filterVehicles() {
        // This will apply both the search term and all filters
        searchVehicles();
    }

    // Add a method to validate image files to ensure they are actually images
    private boolean isValidImageFile(java.io.File file) {
        try {
            // Try to read the file as an image
            BufferedImage testImage = javax.imageio.ImageIO.read(file);
            // If the image is null, it's not a valid image file
            return testImage != null;
        } catch (Exception e) {
            System.err.println("Invalid image file: " + file.getPath() + " - " + e.getMessage());
            return false;
        }
    }

    // Modify the showVehicleDetails method to correctly handle vehicle images
    private void showVehicleDetails(Vehicle vehicle) {
        // Check if the vehicle is currently reserved
        boolean isCurrentlyReserved = checkForActiveReservations(vehicle);

        // Create a modern dialog for showing vehicle details
        JDialog dialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "Vehicle Details", true);
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
        
        // Get appropriate icon based on vehicle type or ID
        ImageIcon vehicleIcon = null;
        
        // First try to load vehicle-specific image
        try {
            java.io.File imageFile = new java.io.File("images/" + vehicle.getVehicleId() + ".jpg");
            if (imageFile.exists() && isValidImageFile(imageFile)) {
                Image image = javax.imageio.ImageIO.read(imageFile);
                Image scaledImage = image.getScaledInstance(320, 180, Image.SCALE_SMOOTH);
                vehicleIcon = new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading vehicle-specific image: " + e.getMessage());
        }
        
        // If no vehicle-specific image, try to load type-specific image
        if (vehicleIcon == null) {
            try {
                java.io.File typeImageFile = new java.io.File("images/" + vehicle.getType().toString().toLowerCase() + ".jpg");
                if (typeImageFile.exists() && isValidImageFile(typeImageFile)) {
                    Image image = javax.imageio.ImageIO.read(typeImageFile);
                    Image scaledImage = image.getScaledInstance(320, 180, Image.SCALE_SMOOTH);
                    vehicleIcon = new ImageIcon(scaledImage);
                }
            } catch (Exception e) {
                System.err.println("Error loading type-specific image: " + e.getMessage());
            }
        }
        
        // If still no image, use generated icon based on vehicle type
        if (vehicleIcon == null) {
            switch (vehicle.getType()) {
                case SEDAN:
                    vehicleIcon = createVehicleIcon(Color.BLUE, "SEDAN");
                    break;
                case SUV:
                    vehicleIcon = createVehicleIcon(new Color(0, 150, 0), "SUV");
                    break;
                case VAN:
                    vehicleIcon = createVehicleIcon(new Color(150, 0, 150), "VAN");
                    break;
                case BUS:
                    vehicleIcon = createVehicleIcon(new Color(200, 100, 0), "BUS");
                    break;
                case TRUCK:
                    vehicleIcon = createVehicleIcon(new Color(150, 0, 0), "TRUCK");
                    break;
                default:
                    vehicleIcon = createVehicleIcon(Color.GRAY, vehicle.getType().toString());
        }
    }
    
    // Create vehicle image label with larger icon
    JLabel imageLabel = new JLabel(vehicleIcon);
    imageLabel.setHorizontalAlignment(JLabel.CENTER);
    imageLabel.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true));
    
    JLabel titleLabel = new JLabel(vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel());
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    titleLabel.setForeground(Color.WHITE);
    
    headerPanel.add(imageLabel, BorderLayout.CENTER);
    headerPanel.add(titleLabel, BorderLayout.SOUTH);
    
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
    addDetailRow(detailsPanel, gbc, 9, "Current Location:", vehicle.getCurrentLocation());
    
    // Add availability status with colored indicator
    JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    statusPanel.setOpaque(false);
    statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
    
    JPanel statusIndicator = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw glowing circle with color based on availability
            Color baseColor = vehicle.isAvailable() && !isCurrentlyReserved ? 
                new Color(0, 200, 0) : new Color(200, 0, 0);
            
            // Draw outer glow
            for (int i = 5; i > 0; i--) {
                g2d.setColor(new Color(
                    baseColor.getRed(), 
                    baseColor.getGreen(), 
                    baseColor.getBlue(), 
                    50 - i * 8
                ));
                g2d.fillOval(-i, -i, getWidth() + (i*2), getHeight() + (i*2));
            }
            
            // Draw main circle
            g2d.setColor(baseColor);
            g2d.fillOval(0, 0, getWidth(), getHeight());
            
            // Draw highlight
            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.fillOval(3, 3, 4, 4);
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(12, 12);
        }
    };
    
    JLabel availabilityLabel = new JLabel(vehicle.isAvailable() && !isCurrentlyReserved ? 
        "Available" : "Not Available");
    availabilityLabel.setFont(new Font("Arial", Font.BOLD, 14));
    availabilityLabel.setForeground(vehicle.isAvailable() && !isCurrentlyReserved ? 
        new Color(100, 255, 100) : new Color(255, 100, 100));
    
    statusPanel.add(statusIndicator);
    statusPanel.add(availabilityLabel);
    
    // Create buttons panel
    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonsPanel.setOpaque(false);
    
    JButton reserveButton = createStyledButton("Reserve Now", parentFrame.getPrimaryAccentColor());
    reserveButton.setPreferredSize(new Dimension(150, 40));
    reserveButton.setEnabled(vehicle.isAvailable() && !isCurrentlyReserved);
    reserveButton.addActionListener(e -> {
        dialog.dispose();
        showReservationDialog(vehicle);
    });
    
    JButton closeButton = createStyledButton("Close", parentFrame.getSecondaryAccentColor());
    closeButton.addActionListener(e -> dialog.dispose());
    
    buttonsPanel.add(reserveButton);
    buttonsPanel.add(closeButton);
    
    // Add components to content panel
    contentPanel.add(headerPanel, BorderLayout.NORTH);
    contentPanel.add(detailsPanel, BorderLayout.CENTER);
    contentPanel.add(statusPanel, BorderLayout.AFTER_LAST_LINE);
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

private void startFadeInAnimation() {
    fadeInAlpha = 0.0f;
    fadeInTimer = new Timer(30, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            fadeInAlpha += 0.1f;
            if (fadeInAlpha >= 1.0f) {
                fadeInAlpha = 1.0f;
                fadeInTimer.stop();
            }
            repaint();
        }
    });
    fadeInTimer.start();
}
    
@Override
protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    // Draw background
    g2d.setColor(parentFrame.getBackgroundColor());
    g2d.fillRect(0, 0, getWidth(), getHeight());
    
    // Apply fade-in effect
    if (fadeInTimer != null && fadeInTimer.isRunning()) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeInAlpha));
    }
    
    g2d.dispose();
    super.paintComponent(g);
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
    
// Modern scrollbar UI
private class ModernScrollBarUI extends BasicScrollBarUI {
    private final Color thumbColor;
        
    public ModernScrollBarUI(Color color) {
        this.thumbColor = color;
    }
        
    @Override
    protected void configureScrollBarColors() {
        trackColor = new Color(40, 40, 60);
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
        
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
            
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
        // Draw rounded thumb
        g2d.setColor(thumbColor);
        g2d.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
            
        g2d.dispose();
    }
        
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
        // Draw track
        g2d.setColor(trackColor);
        g2d.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            
        g2d.dispose();
    }
}

// Add the showReservationDialog method
private void showReservationDialog(Vehicle vehicle) {
    // Check if vehicle is available before showing reservation dialog
    LocalDateTime now = LocalDateTime.now();
    boolean vehicleIsReserved = checkForActiveReservations(vehicle);
        
    if (!vehicle.isAvailable() || vehicleIsReserved) {
        JOptionPane.showMessageDialog(this,
            "This vehicle is currently not available for reservation.",
            "Vehicle Unavailable",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
        
    // Create and show the new formal reservation form
    VehicleReservationForm reservationForm = new VehicleReservationForm(
        (JFrame)SwingUtilities.getWindowAncestor(this),
        vehicle,
        parentFrame
    );
    reservationForm.setVisible(true);
}
}
