import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.util.UUID;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.User;

/**
* Enhanced registration screen with modern design matching the login screen
*/
public class RegistrationFrame extends JFrame {
   private JTextField usernameField;
   private JPasswordField passwordField;
   private JPasswordField confirmPasswordField;
   private JTextField fullNameField;
   private JTextField emailField;
   private JTextField phoneField;
   private JComboBox<User.UserRole> roleComboBox;
   private JButton registerButton;
   private JButton cancelButton;
   
   // Animation components
   private Timer waveAnimationTimer;
   private float waveOffset = 0.0f;
   
   // Custom colors - using the same colors as LoginFrame
   private final Color DARK_BG = LoginFrame.DARK_BG;           // Dark purple-blue
   private final Color ACCENT_PINK = LoginFrame.ACCENT_PINK;   // Vibrant pink
   private final Color ACCENT_BLUE = LoginFrame.ACCENT_BLUE;   // Vibrant blue
   private final Color FIELD_BG = LoginFrame.FIELD_BG;         // Slightly lighter than background
   private final Color TEXT_COLOR = LoginFrame.TEXT_COLOR;     // White text
   private final Color SIDEBAR_COLOR = LoginFrame.SIDEBAR_COLOR; // Sidebar color
   
   public RegistrationFrame() {
       setTitle("CNSC Motorpool - Registration");
       setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       
       // Set to full screen
       setExtendedState(JFrame.MAXIMIZED_BOTH);
       setUndecorated(true); // Remove window decorations for full screen
       
       initComponents();
       startWaveAnimation();
       
       // Add key listener for ESC to exit full screen
       addKeyListener(new KeyAdapter() {
           @Override
           public void keyPressed(KeyEvent e) {
               if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                   dispose();
                   
                   // Show login frame again
                   for (Window window : Window.getWindows()) {
                       if (window instanceof LoginFrame) {
                           window.setVisible(true);
                           break;
                       }
                   }
               }
           }
       });
       setFocusable(true);
   }
   
   private void initComponents() {
       // Create main panel with dark background
       JPanel mainPanel = new JPanel(new BorderLayout());
       mainPanel.setBackground(DARK_BG);
       
       // Create split layout
       JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
       splitPane.setDividerLocation(600);
       splitPane.setDividerSize(0);
       splitPane.setBorder(null);
       splitPane.setEnabled(false);
       
       // Left panel - Registration form
       JPanel leftPanel = new JPanel(new BorderLayout());
       leftPanel.setBackground(DARK_BG);
       leftPanel.setBorder(new EmptyBorder(60, 60, 60, 60));
       
       // Logo at top
       JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
       logoPanel.setOpaque(false);
       
       JLabel logoLabel = new JLabel("CNSC MOTORPOOL");
       logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
       logoLabel.setForeground(ACCENT_PINK);
       logoPanel.add(logoLabel);
       
       // Registration title
       JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
       titlePanel.setOpaque(false);
       titlePanel.setBorder(new EmptyBorder(20, 0, 20, 0));
       
       JLabel titleLabel = new JLabel("Create New Account");
       titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
       titleLabel.setForeground(Color.WHITE);
       titlePanel.add(titleLabel);
       
       // Registration form
       JPanel formPanel = new JPanel();
       formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
       formPanel.setOpaque(false);
       
       // Username field
       usernameField = createStyledTextField("Username");
       JPanel usernameWrapper = new JPanel(new BorderLayout());
       usernameWrapper.setOpaque(false);
       usernameWrapper.add(usernameField);
       usernameWrapper.setMaximumSize(new Dimension(350, 50));
       
       // Password field
       passwordField = createStyledPasswordField("Password");
       JPanel passwordWrapper = new JPanel(new BorderLayout());
       passwordWrapper.setOpaque(false);
       passwordWrapper.add(passwordField);
       passwordWrapper.setMaximumSize(new Dimension(350, 50));
       
       // Confirm Password field
       confirmPasswordField = createStyledPasswordField("Confirm Password");
       JPanel confirmPasswordWrapper = new JPanel(new BorderLayout());
       confirmPasswordWrapper.setOpaque(false);
       confirmPasswordWrapper.add(confirmPasswordField);
       confirmPasswordWrapper.setMaximumSize(new Dimension(350, 50));
       
       // Full Name field
       fullNameField = createStyledTextField("Full Name");
       JPanel fullNameWrapper = new JPanel(new BorderLayout());
       fullNameWrapper.setOpaque(false);
       fullNameWrapper.add(fullNameField);
       fullNameWrapper.setMaximumSize(new Dimension(350, 50));
       
       // Email field
       emailField = createStyledTextField("Email");
       JPanel emailWrapper = new JPanel(new BorderLayout());
       emailWrapper.setOpaque(false);
       emailWrapper.add(emailField);
       emailWrapper.setMaximumSize(new Dimension(350, 50));
       
       // Phone field
       phoneField = createStyledTextField("Phone");
       JPanel phoneWrapper = new JPanel(new BorderLayout());
       phoneWrapper.setOpaque(false);
       phoneWrapper.add(phoneField);
       phoneWrapper.setMaximumSize(new Dimension(350, 50));
       
       // Role selection
       JPanel rolePanel = new JPanel(new BorderLayout());
       rolePanel.setOpaque(false);
       rolePanel.setBorder(new EmptyBorder(5, 0, 5, 0));
       
       JLabel roleLabel = new JLabel("Role:");
       roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
       roleLabel.setForeground(Color.PINK);
       
       roleComboBox = new JComboBox<>(new User.UserRole[]{User.UserRole.USER, User.UserRole.ADMIN});
       roleComboBox.setRenderer(new DefaultListCellRenderer() {
           @Override
           public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
               Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
               if (value != null) {
                   User.UserRole role = (User.UserRole) value;
                   setText(formatRoleText(role));
                   setIcon(getRoleIcon(role));
                   
                   // Make text visible with proper colors
                   if (isSelected) {
                       setBackground(ACCENT_PINK);
                       setForeground(Color.WHITE);
                   } else {
                       setBackground(FIELD_BG);
                       setForeground(Color.WHITE);
                   }
               }
               return c;
           }
       });

       // Improve the roleComboBox styling
       roleComboBox.setBackground(FIELD_BG);
       roleComboBox.setForeground(Color.pink);
       roleComboBox.setBorder(BorderFactory.createCompoundBorder(
           BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
           BorderFactory.createEmptyBorder(8, 12, 8, 12)
       ));

       // Fix the role panel layout to prevent overlapping
       rolePanel = new JPanel(new BorderLayout(10, 0)); // Add horizontal gap
       rolePanel.setOpaque(false);
       rolePanel.setBorder(new EmptyBorder(5, 0, 5, 0));

       roleLabel = new JLabel("Role:");
       roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
       roleLabel.setForeground(Color.WHITE);
       roleLabel.setPreferredSize(new Dimension(50, 30)); // Fixed width for label

       rolePanel.add(roleLabel, BorderLayout.WEST);
       rolePanel.add(roleComboBox, BorderLayout.CENTER);
       rolePanel.setMaximumSize(new Dimension(350, 50));
       
       // Register button
       registerButton = LoginFrame.createStyledButton("Register", ACCENT_PINK);
       JPanel registerButtonWrapper = new JPanel(new BorderLayout());
       registerButtonWrapper.setOpaque(false);
       registerButtonWrapper.add(registerButton);
       registerButtonWrapper.setMaximumSize(new Dimension(350, 50));
       
       // Cancel button
       cancelButton = LoginFrame.createStyledButton("Cancel", ACCENT_BLUE);
       JPanel cancelButtonWrapper = new JPanel(new BorderLayout());
       cancelButtonWrapper.setOpaque(false);
       cancelButtonWrapper.add(cancelButton);
       cancelButtonWrapper.setMaximumSize(new Dimension(350, 50));
       
       // Button panel
       JPanel buttonPanel = new JPanel();
       buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
       buttonPanel.setOpaque(false);
       buttonPanel.add(registerButtonWrapper);
       buttonPanel.add(Box.createHorizontalStrut(20));
       buttonPanel.add(cancelButtonWrapper);
       buttonPanel.setMaximumSize(new Dimension(350, 50));
       buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
       
       // Add components to form with spacing
       formPanel.add(Box.createVerticalStrut(20));
       formPanel.add(usernameWrapper);
       formPanel.add(Box.createVerticalStrut(25)); // Increased from 15
       formPanel.add(passwordWrapper);
       formPanel.add(Box.createVerticalStrut(25)); // Increased from 15
       formPanel.add(confirmPasswordWrapper);
       formPanel.add(Box.createVerticalStrut(25)); // Increased from 15
       formPanel.add(fullNameWrapper);
       formPanel.add(Box.createVerticalStrut(25)); // Increased from 15
       formPanel.add(emailWrapper);
       formPanel.add(Box.createVerticalStrut(25)); // Increased from 15
       formPanel.add(phoneWrapper);
       formPanel.add(Box.createVerticalStrut(25)); // Increased from 15
       formPanel.add(rolePanel);
       formPanel.add(Box.createVerticalStrut(35)); // Increased from 25
       formPanel.add(buttonPanel);
       formPanel.add(Box.createVerticalStrut(20)); // Added extra space at the bottom
       
       // Center the form with better spacing and scrolling capability
       JPanel formContainer = new JPanel();
       formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
       formContainer.setOpaque(false);
       formContainer.setBorder(new EmptyBorder(0, 50, 100, 50)); // Add more padding
       formContainer.add(formPanel);

       // Center align the form elements
       for (Component comp : formPanel.getComponents()) {
           if (comp instanceof JPanel) {
               ((JPanel) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
           }
       }

       // Add a scroll pane to handle overflow if the screen is too small
       JScrollPane scrollPane = new JScrollPane(formContainer);
       scrollPane.setBorder(null);
       scrollPane.setOpaque(false);
       scrollPane.getViewport().setOpaque(false);
       scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
       scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
       scrollPane.setPreferredSize(new Dimension(500, 600));

       // Modify the left panel to use the scroll pane instead of the direct formContainer
       leftPanel.add(logoPanel, BorderLayout.NORTH);
       leftPanel.add(titlePanel, BorderLayout.CENTER);
       leftPanel.add(scrollPane, BorderLayout.SOUTH);
       
       // Right panel - Animated waves with text
       JPanel rightPanel = new JPanel(new BorderLayout()) {
           @Override
           protected void paintComponent(Graphics g) {
               super.paintComponent(g);
               Graphics2D g2d = (Graphics2D) g;
               g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               
               // Draw background
               g2d.setColor(DARK_BG);
               g2d.fillRect(0, 0, getWidth(), getHeight());
               
               // Draw animated waves
               drawWaves(g2d, getWidth(), getHeight());
               
               // Draw text overlay
               g2d.setColor(Color.WHITE);
               g2d.setFont(new Font("Arial", Font.BOLD, 72));
               
               String text = "CNSC";
               FontMetrics fm = g2d.getFontMetrics();
               int textWidth = fm.stringWidth(text);
               g2d.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2 - 30);
               
               text = "Motorpool";
               textWidth = fm.stringWidth(text);
               g2d.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2 + 60);
           }
       };
       rightPanel.setBackground(DARK_BG);
       
       // Add panels to split pane
       splitPane.setLeftComponent(leftPanel);
       splitPane.setRightComponent(rightPanel);
       
       // Add split pane to main panel
       mainPanel.add(splitPane, BorderLayout.CENTER);
       
       // Add exit button in top-right corner
       JButton exitButton = new JButton("×");
       exitButton.setFont(new Font("Arial", Font.BOLD, 24));
       exitButton.setForeground(Color.WHITE);
       exitButton.setBackground(new Color(0, 0, 0, 0));
       exitButton.setBorderPainted(false);
       exitButton.setContentAreaFilled(false);
       exitButton.setFocusPainted(false);
       exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
       exitButton.addActionListener(e -> {
           dispose();
           
           // Show login frame again
           for (Window window : Window.getWindows()) {
               if (window instanceof LoginFrame) {
                   window.setVisible(true);
                   break;
               }
           }
       });
       
       JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
       topRightPanel.setOpaque(false);
       topRightPanel.add(exitButton);
       
       mainPanel.add(topRightPanel, BorderLayout.NORTH);
       
       // Set content pane
       setContentPane(mainPanel);
       
       // Add action listeners
       registerButton.addActionListener(e -> registerUser());
       cancelButton.addActionListener(e -> {
           dispose();
           
           // Show login frame again
           for (Window window : Window.getWindows()) {
               if (window instanceof LoginFrame) {
                   window.setVisible(true);
                   break;
               }
           }
       });
   }
   
   private JTextField createStyledTextField(String placeholder) {
       JTextField textField = new JTextField(15);
       textField.setFont(new Font("Arial", Font.PLAIN, 16));
       textField.setForeground(Color.WHITE);
       textField.setBackground(FIELD_BG);
       textField.setBorder(BorderFactory.createCompoundBorder(
           BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
           BorderFactory.createEmptyBorder(12, 15, 12, 15)
       ));
       textField.setCaretColor(Color.WHITE);
       
       // Add placeholder text
       textField.setText(placeholder);
       textField.setForeground(new Color(150, 150, 150));
       
       textField.addFocusListener(new FocusAdapter() {
           @Override
           public void focusGained(FocusEvent e) {
               if (textField.getText().equals(placeholder)) {
                   textField.setText("");
                   textField.setForeground(Color.WHITE);
               }
           }
           
           @Override
           public void focusLost(FocusEvent e) {
               if (textField.getText().isEmpty()) {
                   textField.setText(placeholder);
                   textField.setForeground(new Color(150, 150, 150));
               }
           }
       });
       
       return textField;
   }
   
   private JPasswordField createStyledPasswordField(String placeholder) {
       JPasswordField passwordField = new JPasswordField(15);
       passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
       passwordField.setForeground(Color.WHITE);
       passwordField.setBackground(FIELD_BG);
       passwordField.setBorder(BorderFactory.createCompoundBorder(
           BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
           BorderFactory.createEmptyBorder(12, 15, 12, 15)
       ));
       passwordField.setCaretColor(Color.WHITE);
       passwordField.setEchoChar((char) 0); // Show text initially for placeholder
       
       // Add placeholder text
       passwordField.setText(placeholder);
       passwordField.setForeground(new Color(150, 150, 150));
       
       passwordField.addFocusListener(new FocusAdapter() {
           @Override
           public void focusGained(FocusEvent e) {
               if (new String(passwordField.getPassword()).equals(placeholder)) {
                   passwordField.setText("");
                   passwordField.setEchoChar('•'); // Set echo char when typing
                   passwordField.setForeground(Color.WHITE);
               }
           }
           
           @Override
           public void focusLost(FocusEvent e) {
               if (new String(passwordField.getPassword()).isEmpty()) {
                   passwordField.setText(placeholder);
                   passwordField.setEchoChar((char) 0); // Remove echo char for placeholder
                   passwordField.setForeground(new Color(150, 150, 150));
               }
           }
       });
       
       return passwordField;
   }
   
   // Modify the formatRoleText method to ensure proper text display
   private String formatRoleText(User.UserRole role) {
       String roleText = role.toString();
       return roleText.charAt(0) + roleText.substring(1).toLowerCase();
   }
   
   // Update the getRoleIcon method to create more visible icons
   private Icon getRoleIcon(User.UserRole role) {
       // Create simple colored square icons for different roles
       return new Icon() {
           @Override
           public void paintIcon(Component c, Graphics g, int x, int y) {
               Graphics2D g2d = (Graphics2D) g;
               g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
           
               Color iconColor;
               if (role == User.UserRole.ADMIN) {
                   iconColor = ACCENT_PINK; // Pink for admin
               } else {
                   iconColor = ACCENT_BLUE; // Blue for regular users
               }
           
               g2d.setColor(iconColor);
               g2d.fillRoundRect(x, y, 16, 16, 4, 4);
           }
       
           @Override
           public int getIconWidth() {
               return 16;
           }
       
           @Override
           public int getIconHeight() {
               return 16;
           }
       };
   }
   
   private void drawWaves(Graphics2D g2d, int width, int height) {
       // Create gradient for waves
       GradientPaint blueGradient = new GradientPaint(
           0, 0, ACCENT_BLUE,
           width, height, ACCENT_PINK
       );
       g2d.setPaint(blueGradient);
       
       // Draw multiple wave layers with different phases
       drawWaveLayer(g2d, width, height, 0.3f, 120, waveOffset);
       drawWaveLayer(g2d, width, height, 0.4f, 100, waveOffset * 0.8f);
       drawWaveLayer(g2d, width, height, 0.6f, 80, waveOffset * 1.2f);
   }
   
   private void drawWaveLayer(Graphics2D g2d, int width, int height, float opacity, int amplitude, float offset) {
       g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
       
       Path2D path = new Path2D.Float();
       path.moveTo(0, height / 2);
       
       // Create wave path
       for (int x = 0; x <= width; x += 10) {
           double y = Math.sin((x * 0.015) + offset) * amplitude + (height / 2);
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
           waveOffset += 0.1f;
           if (waveOffset > 1000) {
               waveOffset = 0;
           }
           repaint();
       });
       waveAnimationTimer.start();
   }
   
   private void registerUser() {
       // Get input values, handling placeholder text
       String username = usernameField.getText();
       String password = new String(passwordField.getPassword());
       String confirmPassword = new String(confirmPasswordField.getPassword());
       String fullName = fullNameField.getText();
       String email = emailField.getText();
       String phone = phoneField.getText();
       User.UserRole role = (User.UserRole) roleComboBox.getSelectedItem();
       
       // Check if fields contain placeholder text
       if (username.equals("Username") || password.equals("Password") || 
           confirmPassword.equals("Confirm Password") || fullName.equals("Full Name") || 
           email.equals("Email")) {
           showErrorMessage("Please fill in all required fields.");
           return;
       }
       
       // Validation
       if (!password.equals(confirmPassword)) {
           showErrorMessage("Passwords do not match.");
           return;
       }
       
       if (DataManager.getInstance().getUserByUsername(username) != null) {
           showErrorMessage("Username already exists. Please choose a different username.");
           return;
       }
       
       // Create new user
       String userId = "U-" + UUID.randomUUID().toString().substring(0, 8);
       User user = new User(userId, username, password, fullName, email, phone, role);
       
       // Save user
       DataManager.getInstance().addUser(user);
       
       // Show success message
       showSuccessMessage("Registration successful! You can now log in.");
       
       // Close registration window after a delay
       Timer closeTimer = new Timer(2000, e -> {
           dispose();
           
           // Show login frame again
           for (Window window : Window.getWindows()) {
               if (window instanceof LoginFrame) {
                   window.setVisible(true);
                   break;
               }
           }
       });
       closeTimer.setRepeats(false);
       closeTimer.start();
   }
   
   private void showErrorMessage(String message) {
       // Create a modern error notification
       JDialog errorDialog = new JDialog(this, "", false);
       errorDialog.setUndecorated(true);
       errorDialog.setSize(350, 80);
       errorDialog.setLocationRelativeTo(this);
       
       JPanel errorPanel = new JPanel(new BorderLayout());
       errorPanel.setBackground(new Color(220, 53, 69));
       errorPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
       
       JLabel errorLabel = new JLabel(message);
       errorLabel.setForeground(Color.WHITE);
       errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
       errorLabel.setHorizontalAlignment(JLabel.CENTER);
       
       errorPanel.add(errorLabel, BorderLayout.CENTER);
       
       // Add close button
       JButton closeButton = new JButton("×");
       closeButton.setFont(new Font("Arial", Font.BOLD, 16));
       closeButton.setForeground(Color.WHITE);
       closeButton.setContentAreaFilled(false);
       closeButton.setBorderPainted(false);
       closeButton.setFocusPainted(false);
       closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
       closeButton.addActionListener(e -> errorDialog.dispose());
       
       errorPanel.add(closeButton, BorderLayout.EAST);
       
       errorDialog.setContentPane(errorPanel);
       errorDialog.setVisible(true);
       
       // Auto-close after 3 seconds
       Timer timer = new Timer(3000, e -> errorDialog.dispose());
       timer.setRepeats(false);
       timer.start();
       
       // Shake the registration frame
       final int originalX = getX();
       final int shakeDistance = 10;
       final int shakeSpeed = 50;
       
       Timer shakeTimer = new Timer(shakeSpeed, new ActionListener() {
           int count = 0;
           int direction = 1;
           
           @Override
           public void actionPerformed(ActionEvent e) {
               if (count > 5) {
                   ((Timer) e.getSource()).stop();
                   setLocation(originalX, getY());
               } else {
                   setLocation(originalX + (direction * shakeDistance), getY());
                   direction *= -1;
                   count++;
               }
           }
       });
       
       shakeTimer.start();
   }
   
   private void showSuccessMessage(String message) {
       // Create a modern success notification
       JDialog successDialog = new JDialog(this, "", false);
       successDialog.setUndecorated(true);
       successDialog.setSize(350, 80);
       successDialog.setLocationRelativeTo(this);
       
       JPanel successPanel = new JPanel(new BorderLayout());
       successPanel.setBackground(new Color(40, 167, 69));
       successPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
       
       JLabel successLabel = new JLabel(message);
       successLabel.setForeground(Color.WHITE);
       successLabel.setFont(new Font("Arial", Font.BOLD, 14));
       successLabel.setHorizontalAlignment(JLabel.CENTER);
       
       successPanel.add(successLabel, BorderLayout.CENTER);
       
       // Add close button
       JButton closeButton = new JButton("×");
       closeButton.setFont(new Font("Arial", Font.BOLD, 16));
       closeButton.setForeground(Color.WHITE);
       closeButton.setContentAreaFilled(false);
       closeButton.setBorderPainted(false);
       closeButton.setFocusPainted(false);
       closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
       closeButton.addActionListener(e -> successDialog.dispose());
       
       successPanel.add(closeButton, BorderLayout.EAST);
       
       successDialog.setContentPane(successPanel);
       successDialog.setVisible(true);
       
       // Auto-close after 3 seconds
       Timer timer = new Timer(3000, e -> successDialog.dispose());
       timer.setRepeats(false);
       timer.start();
   }
}
