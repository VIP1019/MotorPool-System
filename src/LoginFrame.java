import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.User;

/**
 * Modern login screen with dark theme and wave animation
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JCheckBox rememberMeCheckbox;
    
    // Animation components
    private Timer waveAnimationTimer;
    private float waveOffset = 0.0f;
    
    // Custom colors - modern dark theme with vibrant accents
    public static final Color DARK_BG = new Color(27, 20, 70);       // Dark purple-blue
    public static final Color ACCENT_PINK = new Color(255, 64, 180); // Vibrant pink
    public static final Color ACCENT_BLUE = new Color(64, 80, 255);  // Vibrant blue
    public static final Color FIELD_BG = new Color(45, 35, 95);      // Slightly lighter than background
    public static final Color TEXT_COLOR = new Color(255, 255, 255); // White text
    public static final Color SIDEBAR_COLOR = new Color(35, 28, 80); // Slightly lighter than background for sidebar
    
    public LoginFrame() {
        setTitle("CNSC Motorpool - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set to full screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true); // Remove window decorations for full screen
        
        // Setup shutdown hook to save data when application exits
        setupShutdownHook();
        
        initComponents();
        startWaveAnimation();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DataManager.getInstance().saveAllData();
                System.exit(0);
            }
        });
        
        // Add key listener for ESC to exit full screen
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    DataManager.getInstance().saveAllData();
                    dispose();
                    System.exit(0);
                }
            }
        });
        setFocusable(true);
        
        setVisible(true);
    }
    
    private void initComponents() {
        // Create main panel with dark background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BG);
        
        // Create split layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);
        splitPane.setEnabled(false);
        
        // Left panel - Login form
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
        
        // User icon in circle
        JPanel userIconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        userIconPanel.setOpaque(false);
        userIconPanel.setBorder(new EmptyBorder(50, 0, 50, 0));
        
        JPanel circlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw white circle
                g2d.setColor(Color.WHITE);
                g2d.fillOval(0, 0, getWidth(), getHeight());
                
                // Draw user icon
                g2d.setColor(DARK_BG);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                // Head
                g2d.fillOval(centerX - 15, centerY - 25, 30, 30);
                
                // Body
                g2d.fillOval(centerX - 25, centerY + 5, 50, 30);
            }
        };
        circlePanel.setPreferredSize(new Dimension(100, 100));
        circlePanel.setOpaque(false);
        
        userIconPanel.add(circlePanel);
        
        // Login form
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
        
        // Remember me checkbox
        rememberMeCheckbox = new JCheckBox("Remember me");
        rememberMeCheckbox.setFont(new Font("Arial", Font.PLAIN, 14));
        rememberMeCheckbox.setForeground(new Color(200, 200, 200));
        rememberMeCheckbox.setOpaque(false);
        rememberMeCheckbox.setFocusPainted(false);
        rememberMeCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Login button
        loginButton = createStyledButton("Log In", ACCENT_PINK);
        JPanel loginButtonWrapper = new JPanel(new BorderLayout());
        loginButtonWrapper.setOpaque(false);
        loginButtonWrapper.add(loginButton);
        loginButtonWrapper.setMaximumSize(new Dimension(350, 50));
        
        // Register button (text only)
        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.PLAIN, 16));
        registerButton.setForeground(Color.WHITE);
        registerButton.setContentAreaFilled(false);
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components to form with more spacing
        formPanel.add(Box.createVerticalStrut(30)); // Increased from 20
        formPanel.add(usernameWrapper);
        formPanel.add(Box.createVerticalStrut(30)); // Increased from 20
        formPanel.add(passwordWrapper);
        formPanel.add(Box.createVerticalStrut(20)); // Increased from 15
        formPanel.add(rememberMeCheckbox);
        formPanel.add(Box.createVerticalStrut(40)); // Increased from 30
        formPanel.add(loginButtonWrapper);
        formPanel.add(Box.createVerticalStrut(30)); // Increased from 25
        formPanel.add(registerButton);
        formPanel.add(Box.createVerticalStrut(20)); // Added extra space at the bottom
        
        // Center the form with better spacing
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);
        formContainer.setBorder(new EmptyBorder(0, 50, 50, 50)); // Add more padding
        formContainer.add(formPanel);

        // Add a scroll pane to handle overflow if the screen is too small
        JScrollPane scrollPane = new JScrollPane(formContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Modify the left panel to use the scroll pane
        leftPanel.add(logoPanel, BorderLayout.NORTH);
        leftPanel.add(userIconPanel, BorderLayout.CENTER);
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
            DataManager.getInstance().saveAllData();
            System.exit(0);
        });
        
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.setOpaque(false);
        topRightPanel.add(exitButton);
        
        mainPanel.add(topRightPanel, BorderLayout.NORTH);
        
        // Set content pane
        setContentPane(mainPanel);
        
        // Add action listeners
        loginButton.addActionListener(e -> attemptLogin());
        registerButton.addActionListener(e -> openRegistration());
        
        // Set default button
        getRootPane().setDefaultButton(loginButton);
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
    
    public static JButton createStyledButton(String text, Color color) {
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
        
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(350, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
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
    
    private void attemptLogin() {
        // Get username and password, handling placeholder text
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // Check if fields contain placeholder text
        if (username.equals("Username") || password.equals("Password")) {
            showErrorMessage("Please enter both username and password.");
            return;
        }
        
        User user = DataManager.getInstance().getUserByUsername(username);
        
        if (user != null && user.getPassword().equals(password)) {
            if (!user.isActive()) {
                showErrorMessage("Your account has been deactivated. Please contact an administrator.");
                return;
            }
            
            // Convert legacy roles to current roles
            if (user.getRole() != User.UserRole.ADMIN && user.getRole() != User.UserRole.USER) {
                user.setRole(user.getRole().toCurrentRole());
                DataManager.getInstance().updateUser(user);
            }
            
            DataManager.getInstance().setCurrentUser(user);
            openAppropriateInterface(user);
        } else {
            showErrorMessage("Invalid username or password. Please try again.");
            if (!password.equals("Password")) {
                passwordField.setText("");
                passwordField.setEchoChar('•');
            }
        }
    }
    
    private void openRegistration() {
        SwingUtilities.invokeLater(() -> {
            RegistrationFrame registrationFrame = new RegistrationFrame();
            registrationFrame.setVisible(true);
            setVisible(false);
        });
    }
    
    private void openAppropriateInterface(User user) {
        // Stop animation timer
        if (waveAnimationTimer != null && waveAnimationTimer.isRunning()) {
            waveAnimationTimer.stop();
        }
        
        // Open appropriate interface based on user role
        SwingUtilities.invokeLater(() -> {
            if (user.isAdmin()) {
                AdminMainFrame adminFrame = new AdminMainFrame();
                adminFrame.setVisible(true);
            } else {
                UserMainFrame userFrame = new UserMainFrame();
                userFrame.setVisible(true);
            }
            dispose();
        });
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
        
        // Shake the login frame
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
    
    // Inner class for creating a custom image icon
    private ImageIcon createImageIcon(int width, int height, Color color, Shape shape) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.fill(shape);
        g2d.dispose();
        return new ImageIcon(image);
    }

    // Add a method to properly handle application shutdown
    private void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Application shutting down, saving data...");
            DataManager.getInstance().saveAllData();
        }));
    }

    public static void main(String[] args) {
        // Ensure the GUI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Initialize data manager and load data
                DataManager dataManager = DataManager.getInstance();
                
                // Start auto-save thread
                AutoSaveThread autoSaveThread = new AutoSaveThread();
                autoSaveThread.start();
                
                // Create and display the login frame
                new LoginFrame();
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "An error occurred while starting the application: " + e.getMessage(),
                    "Startup Error", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
