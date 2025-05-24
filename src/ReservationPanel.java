import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import model.Reservation;
import model.User;
import model.Vehicle;

/**
* Enhanced panel for managing reservations with modern card-based UI
*/
public class ReservationPanel extends JPanel {
   private MainFrameInterface parentFrame;
   private JPanel reservationsContainer;
   private JScrollPane scrollPane;
   private JComboBox<String> statusFilterComboBox;
   private JButton addButton;
   private JButton refreshButton;
   
   // Reservation cards currently displayed
   private List<Reservation> currentReservations;
   
   // Vehicle type icons
   private ImageIcon sedanIcon;
   private ImageIcon suvIcon;
   private ImageIcon vanIcon;
   private ImageIcon busIcon;
   private ImageIcon truckIcon;
   
   private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
   
   public ReservationPanel(MainFrameInterface parentFrame) {
       this.parentFrame = parentFrame;
       setLayout(new BorderLayout());
       setBackground(parentFrame.getBackgroundColor());
       
       loadIcons();
       initComponents();
       loadReservations();
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
       // Create header panel with title and filters
       JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
       headerPanel.setBackground(parentFrame.getBackgroundColor());
       headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
       
       // Title label
       JLabel titleLabel = new JLabel("Reservation Management");
       titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
       titleLabel.setForeground(Color.WHITE);
       
       // Filter panel
       JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
       filterPanel.setOpaque(false);
       
       JLabel filterLabel = new JLabel("Filter by Status:");
       filterLabel.setFont(new Font("Arial", Font.PLAIN, 14));
       filterLabel.setForeground(Color.WHITE);
       
       String[] statusOptions = {"All", "Pending", "Approved", "Rejected", "Cancelled", "Completed"};
       statusFilterComboBox = new JComboBox<>(statusOptions);
       statusFilterComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
       statusFilterComboBox.setBackground(parentFrame.getFieldBgColor());
       statusFilterComboBox.setForeground(Color.WHITE);
       statusFilterComboBox.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true));
       statusFilterComboBox.addActionListener(e -> filterReservations());
       
       filterPanel.add(filterLabel);
       filterPanel.add(statusFilterComboBox);
       
       // Add title and filter panel to header
       headerPanel.add(titleLabel, BorderLayout.WEST);
       headerPanel.add(filterPanel, BorderLayout.EAST);
       
       // Create reservations container with vertical layout
       reservationsContainer = new JPanel();
       reservationsContainer.setLayout(new BoxLayout(reservationsContainer, BoxLayout.Y_AXIS));
       reservationsContainer.setBackground(parentFrame.getBackgroundColor());
       reservationsContainer.setBorder(new EmptyBorder(0, 15, 0, 15));
       
       // Add scroll pane for reservations container
       scrollPane = new JScrollPane(reservationsContainer);
       scrollPane.setBorder(null);
       scrollPane.getVerticalScrollBar().setUnitIncrement(16);
       scrollPane.getViewport().setBackground(parentFrame.getBackgroundColor());
       
       // Create button panel
       JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
       buttonPanel.setOpaque(false);
       
       addButton = createStyledButton("New Reservation", parentFrame.getPrimaryAccentColor());
       refreshButton = createStyledButton("Refresh", parentFrame.getSecondaryAccentColor());
       
       buttonPanel.add(addButton);
       buttonPanel.add(refreshButton);
       
       // Add action listeners
       addButton.addActionListener(e -> showAddReservationDialog());
       refreshButton.addActionListener(e -> loadReservations());
       
       // Add components to main panel
       add(headerPanel, BorderLayout.NORTH);
       add(scrollPane, BorderLayout.CENTER);
       add(buttonPanel, BorderLayout.SOUTH);
       
       // Set access control based on user role
       setupAccessControl();
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
       button.setPreferredSize(new Dimension(150, 40));
       button.setCursor(new Cursor(Cursor.HAND_CURSOR));
       
       return button;
   }
   
   private void setupAccessControl() {
       User currentUser = DataManager.getInstance().getCurrentUser();
       boolean isAdmin = currentUser.isAdmin();
       
       // Only admins can see all reservations
       if (!isAdmin) {
           statusFilterComboBox.setEnabled(false);
       }
   }
   
   public void loadReservations() {
       SwingWorker<List<Reservation>, Void> worker = new SwingWorker<>() {
           @Override
           protected List<Reservation> doInBackground() {
               // Get filter value
               String statusFilter = (String) statusFilterComboBox.getSelectedItem();
               
               // Load reservations
               List<Reservation> reservations;
               User currentUser = DataManager.getInstance().getCurrentUser();
               
               if (currentUser.isAdmin()) {
                   reservations = DataManager.getInstance().getAllReservations();
               } else {
                   reservations = DataManager.getInstance().getUserReservations(currentUser.getUserId());
               }
               
               // Apply status filter if not "All"
               if (!statusFilter.equals("All")) {
                   Reservation.ReservationStatus status = Reservation.ReservationStatus.valueOf(statusFilter.toUpperCase());
                   reservations = reservations.stream()
                       .filter(r -> r.getStatus() == status)
                       .toList();
               }
               
               return reservations;
           }
           
           @Override
           protected void done() {
               try {
                   currentReservations = get();
                   displayReservations(currentReservations);
                   parentFrame.setStatusMessage("Reservations loaded successfully");
               } catch (Exception e) {
                   e.printStackTrace();
                   JOptionPane.showMessageDialog(ReservationPanel.this,
                       "Error loading reservations: " + e.getMessage(),
                       "Error",
                       JOptionPane.ERROR_MESSAGE);
               }
           }
       };
       
       worker.execute();
   }
   
   private void displayReservations(List<Reservation> reservations) {
       // Clear existing reservation cards
       reservationsContainer.removeAll();
       
       if (reservations.isEmpty()) {
           JLabel noReservationsLabel = new JLabel("No reservations found");
           noReservationsLabel.setFont(new Font("Arial", Font.BOLD, 18));
           noReservationsLabel.setForeground(Color.WHITE);
           noReservationsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
           
           JPanel centerPanel = new JPanel(new GridBagLayout());
           centerPanel.setOpaque(false);
           centerPanel.add(noReservationsLabel);
           
           reservationsContainer.add(Box.createVerticalStrut(50));
           reservationsContainer.add(centerPanel);
       } else {
           // Add reservation cards
           for (Reservation reservation : reservations) {
               reservationsContainer.add(createReservationCard(reservation));
               reservationsContainer.add(Box.createVerticalStrut(15));
           }
       }
       
       // Refresh UI
       reservationsContainer.revalidate();
       reservationsContainer.repaint();
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
       cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
       
       // Create left panel with vehicle info and image
       JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
       leftPanel.setOpaque(false);
       leftPanel.setPreferredSize(new Dimension(300, 0));
       
       // Get appropriate icon based on vehicle ID or type
       ImageIcon vehicleIcon;
       try {
           // Try to load vehicle-specific image first
           if (vehicle != null) {
               java.io.File imageFile = new java.io.File("images/" + vehicle.getVehicleId() + ".jpg");
               if (imageFile.exists()) {
                   Image image = javax.imageio.ImageIO.read(imageFile);
                   // Scale the image to appropriate size - landscape orientation
                   Image scaledImage = image.getScaledInstance(200, 120, Image.SCALE_SMOOTH);
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
       
       // Create vehicle image panel with border
       JPanel imagePanel = new JPanel(new BorderLayout());
       imagePanel.setOpaque(false);
       imagePanel.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true));
       imagePanel.setPreferredSize(new Dimension(200, 120));
       
       // Create vehicle image label
       JLabel imageLabel = new JLabel(vehicleIcon);
       imageLabel.setHorizontalAlignment(JLabel.CENTER);
       imagePanel.add(imageLabel, BorderLayout.CENTER);
       
       // Create vehicle info panel
       JPanel vehicleInfoPanel = new JPanel();
       vehicleInfoPanel.setLayout(new BoxLayout(vehicleInfoPanel, BoxLayout.Y_AXIS));
       vehicleInfoPanel.setOpaque(false);
       vehicleInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
       
       JLabel vehicleNameLabel = new JLabel(vehicleName);
       vehicleNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
       vehicleNameLabel.setForeground(Color.WHITE);
       
       JLabel vehicleTypeLabel = new JLabel(vehicle != null ? vehicle.getType().toString() : "Unknown Type");
       vehicleTypeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
       vehicleTypeLabel.setForeground(new Color(200, 200, 200));
       
       JLabel licensePlateLabel = new JLabel("License: " + (vehicle != null ? vehicle.getLicensePlate() : "N/A"));
       licensePlateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
       licensePlateLabel.setForeground(new Color(200, 200, 200));
       
       vehicleInfoPanel.add(vehicleNameLabel);
       vehicleInfoPanel.add(Box.createVerticalStrut(5));
       vehicleInfoPanel.add(vehicleTypeLabel);
       vehicleInfoPanel.add(Box.createVerticalStrut(5));
       vehicleInfoPanel.add(licensePlateLabel);
       
       // Add image and info to left panel
       leftPanel.add(imagePanel, BorderLayout.WEST);
       leftPanel.add(vehicleInfoPanel, BorderLayout.CENTER);
       
       // Create center panel with reservation details
       JPanel centerPanel = new JPanel(new GridLayout(4, 1, 0, 5));
       centerPanel.setOpaque(false);
       
       JLabel userLabel = new JLabel("Reserved by: " + userName);
       userLabel.setFont(new Font("Arial", Font.BOLD, 14));
       userLabel.setForeground(Color.WHITE);
       
       JLabel dateRangeLabel = new JLabel("From: " + reservation.getStartDateTime().format(DATE_FORMATTER));
       dateRangeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
       dateRangeLabel.setForeground(new Color(200, 200, 200));
       
       JLabel endDateLabel = new JLabel("To: " + reservation.getEndDateTime().format(DATE_FORMATTER));
       endDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
       endDateLabel.setForeground(new Color(200, 200, 200));
       
       JLabel purposeLabel = new JLabel("Purpose: " + reservation.getPurpose());
       purposeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
       purposeLabel.setForeground(new Color(200, 200, 200));
       
       centerPanel.add(userLabel);
       centerPanel.add(dateRangeLabel);
       centerPanel.add(endDateLabel);
       centerPanel.add(purposeLabel);
       
       // Create right panel with status and actions
       JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
       rightPanel.setOpaque(false);
       rightPanel.setPreferredSize(new Dimension(150, 0));
       
       // Create status panel
       JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
       statusPanel.setOpaque(false);
       
       // Create status indicator
       JPanel statusIndicator = new JPanel() {
           @Override
           protected void paintComponent(Graphics g) {
               Graphics2D g2d = (Graphics2D) g;
               g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               
               // Draw circle with color based on status
               Color statusColor;
               switch (reservation.getStatus()) {
                   case APPROVED:
                       statusColor = new Color(0, 200, 0); // Green
                       break;
                   case PENDING:
                       statusColor = new Color(255, 180, 0); // Orange
                       break;
                   case REJECTED:
                       statusColor = new Color(200, 0, 0); // Red
                       break;
                   case CANCELLED:
                       statusColor = new Color(150, 150, 150); // Gray
                       break;
                   case COMPLETED:
                       statusColor = new Color(0, 150, 200); // Blue
                       break;
                   default:
                       statusColor = new Color(150, 150, 150); // Gray
               }
               
               g2d.setColor(statusColor);
               g2d.fillOval(0, 0, getWidth(), getHeight());
           }
       };
       statusIndicator.setPreferredSize(new Dimension(15, 15));
       statusIndicator.setOpaque(false);
       
       JLabel statusLabel = new JLabel(reservation.getStatus().toString());
       statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
       
       // Set status label color based on status
       Color textColor;
       switch (reservation.getStatus()) {
           case APPROVED:
               textColor = new Color(0, 200, 0); // Green
               break;
           case PENDING:
               textColor = new Color(255, 180, 0); // Orange
               break;
           case REJECTED:
               textColor = new Color(200, 0, 0); // Red
               break;
           case CANCELLED:
               textColor = new Color(150, 150, 150); // Gray
               break;
           case COMPLETED:
               textColor = new Color(0, 150, 200); // Blue
               break;
           default:
               textColor = new Color(150, 150, 150); // Gray
       }
       statusLabel.setForeground(textColor);
       
       statusPanel.add(statusIndicator);
       statusPanel.add(statusLabel);
       
       // Create actions panel
       JPanel actionsPanel = new JPanel();
       actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
       actionsPanel.setOpaque(false);
       
       // Create action buttons based on status and user role
       User currentUser = DataManager.getInstance().getCurrentUser();
       boolean isAdmin = currentUser.isAdmin();
       boolean isOwner = reservation.getUserId().equals(currentUser.getUserId());
       
       if (isAdmin && reservation.getStatus() == Reservation.ReservationStatus.PENDING) {
           // Admin can approve or reject pending reservations
           JButton approveButton = createActionButton("Approve", new Color(0, 200, 0));
           JButton rejectButton = createActionButton("Reject", new Color(200, 0, 0));
           
           approveButton.addActionListener(e -> approveReservation(reservation));
           rejectButton.addActionListener(e -> rejectReservation(reservation));
           
           actionsPanel.add(approveButton);
           actionsPanel.add(Box.createVerticalStrut(5));
           actionsPanel.add(rejectButton);
       } else if (isAdmin && reservation.getStatus() == Reservation.ReservationStatus.APPROVED) {
           // Admin can mark approved reservations as completed
           JButton completeButton = createActionButton("Complete", new Color(0, 150, 200));
           completeButton.addActionListener(e -> completeReservation(reservation));
           
           actionsPanel.add(completeButton);
       } else if ((isAdmin || isOwner) && 
                 (reservation.getStatus() == Reservation.ReservationStatus.PENDING || 
                  reservation.getStatus() == Reservation.ReservationStatus.APPROVED)) {
           // Admin or owner can cancel pending or approved reservations
           JButton cancelButton = createActionButton("Cancel", new Color(150, 150, 150));
           cancelButton.addActionListener(e -> cancelReservation(reservation));
           
           actionsPanel.add(cancelButton);
       }
       
       // Add view details button for all reservations
       JButton detailsButton = createActionButton("Details", parentFrame.getSecondaryAccentColor());
       detailsButton.addActionListener(e -> showReservationDetails(reservation));

       // Add remove button for admins
       if (DataManager.getInstance().getCurrentUser().isAdmin()) {
           JButton removeButton = createActionButton("Remove", new Color(220, 53, 69));
           removeButton.addActionListener(e -> removeReservation(reservation));
           
           actionsPanel.add(Box.createVerticalStrut(5));
           actionsPanel.add(removeButton);
       }

       actionsPanel.add(Box.createVerticalStrut(5));
       actionsPanel.add(detailsButton);
       
       // Add status and actions to right panel
       rightPanel.add(statusPanel, BorderLayout.NORTH);
       rightPanel.add(actionsPanel, BorderLayout.CENTER);
       
       // Add panels to card
       cardPanel.add(leftPanel, BorderLayout.WEST);
       cardPanel.add(centerPanel, BorderLayout.CENTER);
       cardPanel.add(rightPanel, BorderLayout.EAST);
       
       return cardPanel;
   }
   
   private JButton createActionButton(String text, Color color) {
       JButton button = new JButton(text) {
           @Override
           protected void paintComponent(Graphics g) {
               Graphics2D g2d = (Graphics2D) g;
               g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               
               // Draw rounded rectangle background
               g2d.setColor(getModel().isPressed() ? color.darker() : color);
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
       
       button.setFont(new Font("Arial", Font.BOLD, 12));
       button.setForeground(Color.WHITE);
       button.setBackground(color);
       button.setFocusPainted(false);
       button.setBorderPainted(false);
       button.setContentAreaFilled(false);
       button.setPreferredSize(new Dimension(100, 30));
       button.setMaximumSize(new Dimension(100, 30));
       button.setAlignmentX(Component.CENTER_ALIGNMENT);
       button.setCursor(new Cursor(Cursor.HAND_CURSOR));
       
       return button;
   }
   
   private void filterReservations() {
       loadReservations();
   }
   
   private void showAddReservationDialog() {
       // Create a modern dialog for adding a new reservation
       JDialog dialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "New Reservation", true);
       dialog.setSize(600, 550);
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
       
       // Get available vehicles
       List<Vehicle> availableVehicles = DataManager.getInstance().getAllVehicles();
       
       // Create form fields with modern styling
       JComboBox<Vehicle> vehicleComboBox = new JComboBox<>(availableVehicles.toArray(new Vehicle[0]));
       styleComboBox(vehicleComboBox);
       
       // Date pickers
       JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
       JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "MM/dd/yyyy HH:mm");
       startDateSpinner.setEditor(startDateEditor);
       styleSpinner(startDateSpinner);
       
       JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
       JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "MM/dd/yyyy HH:mm");
       endDateSpinner.setEditor(endDateEditor);
       styleSpinner(endDateSpinner);
       
       JTextField purposeField = createStyledTextField("");
       JTextField destinationField = createStyledTextField("");
       JSpinner passengersSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
       styleSpinner(passengersSpinner);
       
       JTextArea notesArea = new JTextArea(3, 20);
       notesArea.setFont(new Font("Arial", Font.PLAIN, 14));
       notesArea.setForeground(Color.WHITE);
       notesArea.setBackground(parentFrame.getBackgroundColor().brighter());
       notesArea.setLineWrap(true);
       notesArea.setWrapStyleWord(true);
       notesArea.setBorder(BorderFactory.createCompoundBorder(
           BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
           BorderFactory.createEmptyBorder(8, 10, 8, 10)
       ));
       notesArea.setCaretColor(Color.WHITE);
       
       JScrollPane notesScrollPane = new JScrollPane(notesArea);
       notesScrollPane.setBorder(null);
       
       // Add form fields to panel
       addFormField(formPanel, gbc, 0, "Vehicle:", vehicleComboBox);
       addFormField(formPanel, gbc, 1, "Start Date/Time:", startDateSpinner);
       addFormField(formPanel, gbc, 2, "End Date/Time:", endDateSpinner);
       addFormField(formPanel, gbc, 3, "Purpose:", purposeField);
       addFormField(formPanel, gbc, 4, "Destination:", destinationField);
       addFormField(formPanel, gbc, 5, "Passengers:", passengersSpinner);
       
       gbc.gridx = 0;
       gbc.gridy = 6;
       gbc.gridwidth = 1;
       JLabel notesLabel = new JLabel("Notes:");
       notesLabel.setFont(new Font("Arial", Font.BOLD, 14));
       notesLabel.setForeground(Color.WHITE);
       formPanel.add(notesLabel, gbc);
       
       gbc.gridx = 1;
       gbc.gridy = 6;
       gbc.gridwidth = 1;
       gbc.gridheight = 2;
       gbc.fill = GridBagConstraints.BOTH;
       formPanel.add(notesScrollPane, gbc);
       
       // Create buttons panel
       JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
       buttonsPanel.setOpaque(false);
       
       JButton saveButton = createStyledButton("Save", parentFrame.getPrimaryAccentColor());
       JButton cancelButton = createStyledButton("Cancel", new Color(150, 150, 150));
       
       // Add action listeners
       saveButton.addActionListener(e -> {
           // Validate input
           if (vehicleComboBox.getSelectedItem() == null ||
               purposeField.getText().trim().isEmpty() ||
               destinationField.getText().trim().isEmpty()) {
               JOptionPane.showMessageDialog(dialog,
                   "Please fill in all required fields.",
                   "Validation Error",
                   JOptionPane.ERROR_MESSAGE);
               return;
           }
           
           // Get selected vehicle
           Vehicle selectedVehicle = (Vehicle) vehicleComboBox.getSelectedItem();
           
           // Convert spinner dates to LocalDateTime
           java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
           java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();
           
           LocalDateTime startDateTime = startDate.toInstant()
               .atZone(java.time.ZoneId.systemDefault())
               .toLocalDateTime();
               
           LocalDateTime endDateTime = endDate.toInstant()
               .atZone(java.time.ZoneId.systemDefault())
               .toLocalDateTime();
           
           // Validate dates
           if (startDateTime.isAfter(endDateTime)) {
               JOptionPane.showMessageDialog(dialog,
                   "Start date must be before end date.",
                   "Validation Error",
                   JOptionPane.ERROR_MESSAGE);
               return;
           }
           
           if (startDateTime.isBefore(LocalDateTime.now())) {
               JOptionPane.showMessageDialog(dialog,
                   "Start date must be in the future.",
                   "Validation Error",
                   JOptionPane.ERROR_MESSAGE);
               return;
           }
           
           // Create new reservation
           String reservationId = "R-" + UUID.randomUUID().toString().substring(0, 8);
           Reservation reservation = new Reservation(
               reservationId,
               DataManager.getInstance().getCurrentUser().getUserId(),
               selectedVehicle.getVehicleId(),
               startDateTime,
               endDateTime,
               purposeField.getText().trim(),
               destinationField.getText().trim(),
               (Integer) passengersSpinner.getValue()
           );
           
           reservation.setNotes(notesArea.getText().trim());
           
           // Save reservation
           DataManager.getInstance().addReservation(reservation);
           
           // Close dialog and refresh
           dialog.dispose();
           loadReservations();
       });
       
       cancelButton.addActionListener(e -> dialog.dispose());
       
       buttonsPanel.add(saveButton);
       buttonsPanel.add(cancelButton);
       
       // Add components to content panel
       contentPanel.add(new JLabel("New Reservation", JLabel.CENTER) {{
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
       gbc.fill = GridBagConstraints.HORIZONTAL;
       gbc.weightx = 0.0;
       
       JLabel label = new JLabel(labelText);
       label.setFont(new Font("Arial", Font.BOLD, 14));
       label.setForeground(Color.WHITE);
       panel.add(label, gbc);
       
       gbc.gridx = 1;
       gbc.weightx = 1.0;
       panel.add(field, gbc);
   }
   
   private void approveReservation(Reservation reservation) {
       // Update reservation status
       reservation.setStatus(Reservation.ReservationStatus.APPROVED);
       reservation.setApprovedBy(DataManager.getInstance().getCurrentUser().getUserId());
       reservation.setApprovalDateTime(LocalDateTime.now());
       
       // Save reservation
       DataManager.getInstance().updateReservation(reservation);
       
       // Refresh list
       loadReservations();
   }
   
   private void rejectReservation(Reservation reservation) {
       // Show dialog to get rejection reason
       String reason = JOptionPane.showInputDialog(this,
           "Please provide a reason for rejection:",
           "Rejection Reason",
           JOptionPane.QUESTION_MESSAGE);
           
       if (reason == null) {
           return; // User cancelled
       }
       
       // Update reservation
       reservation.setStatus(Reservation.ReservationStatus.REJECTED);
       reservation.setNotes(reservation.getNotes() + "\n\nRejection Reason: " + reason);
       
       // Save reservation
       DataManager.getInstance().updateReservation(reservation);
       
       // Refresh list
       loadReservations();
   }
   
   private void cancelReservation(Reservation reservation) {
       int choice = JOptionPane.showConfirmDialog(this,
           "Are you sure you want to cancel this reservation?",
           "Confirm Cancellation",
           JOptionPane.YES_NO_OPTION,
           JOptionPane.WARNING_MESSAGE);
           
       if (choice == JOptionPane.YES_OPTION) {
           // Update reservation
           reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
           
           // Save reservation
           DataManager.getInstance().updateReservation(reservation);
           
           // Refresh list
           loadReservations();
       }
   }
   
   private void completeReservation(Reservation reservation) {
       // Get vehicle
       Vehicle vehicle = DataManager.getInstance().getVehicleById(reservation.getVehicleId());
       if (vehicle == null) {
           JOptionPane.showMessageDialog(this,
               "Vehicle not found.",
               "Error",
               JOptionPane.ERROR_MESSAGE);
           return;
       }
       
       // Show dialog to enter final mileage
       JDialog dialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "Complete Reservation", true);
       dialog.setSize(400, 300);
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
       
       // Create form fields
       JLabel initialMileageLabel = new JLabel("Initial Mileage: " + vehicle.getMileage());
       initialMileageLabel.setFont(new Font("Arial", Font.BOLD, 14));
       initialMileageLabel.setForeground(Color.WHITE);
       
       JSpinner finalMileageSpinner = new JSpinner(
           new SpinnerNumberModel(vehicle.getMileage(), vehicle.getMileage(), 999999.9, 0.1));
       styleSpinner(finalMileageSpinner);
       
       JTextArea notesArea = new JTextArea(3, 20);
       notesArea.setFont(new Font("Arial", Font.PLAIN, 14));
       notesArea.setForeground(Color.WHITE);
       notesArea.setBackground(parentFrame.getBackgroundColor().brighter());
       notesArea.setLineWrap(true);
       notesArea.setWrapStyleWord(true);
       notesArea.setBorder(BorderFactory.createCompoundBorder(
           BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
           BorderFactory.createEmptyBorder(8, 10, 8, 10)
       ));
       notesArea.setCaretColor(Color.WHITE);
       
       JScrollPane notesScrollPane = new JScrollPane(notesArea);
       notesScrollPane.setBorder(null);
       
       // Add form fields to panel
       gbc.gridx = 0;
       gbc.gridy = 0;
       gbc.gridwidth = 2;
       formPanel.add(initialMileageLabel, gbc);
       
       gbc.gridx = 0;
       gbc.gridy = 1;
       gbc.gridwidth = 1;
       JLabel finalMileageLabel = new JLabel("Final Mileage:");
       finalMileageLabel.setFont(new Font("Arial", Font.BOLD, 14));
       finalMileageLabel.setForeground(Color.WHITE);
       formPanel.add(finalMileageLabel, gbc);
       
       gbc.gridx = 1;
       formPanel.add(finalMileageSpinner, gbc);
       
       gbc.gridx = 0;
       gbc.gridy = 2;
       gbc.gridwidth = 2;
       JLabel notesLabel = new JLabel("Completion Notes:");
       notesLabel.setFont(new Font("Arial", Font.BOLD, 14));
       notesLabel.setForeground(Color.WHITE);
       formPanel.add(notesLabel, gbc);
       
       gbc.gridx = 0;
       gbc.gridy = 3;
       gbc.gridwidth = 2;
       gbc.fill = GridBagConstraints.BOTH;
       formPanel.add(notesScrollPane, gbc);
       
       // Create buttons panel
       JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
       buttonsPanel.setOpaque(false);
       
       JButton completeButton = createStyledButton("Complete", parentFrame.getPrimaryAccentColor());
       JButton cancelButton = createStyledButton("Cancel", new Color(150, 150, 150));
       
       // Add action listeners
       completeButton.addActionListener(e -> {
           // Get final mileage
           double finalMileage = ((Number) finalMileageSpinner.getValue()).doubleValue();
           
           // Update reservation
           reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
           reservation.setInitialMileage(vehicle.getMileage());
           reservation.setFinalMileage(finalMileage);
           
           if (!notesArea.getText().trim().isEmpty()) {
               reservation.setNotes(reservation.getNotes() + "\n\nCompletion Notes: " + notesArea.getText().trim());
           }
           
           // Update vehicle mileage
           vehicle.setMileage(finalMileage);
           
           // Save changes
           DataManager.getInstance().updateReservation(reservation);
           DataManager.getInstance().updateVehicle(vehicle);
           
           // Close dialog and refresh
           dialog.dispose();
           loadReservations();
       });
       
       cancelButton.addActionListener(e -> dialog.dispose());
       
       buttonsPanel.add(completeButton);
       buttonsPanel.add(cancelButton);
       
       // Add components to content panel
       contentPanel.add(new JLabel("Complete Reservation", JLabel.CENTER) {{
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
   
   private void showReservationDetails(Reservation reservation) {
       // Create a modern dialog for showing reservation details
       JDialog dialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "Reservation Details", true);
       dialog.setSize(700, 600);
       dialog.setLocationRelativeTo(this);
       
       // Create content panel with dark background
       JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
       contentPanel.setBackground(parentFrame.getBackgroundColor());
       contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
       
       // Get vehicle and user info
       Vehicle vehicle = DataManager.getInstance().getVehicleById(reservation.getVehicleId());
       User user = DataManager.getInstance().getUserById(reservation.getUserId());
       User approver = null;
       if (reservation.getApprovedBy() != null) {
           approver = DataManager.getInstance().getUserById(reservation.getApprovedBy());
       }
       
       // Create header with reservation ID and status
       JPanel headerPanel = new JPanel(new BorderLayout());
       headerPanel.setOpaque(false);
       headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
       
       JLabel titleLabel = new JLabel("Reservation #" + reservation.getReservationId());
       titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
       titleLabel.setForeground(Color.WHITE);
       
       // Create status panel
       JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
       statusPanel.setOpaque(false);
       
       JPanel statusIndicator = new JPanel() {
           @Override
           protected void paintComponent(Graphics g) {
               Graphics2D g2d = (Graphics2D) g;
               g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               
               // Draw circle with color based on status
               Color statusColor;
               switch (reservation.getStatus()) {
                   case APPROVED:
                       statusColor = new Color(0, 200, 0); // Green
                       break;
                   case PENDING:
                       statusColor = new Color(255, 180, 0); // Orange
                       break;
                   case REJECTED:
                       statusColor = new Color(200, 0, 0); // Red
                       break;
                   case CANCELLED:
                       statusColor = new Color(150, 150, 150); // Gray
                       break;
                   case COMPLETED:
                       statusColor = new Color(0, 150, 200); // Blue
                       break;
                   default:
                       statusColor = new Color(150, 150, 150); // Gray
               }
               
               g2d.setColor(statusColor);
               g2d.fillOval(0, 0, getWidth(), getHeight());
           }
       };
       statusIndicator.setPreferredSize(new Dimension(15, 15));
       statusIndicator.setOpaque(false);
       
       JLabel statusLabel = new JLabel(reservation.getStatus().toString());
       statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
       
       // Set status label color based on status
       Color textColor;
       switch (reservation.getStatus()) {
           case APPROVED:
               textColor = new Color(0, 200, 0); // Green
               break;
           case PENDING:
               textColor = new Color(255, 180, 0); // Orange
               break;
           case REJECTED:
               textColor = new Color(200, 0, 0); // Red
               break;
           case CANCELLED:
               textColor = new Color(150, 150, 150); // Gray
               break;
           case COMPLETED:
               textColor = new Color(0, 150, 200); // Blue
               break;
           default:
               textColor = new Color(150, 150, 150); // Gray
       }
       statusLabel.setForeground(textColor);
       
       statusPanel.add(statusIndicator);
       statusPanel.add(statusLabel);
       
       headerPanel.add(titleLabel, BorderLayout.WEST);
       headerPanel.add(statusPanel, BorderLayout.EAST);
       
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
       
       // Add reservation details
       addDetailRow(detailsPanel, gbc, 0, "Vehicle:", vehicle != null ? 
           vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel() : "Unknown");
       addDetailRow(detailsPanel, gbc, 1, "Reserved By:", user != null ? user.getFullName() : "Unknown");
       addDetailRow(detailsPanel, gbc, 2, "Start Date/Time:", reservation.getStartDateTime().format(DATE_FORMATTER));
       addDetailRow(detailsPanel, gbc, 3, "End Date/Time:", reservation.getEndDateTime().format(DATE_FORMATTER));
       addDetailRow(detailsPanel, gbc, 4, "Purpose:", reservation.getPurpose());
       addDetailRow(detailsPanel, gbc, 5, "Destination:", reservation.getDestination());
       addDetailRow(detailsPanel, gbc, 6, "Passengers:", String.valueOf(reservation.getEstimatedPassengers()));
       
       if (reservation.getStatus() == Reservation.ReservationStatus.APPROVED || 
           reservation.getStatus() == Reservation.ReservationStatus.COMPLETED) {
           addDetailRow(detailsPanel, gbc, 7, "Approved By:", approver != null ? approver.getFullName() : "Unknown");
           addDetailRow(detailsPanel, gbc, 8, "Approval Date:", reservation.getApprovalDateTime() != null ? 
               reservation.getApprovalDateTime().format(DATE_FORMATTER) : "N/A");
       }
       
       if (reservation.getStatus() == Reservation.ReservationStatus.COMPLETED) {
           addDetailRow(detailsPanel, gbc, 9, "Initial Mileage:", String.format("%.1f", reservation.getInitialMileage()));
           addDetailRow(detailsPanel, gbc, 10, "Final Mileage:", String.format("%.1f", reservation.getFinalMileage()));
           addDetailRow(detailsPanel, gbc, 11, "Distance Traveled:", 
               String.format("%.1f", reservation.getFinalMileage() - reservation.getInitialMileage()));
       }
       
       // Add notes section
       gbc.gridx = 0;
       gbc.gridy = 12;
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
       
       JTextArea notesArea = new JTextArea(reservation.getNotes());
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

       // Add remove button for admins
       if (DataManager.getInstance().getCurrentUser().isAdmin()) {
           JButton removeButton = createActionButton("Remove", new Color(220, 53, 69));
           removeButton.addActionListener(e -> {
               // Close the dialog first
               Window window = SwingUtilities.getWindowAncestor(buttonsPanel);
               if (window instanceof JDialog) {
                   ((JDialog) window).dispose();
               }
               
               // Then remove the reservation
               removeReservation(reservation);
           });
           
           buttonsPanel.add(removeButton);
       }

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

   /**
    * Removes a reservation from the system after confirmation.
    * 
    * @param reservation The reservation to remove
    */
   private void removeReservation(Reservation reservation) {
       int choice = JOptionPane.showConfirmDialog(this,
           "Are you sure you want to permanently remove this reservation?\nThis action cannot be undone.",
           "Confirm Removal",
           JOptionPane.YES_NO_OPTION,
           JOptionPane.WARNING_MESSAGE);
           
       if (choice == JOptionPane.YES_OPTION) {
           try {
               // Delete the reservation
               DataManager.getInstance().deleteReservation(reservation.getReservationId());
               
               // Show success message
               parentFrame.setStatusMessage("Reservation removed successfully");
               
               // Refresh the list
               loadReservations();
           } catch (Exception ex) {
               JOptionPane.showMessageDialog(this,
                   "Error removing reservation: " + ex.getMessage(),
                   "Error",
                   JOptionPane.ERROR_MESSAGE);
           }
       }
   }
}
