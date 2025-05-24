import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Reservation;
import model.User;
import model.Vehicle;

/**
* Enhanced dashboard panel with modern card-based UI
*/
public class DashboardPanel extends JPanel {
   private MainFrameInterface parentFrame;
   private JPanel statsPanel;
   private JPanel upcomingReservationsPanel;
   private JPanel availableVehiclesPanel;
   
   private JLabel totalVehiclesLabel;
   private JLabel availableVehiclesLabel;
   private JLabel reservedVehiclesLabel;
   private JLabel pendingReservationsLabel;
   
   private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
   
   // Vehicle type icons
   private ImageIcon sedanIcon;
   private ImageIcon suvIcon;
   private ImageIcon vanIcon;
   private ImageIcon busIcon;
   private ImageIcon truckIcon;

   public DashboardPanel(MainFrameInterface parentFrame) {
       this.parentFrame = parentFrame;
       setLayout(new BorderLayout());
       setBackground(parentFrame.getBackgroundColor());
       
       loadIcons();
       initComponents();
       loadDashboardData();
   }
   
   private void loadIcons() {
       // Load vehicle type icons - using placeholder colors for now
       // In a real application, these would be actual image files
       sedanIcon = createVehicleIcon(Color.BLUE, "SEDAN");
       suvIcon = createVehicleIcon(new Color(0, 150, 0), "SUV");
       vanIcon = createVehicleIcon(new Color(150, 0, 150), "VAN");
       busIcon = createVehicleIcon(new Color(200, 100, 0), "BUS");
       truckIcon = createVehicleIcon(new Color(150, 0, 0), "TRUCK");
   }
   
   // Updated createVehicleIcon method to match VehicleManagementPanel for consistency
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
       // Create main panel with padding
       JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
       mainPanel.setBackground(parentFrame.getBackgroundColor());
       mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
       
       // Create dashboard header
       JPanel headerPanel = new JPanel(new BorderLayout());
       headerPanel.setOpaque(false);
       
       JLabel titleLabel = new JLabel("Dashboard");
       titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
       titleLabel.setForeground(Color.WHITE);
       
       JButton refreshButton = createStyledButton("Refresh Dashboard", parentFrame.getPrimaryAccentColor());
       refreshButton.addActionListener(e -> loadDashboardData());
       
       headerPanel.add(titleLabel, BorderLayout.WEST);
       headerPanel.add(refreshButton, BorderLayout.EAST);
       
       // Create stats panel
       statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
       statsPanel.setOpaque(false);
       
       // Create stats cards
       JPanel totalVehiclesCard = createStatCard("Total Vehicles", "0", parentFrame.getSecondaryAccentColor());
       JPanel availableVehiclesCard = createStatCard("Available Vehicles", "0", parentFrame.getPrimaryAccentColor());
       JPanel reservedVehiclesCard = createStatCard("Reserved Vehicles", "0", parentFrame.getSecondaryAccentColor());
       JPanel pendingReservationsCard = createStatCard("Pending Reservations", "0", parentFrame.getPrimaryAccentColor());
       
       // Get stat value labels for updating
       totalVehiclesLabel = getStatValueLabel(totalVehiclesCard);
       availableVehiclesLabel = getStatValueLabel(availableVehiclesCard);
       reservedVehiclesLabel = getStatValueLabel(reservedVehiclesCard);
       pendingReservationsLabel = getStatValueLabel(pendingReservationsCard);
       
       // Add stats cards to panel
       statsPanel.add(totalVehiclesCard);
       statsPanel.add(availableVehiclesCard);
       statsPanel.add(reservedVehiclesCard);
       statsPanel.add(pendingReservationsCard);
       
       // Create content panel with reservations and vehicles
       JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
       contentPanel.setOpaque(false);
       
       // Create upcoming reservations panel
       upcomingReservationsPanel = createSectionPanel("Upcoming Reservations");
       
       // Create available vehicles panel
       availableVehiclesPanel = createSectionPanel("Available Vehicles");
       
       // Add panels to content panel
       contentPanel.add(upcomingReservationsPanel);
       contentPanel.add(availableVehiclesPanel);
       
       // Add components to main panel
       mainPanel.add(headerPanel, BorderLayout.NORTH);
       mainPanel.add(statsPanel, BorderLayout.CENTER);
       mainPanel.add(contentPanel, BorderLayout.SOUTH);
       
       // Add main panel to dashboard panel
       add(mainPanel, BorderLayout.CENTER);
   }
   
   private JPanel createStatCard(String title, String value, Color accentColor) {
       JPanel cardPanel = new JPanel(new BorderLayout()) {
           @Override
           protected void paintComponent(Graphics g) {
               Graphics2D g2d = (Graphics2D) g;
               g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               
               // Draw rounded rectangle background
               g2d.setColor(parentFrame.getFieldBgColor());
               g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
           }
       };
       cardPanel.setOpaque(false);
       cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
       
       JLabel titleLabel = new JLabel(title);
       titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
       titleLabel.setForeground(Color.WHITE);
       
       JLabel valueLabel = new JLabel(value);
       valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
       valueLabel.setForeground(accentColor);
       valueLabel.setHorizontalAlignment(JLabel.CENTER);
       
       cardPanel.add(titleLabel, BorderLayout.NORTH);
       cardPanel.add(valueLabel, BorderLayout.CENTER);
       
       return cardPanel;
   }
   
   private JLabel getStatValueLabel(JPanel statCard) {
       // Get the value label from the stat card (it's the center component)
       return (JLabel) ((BorderLayout) statCard.getLayout()).getLayoutComponent(BorderLayout.CENTER);
   }
   
   private JPanel createSectionPanel(String title) {
       JPanel sectionPanel = new JPanel(new BorderLayout()) {
           @Override
           protected void paintComponent(Graphics g) {
               Graphics2D g2d = (Graphics2D) g;
               g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               
               // Draw rounded rectangle background
               g2d.setColor(parentFrame.getFieldBgColor());
               g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
           }
       };
       sectionPanel.setOpaque(false);
       sectionPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
       sectionPanel.setPreferredSize(new Dimension(0, 400));
       
       JLabel titleLabel = new JLabel(title);
       titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
       titleLabel.setForeground(Color.WHITE);
       titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
       
       JPanel contentPanel = new JPanel();
       contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
       contentPanel.setOpaque(false);
       
       JScrollPane scrollPane = new JScrollPane(contentPanel);
       scrollPane.setBorder(null);
       scrollPane.setOpaque(false);
       scrollPane.getViewport().setOpaque(false);
       scrollPane.getVerticalScrollBar().setUnitIncrement(16);
       
       sectionPanel.add(titleLabel, BorderLayout.NORTH);
       sectionPanel.add(scrollPane, BorderLayout.CENTER);
       
       return sectionPanel;
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
       button.setPreferredSize(new Dimension(180, 40));
       button.setCursor(new Cursor(Cursor.HAND_CURSOR));
       
       return button;
   }
   
   public void loadDashboardData() {
       SwingWorker<Object[], Void> worker = new SwingWorker<>() {
           @Override
           protected Object[] doInBackground() {
               // Load vehicles data
               List<Vehicle> allVehicles = DataManager.getInstance().getAllVehicles();
               int totalVehicles = allVehicles.size();
               
               // Count available vehicles
               int availableVehicles = 0;
               for (Vehicle vehicle : allVehicles) {
                   if (vehicle.isAvailable()) {
                       availableVehicles++;
                   }
               }
               
               // Load reservations data
               List<Reservation> allReservations = DataManager.getInstance().getAllReservations();
               int reservedVehicles = 0;
               int pendingReservations = 0;
               
               LocalDateTime now = LocalDateTime.now();
               
               for (Reservation reservation : allReservations) {
                   if (reservation.getStatus() == Reservation.ReservationStatus.PENDING) {
                       pendingReservations++;
                   }
                   
                   if (reservation.getStatus() == Reservation.ReservationStatus.APPROVED &&
                       reservation.getStartDateTime().isAfter(now)) {
                       reservedVehicles++;
                   }
               }
               
               // Get available vehicles and upcoming reservations for display
               List<Vehicle> availableVehiclesList = DataManager.getInstance().getAvailableVehicles(now, now.plusDays(7));
               
               // Get upcoming reservations for current user or all if admin
               List<Reservation> upcomingReservations;
               User currentUser = DataManager.getInstance().getCurrentUser();
               if (currentUser.isAdmin()) {
                   upcomingReservations = allReservations.stream()
                       .filter(r -> r.getStatus() == Reservation.ReservationStatus.APPROVED && 
                               r.getStartDateTime().isAfter(now))
                       .sorted((r1, r2) -> r1.getStartDateTime().compareTo(r2.getStartDateTime()))
                       .limit(10)
                       .toList();
               } else {
                   upcomingReservations = allReservations.stream()
                       .filter(r -> r.getUserId().equals(currentUser.getUserId()) && 
                               r.getStatus() == Reservation.ReservationStatus.APPROVED && 
                               r.getStartDateTime().isAfter(now))
                       .sorted((r1, r2) -> r1.getStartDateTime().compareTo(r2.getStartDateTime()))
                       .limit(10)
                       .toList();
               }
               
               // Return all the data we need in the done method
               return new Object[] {
                   totalVehicles,
                   availableVehicles,
                   reservedVehicles,
                   pendingReservations,
                   upcomingReservations,
                   availableVehiclesList
               };
           }
           
           @Override
           protected void done() {
               try {
                   // Get the data from doInBackground
                   Object[] results = get();
                   int totalVehicles = (Integer) results[0];
                   int availableVehicles = (Integer) results[1];
                   int reservedVehicles = (Integer) results[2];
                   int pendingReservations = (Integer) results[3];
                   @SuppressWarnings("unchecked")
                   List<Reservation> upcomingReservations = (List<Reservation>) results[4];
                   @SuppressWarnings("unchecked")
                   List<Vehicle> availableVehiclesList = (List<Vehicle>) results[5];
                   
                   // Update stats labels
                   totalVehiclesLabel.setText(String.valueOf(totalVehicles));
                   availableVehiclesLabel.setText(String.valueOf(availableVehicles));
                   reservedVehiclesLabel.setText(String.valueOf(reservedVehicles));
                   pendingReservationsLabel.setText(String.valueOf(pendingReservations));
                   
                   // Update upcoming reservations panel
                   updateUpcomingReservationsPanel(upcomingReservations);
                   
                   // Update available vehicles panel
                   updateAvailableVehiclesPanel(availableVehiclesList);
                   
                   parentFrame.setStatusMessage("Dashboard updated: " + 
                       LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
       };
       
       worker.execute();
   }
   
   private void updateUpcomingReservationsPanel(List<Reservation> reservations) {
       // Get the content panel from the scroll pane
       JScrollPane scrollPane = (JScrollPane) upcomingReservationsPanel.getComponent(1);
       JPanel contentPanel = (JPanel) scrollPane.getViewport().getView();
       
       // Clear existing content
       contentPanel.removeAll();
       
       if (reservations.isEmpty()) {
           JLabel noReservationsLabel = new JLabel("No upcoming reservations");
           noReservationsLabel.setFont(new Font("Arial", Font.BOLD, 16));
           noReservationsLabel.setForeground(Color.WHITE);
           noReservationsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
           
           contentPanel.add(Box.createVerticalGlue());
           contentPanel.add(noReservationsLabel);
           contentPanel.add(Box.createVerticalGlue());
       } else {
           // Add reservation cards
           for (Reservation reservation : reservations) {
               contentPanel.add(createReservationCard(reservation));
               contentPanel.add(Box.createVerticalStrut(10));
           }
       }
       
       // Refresh UI
       contentPanel.revalidate();
       contentPanel.repaint();
   }
   
   private JPanel createReservationCard(Reservation reservation) {
       // Get vehicle and user info
       Vehicle vehicle = DataManager.getInstance().getVehicleById(reservation.getVehicleId());
       User user = DataManager.getInstance().getUserById(reservation.getUserId());
       
       String vehicleName = vehicle != null ? 
           vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel() : 
           "Unknown Vehicle";
           
       String userName = user != null ? user.getFullName() : "Unknown User";
       
       // Create card panel with rounded corners and improved design
       JPanel cardPanel = new JPanel(new BorderLayout(10, 10)) {
           @Override
           protected void paintComponent(Graphics g) {
               Graphics2D g2d = (Graphics2D) g;
               g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               
               // Draw rounded rectangle background
               g2d.setColor(parentFrame.getBackgroundColor().brighter());
               g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
           }
       };
       cardPanel.setOpaque(false);
       cardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
       
       // Get appropriate icon based on vehicle ID or type
       ImageIcon vehicleIcon;
       try {
           // Try to load vehicle-specific image first
           if (vehicle != null) {
               java.io.File imageFile = new java.io.File("images/" + vehicle.getVehicleId() + ".jpg");
               if (imageFile.exists()) {
                   Image image = javax.imageio.ImageIO.read(imageFile);
                   // Scale the image to appropriate size - landscape orientation
                   Image scaledImage = image.getScaledInstance(160, 90, Image.SCALE_SMOOTH);
                   vehicleIcon = new ImageIcon(scaledImage);
               } else {
                   // Fall back to type-based icon
                   if (vehicle != null) {
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
                   } else {
                       vehicleIcon = sedanIcon; // Default fallback
                   }
               }
           } else {
               vehicleIcon = sedanIcon; // Default fallback
           }
       } catch (Exception e) {
           // Fall back to type-based icon on error
           if (vehicle != null) {
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
           } else {
               vehicleIcon = sedanIcon; // Default fallback
           }
       }
       
       // Create image panel with border
       JPanel imagePanel = new JPanel(new BorderLayout());
       imagePanel.setOpaque(false);
       imagePanel.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true));
       imagePanel.setPreferredSize(new Dimension(160, 90));
       
       // Create vehicle image label
       JLabel imageLabel = new JLabel(vehicleIcon);
       imageLabel.setHorizontalAlignment(JLabel.CENTER);
       imagePanel.add(imageLabel, BorderLayout.CENTER);
       
       // Create info panel
       JPanel infoPanel = new JPanel();
       infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
       infoPanel.setOpaque(false);
       
       // Add reservation details
       JLabel vehicleLabel = new JLabel(vehicleName);
       vehicleLabel.setFont(new Font("Arial", Font.BOLD, 14));
       vehicleLabel.setForeground(Color.WHITE);
       
       JLabel userLabel = new JLabel("Reserved by: " + userName);
       userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
       userLabel.setForeground(new Color(200, 200, 200));
       
       JLabel dateLabel = new JLabel(
           "From: " + reservation.getStartDateTime().format(DATE_FORMATTER) + 
           " To: " + reservation.getEndDateTime().format(DATE_FORMATTER)
       );
       dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
       dateLabel.setForeground(new Color(200, 200, 200));
       
       JLabel statusLabel = new JLabel("Status: " + reservation.getStatus());
       statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
       statusLabel.setForeground(
           reservation.getStatus() == Reservation.ReservationStatus.APPROVED ? 
           new Color(0, 200, 0) : new Color(200, 200, 0)
       );
       
       infoPanel.add(vehicleLabel);
       infoPanel.add(Box.createVerticalStrut(5));
       infoPanel.add(userLabel);
       infoPanel.add(Box.createVerticalStrut(5));
       infoPanel.add(dateLabel);
       infoPanel.add(Box.createVerticalStrut(5));
       infoPanel.add(statusLabel);
       
       // Add components to card
       cardPanel.add(imagePanel, BorderLayout.WEST);
       cardPanel.add(infoPanel, BorderLayout.CENTER);
       
       return cardPanel;
   }
   
   private void updateAvailableVehiclesPanel(List<Vehicle> vehicles) {
       // Get the content panel from the scroll pane
       JScrollPane scrollPane = (JScrollPane) availableVehiclesPanel.getComponent(1);
       JPanel contentPanel = (JPanel) scrollPane.getViewport().getView();
       
       // Clear existing content
       contentPanel.removeAll();
       
       if (vehicles.isEmpty()) {
           JLabel noVehiclesLabel = new JLabel("No available vehicles");
           noVehiclesLabel.setFont(new Font("Arial", Font.BOLD, 16));
           noVehiclesLabel.setForeground(Color.WHITE);
           noVehiclesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
           
           contentPanel.add(Box.createVerticalGlue());
           contentPanel.add(noVehiclesLabel);
           contentPanel.add(Box.createVerticalGlue());
       } else {
           // Add vehicle cards
           for (Vehicle vehicle : vehicles) {
               contentPanel.add(createVehicleCard(vehicle));
               contentPanel.add(Box.createVerticalStrut(10));
           }
       }
       
       // Refresh UI
       contentPanel.revalidate();
       contentPanel.repaint();
   }
   
   private JPanel createVehicleCard(Vehicle vehicle) {
       // Create card panel with rounded corners and improved design
       JPanel cardPanel = new JPanel(new BorderLayout(10, 10)) {
           @Override
           protected void paintComponent(Graphics g) {
               Graphics2D g2d = (Graphics2D) g;
               g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               
               // Draw rounded rectangle background
               g2d.setColor(parentFrame.getBackgroundColor().brighter());
               g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
           }
       };
       cardPanel.setOpaque(false);
       cardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
       
       // Get appropriate icon based on vehicle ID or type
       ImageIcon vehicleIcon;
       try {
           // Try to load vehicle-specific image first
           java.io.File imageFile = new java.io.File("images/" + vehicle.getVehicleId() + ".jpg");
           if (imageFile.exists()) {
               Image image = javax.imageio.ImageIO.read(imageFile);
               // Scale the image to appropriate size - landscape orientation
               Image scaledImage = image.getScaledInstance(160, 90, Image.SCALE_SMOOTH);
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
       }
       
       // Create image panel with border
       JPanel imagePanel = new JPanel(new BorderLayout());
       imagePanel.setOpaque(false);
       imagePanel.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true));
       imagePanel.setPreferredSize(new Dimension(160, 90));
       
       // Create vehicle image label
       JLabel imageLabel = new JLabel(vehicleIcon);
       imageLabel.setHorizontalAlignment(JLabel.CENTER);
       imagePanel.add(imageLabel, BorderLayout.CENTER);
       
       // Create info panel
       JPanel infoPanel = new JPanel();
       infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
       infoPanel.setOpaque(false);
       
       // Add vehicle details
       JLabel nameLabel = new JLabel(vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel());
       nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
       nameLabel.setForeground(Color.WHITE);
       
       JLabel typeLabel = new JLabel("Type: " + vehicle.getType());
       typeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
       typeLabel.setForeground(new Color(200, 200, 200));
       
       JLabel capacityLabel = new JLabel("Capacity: " + vehicle.getCapacity() + " | Fuel: " + vehicle.getFuelType());
       capacityLabel.setFont(new Font("Arial", Font.PLAIN, 12));
       capacityLabel.setForeground(new Color(200, 200, 200));
       
       JLabel licensePlateLabel = new JLabel("License Plate: " + vehicle.getLicensePlate());
       licensePlateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
       licensePlateLabel.setForeground(new Color(200, 200, 200));
       
       infoPanel.add(nameLabel);
       infoPanel.add(Box.createVerticalStrut(5));
       infoPanel.add(typeLabel);
       infoPanel.add(Box.createVerticalStrut(5));
       infoPanel.add(capacityLabel);
       infoPanel.add(Box.createVerticalStrut(5));
       infoPanel.add(licensePlateLabel);
       
       // Create button panel
       JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
       buttonPanel.setOpaque(false);
       
       JButton reserveButton = new JButton("Reserve") {
           @Override
           protected void paintComponent(Graphics g) {
               Graphics2D g2d = (Graphics2D) g;
               g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               
               // Draw rounded rectangle background
               g2d.setColor(getModel().isPressed() ? 
                   parentFrame.getPrimaryAccentColor().darker() : 
                   parentFrame.getPrimaryAccentColor());
               g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
               
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
       reserveButton.setFont(new Font("Arial", Font.BOLD, 12));
       reserveButton.setForeground(Color.WHITE);
       reserveButton.setBackground(parentFrame.getPrimaryAccentColor());
       reserveButton.setFocusPainted(false);
       reserveButton.setBorderPainted(false);
       reserveButton.setContentAreaFilled(false);
       reserveButton.setPreferredSize(new Dimension(100, 30));
       reserveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
       
       // Add action listener to open reservation dialog
       reserveButton.addActionListener(e -> {
           // This would open a reservation dialog in a real application
           JOptionPane.showMessageDialog(this,
               "Reservation functionality would be implemented here.",
               "Reserve Vehicle",
               JOptionPane.INFORMATION_MESSAGE);
       });
       
       buttonPanel.add(reserveButton);
       
       // Add components to card
       cardPanel.add(imagePanel, BorderLayout.WEST);
       cardPanel.add(infoPanel, BorderLayout.CENTER);
       cardPanel.add(buttonPanel, BorderLayout.EAST);
       
       return cardPanel;
   }
}
