import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.User;

/**
* Main application window for administrators with full system access
* Redesigned to match the modern UI of the login screen
*/
public class AdminMainFrame extends JFrame implements MainFrameInterface {
 private JPanel contentPanel;
 private JMenuBar menuBar;
 private JLabel statusLabel;
 
 // Panels
 private DashboardPanel dashboardPanel;
 private VehicleManagementPanel vehiclePanel;
 private ReservationPanel reservationPanel;
 private UserManagementPanel userPanel;
 private ReportsPanel reportsPanel;
 // Add a field for the CalendarPanel
 private CalendarPanel calendarPanel;
 
 // Animation components
 private Timer animationTimer;
 private JPanel sidebarPanel;
 private boolean sidebarExpanded = true;
 
 // Custom colors defined directly in this class instead of referencing LoginFrame
 private final Color DARK_BG = new Color(30, 20, 60);
 private final Color ACCENT_PINK = new Color(255, 64, 200);
 private final Color ACCENT_BLUE = new Color(64, 180, 255);
 private final Color FIELD_BG = new Color(45, 35, 85);
 private final Color TEXT_COLOR = Color.WHITE;
 private final Color SIDEBAR_COLOR = new Color(30, 25, 75);

 // Add wave animation to the content panel
 private Timer waveAnimationTimer;
 private float waveOffset = 0.0f;
 
 public AdminMainFrame() {
     setTitle("CNSC Motorpool - Administrator");
     setExtendedState(JFrame.MAXIMIZED_BOTH);
     setUndecorated(true); // Remove window decorations for full screen
     setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
     
     initComponents();
     
     addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
             logout();
         }
     });
     
     // Add key listener for ESC to exit full screen
     addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
             if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                 logout();
             }
         }
     });
     setFocusable(true);
 }
 
 private void initComponents() {
     // Create main panel with dark background
     JPanel mainPanel = new JPanel(new BorderLayout());
     mainPanel.setBackground(DARK_BG);
     
     // Create sidebar
     sidebarPanel = createSidebar();
     
     // Create content panel with card layout
     contentPanel = new JPanel(new CardLayout()) {
         @Override
         protected void paintComponent(Graphics g) {
             super.paintComponent(g);
             Graphics2D g2d = (Graphics2D) g;
             g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
             
             // Draw background
             g2d.setColor(DARK_BG);
             g2d.fillRect(0, 0, getWidth(), getHeight());
             
             // Draw subtle wave pattern at the bottom
             drawSubtleWaves(g2d, getWidth(), getHeight());
         }
     };
     contentPanel.setBackground(DARK_BG);

     // Start the wave animation
     startWaveAnimation();
     
     // Create panels
     dashboardPanel = new DashboardPanel(this);
     vehiclePanel = new VehicleManagementPanel(this);
     reservationPanel = new ReservationPanel(this);
     userPanel = new UserManagementPanel(this);
     reportsPanel = new ReportsPanel(this);
     // In the initComponents method, after creating the other panels, add:
     calendarPanel = new CalendarPanel(this);
     
     // Add panels to content panel
     contentPanel.add(dashboardPanel, "dashboard");
     contentPanel.add(vehiclePanel, "vehicles");
     contentPanel.add(reservationPanel, "reservations");
     contentPanel.add(userPanel, "users");
     contentPanel.add(reportsPanel, "reports");
     // In the initComponents method, after adding the other panels to contentPanel, add:
     contentPanel.add(calendarPanel, "calendar");
     
     // Create top bar with exit button
     JPanel topBar = createTopBar();
     
     // Create status bar
     JPanel statusPanel = createStatusBar();
     
     // Add components to main panel
     mainPanel.add(topBar, BorderLayout.NORTH);
     mainPanel.add(sidebarPanel, BorderLayout.WEST);
     mainPanel.add(contentPanel, BorderLayout.CENTER);
     mainPanel.add(statusPanel, BorderLayout.SOUTH);
     
     // Add main panel to frame
     setContentPane(mainPanel);
     
     // Show dashboard by default
     showPanel("dashboard");
 }

 private void drawSubtleWaves(Graphics2D g2d, int width, int height) {
     // Create gradient for waves
     GradientPaint blueGradient = new GradientPaint(
             0, height - 100, new Color(ACCENT_BLUE.getRed(), ACCENT_BLUE.getGreen(), ACCENT_BLUE.getBlue(), 40),
             width, height, new Color(ACCENT_PINK.getRed(), ACCENT_PINK.getGreen(), ACCENT_PINK.getBlue(), 40)
     );
     g2d.setPaint(blueGradient);

     // Draw multiple wave layers with different phases
     drawWaveLayer(g2d, width, height, 0.2f, 60, waveOffset, height - 80);
     drawWaveLayer(g2d, width, height, 0.15f, 40, waveOffset * 0.8f, height - 40);
 }

 private void drawWaveLayer(Graphics2D g2d, int width, int height, float opacity, int amplitude, float offset, int yPosition) {
     g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

     Path2D path = new Path2D.Float();
     path.moveTo(0, yPosition);

     // Create wave path
     for (int x = 0; x <= width; x += 10) {
         double y = Math.sin((x * 0.015) + offset) * amplitude + yPosition;
         path.lineTo(x, y);
     }

     // Complete the path
     path.lineTo(width, height);
     path.lineTo(0, height);
     path.closePath();

     g2d.fill(path);

     // Reset composite
     g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
 }

 private void startWaveAnimation() {
     waveAnimationTimer = new Timer(50, e -> {
         waveOffset += 0.05f;
         if (waveOffset > 1000) {
             waveOffset = 0;
         }
         contentPanel.repaint();
     });
     waveAnimationTimer.start();
 }
 
 private JPanel createTopBar() {
     JPanel topBar = new JPanel(new BorderLayout());
     topBar.setBackground(SIDEBAR_COLOR);
     topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(40, 30, 90)));
     topBar.setPreferredSize(new Dimension(getWidth(), 50));
     
     // App title
     JLabel titleLabel = new JLabel("CNSC MOTORPOOL");
     titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
     titleLabel.setForeground(ACCENT_PINK);
     titleLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
     
     // Exit button
     JButton exitButton = new JButton("×");
     exitButton.setFont(new Font("Arial", Font.BOLD, 24));
     exitButton.setForeground(Color.WHITE);
     exitButton.setBackground(new Color(0, 0, 0, 0));
     exitButton.setBorderPainted(false);
     exitButton.setContentAreaFilled(false);
     exitButton.setFocusPainted(false);
     exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
     exitButton.addActionListener(e -> logout());
     
     JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
     rightPanel.setOpaque(false);
     rightPanel.add(exitButton);
     
     topBar.add(titleLabel, BorderLayout.WEST);
     topBar.add(rightPanel, BorderLayout.EAST);
     
     return topBar;
 }
 
 private JPanel createSidebar() {
     JPanel sidebar = new JPanel(new BorderLayout());
     sidebar.setBackground(SIDEBAR_COLOR);
     sidebar.setPreferredSize(new Dimension(250, 0));
     sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(40, 30, 90)));
     
     // User profile panel
     JPanel profilePanel = new JPanel(new BorderLayout(10, 0));
     profilePanel.setBackground(new Color(30, 25, 75));
     profilePanel.setBorder(new EmptyBorder(15, 15, 15, 15));
     
     // User avatar
     JPanel avatarPanel = new JPanel() {
         @Override
         protected void paintComponent(Graphics g) {
             super.paintComponent(g);
             Graphics2D g2d = (Graphics2D) g;
             g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
             
             // Draw circle
             g2d.setColor(ACCENT_PINK); // Pink for admin
             g2d.fillOval(0, 0, getWidth(), getHeight());
             
             // Draw admin icon
             g2d.setColor(Color.WHITE);
             int centerX = getWidth() / 2;
             int centerY = getHeight() / 2;
             
             // Crown shape for admin
             int[] xPoints = {centerX - 15, centerX - 5, centerX, centerX + 5, centerX + 15, centerX + 10, centerX - 10};
             int[] yPoints = {centerY, centerY - 15, centerY - 5, centerY - 15, centerY, centerY + 10, centerY + 10};
             g2d.fillPolygon(xPoints, yPoints, 7);
         }
     };
     avatarPanel.setPreferredSize(new Dimension(60, 60));
     avatarPanel.setOpaque(false);
     
     // User info
     User currentUser = DataManager.getInstance().getCurrentUser();
     JPanel userInfoPanel = new JPanel(new GridLayout(2, 1));
     userInfoPanel.setOpaque(false);
     
     JLabel nameLabel = new JLabel(currentUser.getFullName());
     nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
     nameLabel.setForeground(Color.WHITE);
     
     JLabel roleLabel = new JLabel("Administrator");
     roleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
     roleLabel.setForeground(new Color(189, 195, 199));
     
     userInfoPanel.add(nameLabel);
     userInfoPanel.add(roleLabel);
     
     profilePanel.add(avatarPanel, BorderLayout.WEST);
     profilePanel.add(userInfoPanel, BorderLayout.CENTER);
     
     // Navigation panel
     JPanel navPanel = new JPanel();
     navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
     navPanel.setBackground(SIDEBAR_COLOR);
     navPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
     
     // Create navigation buttons
     JButton dashboardButton = createNavButton("Dashboard", "dashboard");
     JButton vehiclesButton = createNavButton("Vehicles", "vehicles");
     JButton reservationsButton = createNavButton("Reservations", "reservations");
     JButton usersButton = createNavButton("Users", "users");
     JButton reportsButton = createNavButton("Reports", "reports");
     // In the createSidebar method, after creating the other navigation buttons, add:
     JButton calendarButton = createNavButton("Calendar", "calendar");
     JButton logoutButton = createNavButton("Logout", "logout");
     
     // Add buttons to navigation panel
     navPanel.add(dashboardButton);
     navPanel.add(Box.createVerticalStrut(10));
     navPanel.add(vehiclesButton);
     navPanel.add(Box.createVerticalStrut(10));
     navPanel.add(reservationsButton);
     navPanel.add(Box.createVerticalStrut(10));
     navPanel.add(usersButton);
     navPanel.add(Box.createVerticalStrut(10));
     navPanel.add(reportsButton);
     // In the createSidebar method, after adding the other buttons to navPanel, add:
     navPanel.add(calendarButton);
     navPanel.add(Box.createVerticalStrut(10));
     navPanel.add(Box.createVerticalGlue());
     navPanel.add(logoutButton);
     
     // Toggle sidebar button
     JButton toggleButton = new JButton() {
         @Override
         protected void paintComponent(Graphics g) {
             super.paintComponent(g);
             Graphics2D g2d = (Graphics2D) g;
             g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
             
             g2d.setColor(Color.WHITE);
             int centerX = getWidth() / 2;
             int centerY = getHeight() / 2;
             
             // Draw toggle icon (three lines)
             g2d.setStroke(new BasicStroke(2));
             g2d.drawLine(centerX - 8, centerY - 5, centerX + 8, centerY - 5);
             g2d.drawLine(centerX - 8, centerY, centerX + 8, centerY);
             g2d.drawLine(centerX - 8, centerY + 5, centerX + 8, centerY + 5);
         }
     };
     toggleButton.setBackground(new Color(30, 25, 75));
     toggleButton.setBorderPainted(false);
     toggleButton.setFocusPainted(false);
     toggleButton.setPreferredSize(new Dimension(30, 30));
     toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
     toggleButton.addActionListener(e -> toggleSidebar());
     
     JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
     togglePanel.setBackground(new Color(30, 25, 75));
     togglePanel.add(toggleButton);
     
     // Add components to sidebar
     sidebar.add(profilePanel, BorderLayout.NORTH);
     sidebar.add(navPanel, BorderLayout.CENTER);
     sidebar.add(togglePanel, BorderLayout.SOUTH);
     
     return sidebar;
 }
 
 private JButton createNavButton(String text, String action) {
     JButton button = new JButton(text) {
         @Override
         protected void paintComponent(Graphics g) {
             Graphics2D g2d = (Graphics2D) g;
             g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
             
             if (getModel().isPressed()) {
                 g2d.setColor(new Color(20, 15, 55));
             } else if (getModel().isRollover()) {
                 g2d.setColor(new Color(35, 28, 80));
             } else {
                 g2d.setColor(SIDEBAR_COLOR);
             }
             g2d.fillRect(0, 0, getWidth(), getHeight());
             
             // Draw selection indicator
             if (checkIfSelected()) {
                 g2d.setColor(ACCENT_PINK);
                 g2d.fillRect(0, 0, 5, getHeight());
             }
             
             // Draw text
             g2d.setFont(getFont());
             g2d.setColor(Color.WHITE);
             FontMetrics fm = g2d.getFontMetrics();
             int textWidth = fm.stringWidth(getText());
             int textHeight = fm.getHeight();
             
             g2d.drawString(getText(), 20, (getHeight() - textHeight) / 2 + fm.getAscent());
         }
         
         // Renamed from isSelected() to checkIfSelected() to avoid override issues
         private boolean checkIfSelected() {
             String currentPanel = "";
             
             for (Component comp : contentPanel.getComponents()) {
                 if (comp.isVisible()) {
                     currentPanel = comp.getName();
                     break;
                 }
             }
             
             return action.equals(currentPanel);
         }
     };
     
     button.setFont(new Font("Arial", Font.BOLD, 14));
     button.setForeground(Color.WHITE);
     button.setBackground(SIDEBAR_COLOR);
     button.setBorderPainted(false);
     button.setFocusPainted(false);
     button.setContentAreaFilled(false);
     button.setHorizontalAlignment(SwingConstants.LEFT);
     button.setPreferredSize(new Dimension(250, 40));
     button.setMaximumSize(new Dimension(250, 40));
     
     // Add hover effect
     button.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent e) {
             button.setCursor(new Cursor(Cursor.HAND_CURSOR));
         }
         
         @Override
         public void mouseExited(MouseEvent e) {
             button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
         }
     });
     
     // Add action
     button.addActionListener(e -> {
         if (action.equals("logout")) {
             logout();
         } else {
             showPanel(action);
         }
     });
     
     return button;
 }
 
 private JPanel createStatusBar() {
     JPanel statusPanel = new JPanel(new BorderLayout());
     statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
     statusPanel.setBackground(SIDEBAR_COLOR);
     
     statusLabel = new JLabel("Ready");
     statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
     statusLabel.setForeground(Color.WHITE);
     
     User currentUser = DataManager.getInstance().getCurrentUser();
     JLabel userLabel = new JLabel("Logged in as: " + 
             currentUser.getFullName() + " (Administrator)");
     userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
     userLabel.setForeground(Color.WHITE);
     
     statusPanel.add(statusLabel, BorderLayout.WEST);
     statusPanel.add(userLabel, BorderLayout.EAST);
     
     return statusPanel;
 }
 
 private void createMenuBar() {
     menuBar = new JMenuBar();
     
     // File menu
     JMenu fileMenu = new JMenu("File");
     JMenuItem logoutItem = new JMenuItem("Logout");
     JMenuItem exitItem = new JMenuItem("Exit");
     
     logoutItem.addActionListener(e -> logout());
     exitItem.addActionListener(e -> {
         DataManager.getInstance().saveAllData();
         System.exit(0);
     });
     
     fileMenu.add(logoutItem);
     fileMenu.addSeparator();
     fileMenu.add(exitItem);
     
     // View menu
     JMenu viewMenu = new JMenu("View");
     JMenuItem dashboardItem = new JMenuItem("Dashboard");
     JMenuItem vehiclesItem = new JMenuItem("Vehicles");
     JMenuItem reservationsItem = new JMenuItem("Reservations");
     JMenuItem usersItem = new JMenuItem("Users");
     JMenuItem reportsItem = new JMenuItem("Reports");
     JMenuItem toggleSidebarItem = new JMenuItem("Toggle Sidebar");
     
     dashboardItem.addActionListener(e -> showPanel("dashboard"));
     vehiclesItem.addActionListener(e -> showPanel("vehicles"));
     reservationsItem.addActionListener(e -> showPanel("reservations"));
     usersItem.addActionListener(e -> showPanel("users"));
     reportsItem.addActionListener(e -> showPanel("reports"));
     toggleSidebarItem.addActionListener(e -> toggleSidebar());
     
     viewMenu.add(dashboardItem);
     viewMenu.add(vehiclesItem);
     viewMenu.add(reservationsItem);
     viewMenu.add(usersItem);
     viewMenu.add(reportsItem);
     viewMenu.addSeparator();
     viewMenu.add(toggleSidebarItem);
     
     // Help menu
     JMenu helpMenu = new JMenu("Help");
     JMenuItem aboutItem = new JMenuItem("About");
     
     aboutItem.addActionListener(e -> showAboutDialog());
     
     helpMenu.add(aboutItem);
     
     // Add menus to menu bar
     menuBar.add(fileMenu);
     menuBar.add(viewMenu);
     menuBar.add(helpMenu);
     
     setJMenuBar(menuBar);
 }
 
 private void toggleSidebar() {
     final int expandedWidth = 250;
     final int collapsedWidth = 60;
     final int animationDuration = 200; // milliseconds
     final int steps = 20;
     
     if (animationTimer != null && animationTimer.isRunning()) {
         return;
     }
     
     final int startWidth = sidebarPanel.getWidth();
     final int targetWidth = sidebarExpanded ? collapsedWidth : expandedWidth;
     final int widthDelta = targetWidth - startWidth;
     
     animationTimer = new Timer(animationDuration / steps, new ActionListener() {
         int step = 0;
         
         @Override
         public void actionPerformed(ActionEvent e) {
             step++;
             if (step > steps) {
                 animationTimer.stop();
                 sidebarExpanded = !sidebarExpanded;
                 
                 // Update UI components based on sidebar state
                 updateSidebarComponents();
                 return;
             }
             
             // Calculate current width using easing function
             float progress = (float) step / steps;
             // Ease in-out function: progress^2 * (3 - 2 * progress)
             float easedProgress = progress * progress * (3 - 2 * progress);
             int currentWidth = startWidth + (int) (widthDelta * easedProgress);
             
             // Update sidebar width
             sidebarPanel.setPreferredSize(new Dimension(currentWidth, sidebarPanel.getHeight()));
             sidebarPanel.revalidate();
         }
     });
     
     animationTimer.start();
 }
 
 private void updateSidebarComponents() {
     // Update sidebar components based on expanded/collapsed state
     Component[] components = sidebarPanel.getComponents();
     
     for (Component component : components) {
         if (component instanceof JPanel) {
             JPanel panel = (JPanel) component;
             
             if (panel.getLayout() instanceof BoxLayout) {
                 // This is the navigation panel
                 Component[] navButtons = panel.getComponents();
                 
                 for (Component navComp : navButtons) {
                     if (navComp instanceof JButton) {
                         JButton button = (JButton) navComp;
                         if (!sidebarExpanded) {
                             // Show only icons in collapsed state
                             button.setText("");
                             button.setPreferredSize(new Dimension(60, 40));
                             button.setMaximumSize(new Dimension(60, 40));
                         } else {
                             // Restore text in expanded state
                             button.setPreferredSize(new Dimension(250, 40));
                             button.setMaximumSize(new Dimension(250, 40));
                         }
                     }
                 }
             } else if (panel.getLayout() instanceof BorderLayout) {
                 // This might be the profile panel
                 if (panel.getBackground().equals(new Color(30, 25, 75))) {
                     if (!sidebarExpanded) {
                         // Hide user info in collapsed state
                         for (Component profileComp : panel.getComponents()) {
                             if (profileComp instanceof JPanel && !(profileComp instanceof JPanel && 
                                 ((JPanel)profileComp).getComponentCount() > 0 && 
                                 ((JPanel)profileComp).getComponent(0) instanceof JLabel)) {
                                 profileComp.setVisible(false);
                             }
                         }
                     } else {
                         // Show all components in expanded state
                         for (Component profileComp : panel.getComponents()) {
                             profileComp.setVisible(true);
                         }
                     }
                 }
             }
         }
     }
     
     sidebarPanel.revalidate();
     sidebarPanel.repaint();
 }
 
 public void showPanel(String panelName) {
     // Get current and target panels
     final Component currentPanel = getCurrentVisiblePanel();
     final Component targetPanel = getPanel(panelName);
     
     if (currentPanel == targetPanel) {
         return;
     }
     
     // Simple panel switch without fade effect
     CardLayout cl = (CardLayout) contentPanel.getLayout();
     cl.show(contentPanel, panelName);
     
     // Update status label
     statusLabel.setText("Viewing: " + panelName);
     
     // Refresh the panel
     contentPanel.revalidate();
     contentPanel.repaint();
 }
 
 private Component getCurrentVisiblePanel() {
     for (Component comp : contentPanel.getComponents()) {
         if (comp.isVisible()) {
             return comp;
         }
     }
     return null;
 }
 
 private Component getPanel(String name) {
     switch (name) {
         case "dashboard": return dashboardPanel;
         case "vehicles": return vehiclePanel;
         case "reservations": return reservationPanel;
         case "users": return userPanel;
         case "reports": return reportsPanel;
         // In the getPanel method, add a case for the calendar panel:
         case "calendar": return calendarPanel;
         default: return dashboardPanel;
     }
 }
 
 private void showAboutDialog() {
     // Create custom about dialog with animation
     JDialog aboutDialog = new JDialog(this, "About", true);
     aboutDialog.setSize(500, 400);
     aboutDialog.setLocationRelativeTo(this);
     
     JPanel mainPanel = new JPanel(new BorderLayout());
     mainPanel.setBackground(Color.WHITE);
     mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
     
     // Logo panel with animation
     JPanel logoPanel = new JPanel() {
         private Timer animTimer;
         private int rotation = 0;
         
         {
             animTimer = new Timer(50, e -> {
                 rotation = (rotation + 5) % 360;
                 repaint();
             });
             
             addHierarchyListener(e -> {
                 if (isShowing()) {
                     animTimer.start();
                 } else {
                     animTimer.stop();
                 }
             });
         }
         
         @Override
         protected void paintComponent(Graphics g) {
             super.paintComponent(g);
             Graphics2D g2d = (Graphics2D) g;
             g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
             
             int centerX = getWidth() / 2;
             int centerY = getHeight() / 2;
             int radius = Math.min(getWidth(), getHeight()) / 3;
             
             // Draw rotating circle
             g2d.setColor(ACCENT_PINK);
             g2d.rotate(Math.toRadians(rotation), centerX, centerY);
             g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
             
             // Draw car icon
             g2d.setColor(Color.WHITE);
             g2d.fillRoundRect(centerX - 30, centerY - 10, 60, 25, 10, 10);
             g2d.fillRoundRect(centerX - 20, centerY - 22, 40, 18, 8, 8);
             
             // Wheels
             g2d.setColor(Color.BLACK);
             g2d.fillOval(centerX - 20, centerY + 10, 15, 15);
             g2d.fillOval(centerX + 5, centerY + 10, 15, 15);
         }
     };
     logoPanel.setPreferredSize(new Dimension(150, 150));
     
     JPanel logoContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
     logoContainer.setBackground(Color.WHITE);
     logoContainer.add(logoPanel);
     
     // Info panel
     JPanel infoPanel = new JPanel();
     infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
     infoPanel.setBackground(Color.WHITE);
     
     JLabel titleLabel = new JLabel("Campus Motor Pool Management System");
     titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
     titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
     
     JLabel versionLabel = new JLabel("Version 1.0");
     versionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
     versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
     
     JLabel copyrightLabel = new JLabel("© 2023 University IT Department");
     copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 12));
     copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
     
     JTextArea descriptionArea = new JTextArea(
         "A comprehensive system for managing university vehicle fleet.\n\n" +
         "This application allows administrators to manage vehicles, " +
         "users, and reservations for the campus motor pool. Faculty, " +
         "staff, and students can make vehicle reservations for " +
         "university-related activities."
     );
     descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
     descriptionArea.setLineWrap(true);
     descriptionArea.setWrapStyleWord(true);
     descriptionArea.setEditable(false);
     descriptionArea.setBackground(Color.WHITE);
     descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);
     
     infoPanel.add(Box.createVerticalStrut(20));
     infoPanel.add(titleLabel);
     infoPanel.add(Box.createVerticalStrut(10));
     infoPanel.add(versionLabel);
     infoPanel.add(Box.createVerticalStrut(5));
     infoPanel.add(copyrightLabel);
     infoPanel.add(Box.createVerticalStrut(20));
     infoPanel.add(descriptionArea);
     
     // Close button
     JButton closeButton = new JButton("Close");
     closeButton.setFont(new Font("Arial", Font.BOLD, 14));
     closeButton.addActionListener(e -> aboutDialog.dispose());
     
     JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
     buttonPanel.setBackground(Color.WHITE);
     buttonPanel.add(closeButton);
     
     // Add components to main panel
     mainPanel.add(logoContainer, BorderLayout.NORTH);
     mainPanel.add(infoPanel, BorderLayout.CENTER);
     mainPanel.add(buttonPanel, BorderLayout.SOUTH);
     
     aboutDialog.setContentPane(mainPanel);
     aboutDialog.setVisible(true);
 }
 
 private void logout() {
     int choice = JOptionPane.showConfirmDialog(this,
         "Are you sure you want to logout?",
         "Logout Confirmation",
         JOptionPane.YES_NO_OPTION);
         
     if (choice == JOptionPane.YES_OPTION) {
         DataManager.getInstance().saveAllData();
         DataManager.getInstance().setCurrentUser(null);

         if (waveAnimationTimer != null && waveAnimationTimer.isRunning()) {
             waveAnimationTimer.stop();
         }
         
         // Open login frame immediately without fade
         SwingUtilities.invokeLater(() -> {
             LoginFrame loginFrame = new LoginFrame();
             loginFrame.setVisible(true);
             dispose();
         });
     }
 }
 
 @Override
 public void setStatusMessage(String message) {
     statusLabel.setText(message);
 }

 @Override
 public Color getPrimaryAccentColor() {
     return ACCENT_PINK;
 }

 @Override
 public Color getSecondaryAccentColor() {
     return ACCENT_BLUE;
 }

 @Override
 public Color getBackgroundColor() {
     return DARK_BG;
 }

 @Override
 public Color getFieldBgColor() {
     return FIELD_BG;
 }

 @Override
 public Color getTextColor() {
     return TEXT_COLOR;
 }

 @Override
 public Color getSidebarColor() {
     return SIDEBAR_COLOR;
 }
}
