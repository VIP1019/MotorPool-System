import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.User;

/**
* Modern panel for managing users with card-based UI
*/
public class UserManagementPanel extends JPanel {
   private MainFrameInterface parentFrame;
   private JPanel usersContainer;
   private JScrollPane scrollPane;
   private JTextField searchField;
   private JComboBox<String> roleFilterComboBox;
   private JButton addButton;
   private JButton refreshButton;
   
   // Users currently displayed
   private List<User> currentUsers;
   
   // Role icons
   private ImageIcon adminIcon;
   private ImageIcon userIcon;
   private ImageIcon studentIcon;
   private ImageIcon facultyIcon;
   private ImageIcon staffIcon;
   
   public UserManagementPanel(MainFrameInterface parentFrame) {
       this.parentFrame = parentFrame;
       setLayout(new BorderLayout());
       setBackground(new Color(25, 20, 60)); // Darker background color
       
       // Check if current user is admin
       if (!DataManager.getInstance().getCurrentUser().isAdmin()) {
           JLabel accessDeniedLabel = new JLabel("Access Denied: You must be an administrator to manage users.");
           accessDeniedLabel.setHorizontalAlignment(JLabel.CENTER);
           accessDeniedLabel.setFont(new Font("Arial", Font.BOLD, 16));
           accessDeniedLabel.setForeground(Color.WHITE);
           add(accessDeniedLabel, BorderLayout.CENTER);
           return;
       }
       
       loadIcons();
       initComponents();
       loadUsers();
   }
   
   private void loadIcons() {
       // Create role icons
       adminIcon = createRoleIcon(new Color(255, 64, 180), "ADMIN");
       userIcon = createRoleIcon(new Color(64, 80, 255), "USER");
       studentIcon = createRoleIcon(new Color(64, 180, 64), "STUDENT");
       facultyIcon = createRoleIcon(new Color(255, 180, 64), "FACULTY");
       staffIcon = createRoleIcon(new Color(180, 64, 255), "STAFF");
   }
   
   private ImageIcon createRoleIcon(Color color, String role) {
       int size = 48;
       BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
       Graphics2D g2d = image.createGraphics();
       
       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
       
       // Draw circle background
       g2d.setColor(color);
       g2d.fillOval(0, 0, size, size);
       
       // Draw text
       g2d.setColor(Color.WHITE);
       g2d.setFont(new Font("Arial", Font.BOLD, 12));
       FontMetrics fm = g2d.getFontMetrics();
       int textWidth = fm.stringWidth(role);
       g2d.drawString(role, (size - textWidth) / 2, size / 2 + 5);
       
       g2d.dispose();
       return new ImageIcon(image);
   }
   
   private void initComponents() {
       // Create header panel with search and filters
       JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
       headerPanel.setBackground(new Color(30, 25, 70));
       headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
       
       // Title label
       JLabel titleLabel = new JLabel("User Management");
       titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
       titleLabel.setForeground(Color.WHITE);
       
       // Search and filter panel
       JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
       searchFilterPanel.setOpaque(false);
       
       // Search field with modern styling
       searchField = new JTextField(20);
       searchField.setFont(new Font("Arial", Font.PLAIN, 14));
       searchField.setForeground(Color.WHITE);
       searchField.setBackground(new Color(50, 45, 90));
       searchField.setBorder(BorderFactory.createCompoundBorder(
           BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
           BorderFactory.createEmptyBorder(10, 15, 10, 15)
       ));
       searchField.setCaretColor(Color.WHITE);
       
       // Add search icon and placeholder
       searchField.setText("Search users...");
       searchField.setForeground(new Color(150, 150, 150));
       
       searchField.addFocusListener(new FocusAdapter() {
           @Override
           public void focusGained(FocusEvent e) {
               if (searchField.getText().equals("Search users...")) {
                   searchField.setText("");
                   searchField.setForeground(Color.WHITE);
               }
           }
           
           @Override
           public void focusLost(FocusEvent e) {
               if (searchField.getText().isEmpty()) {
                   searchField.setText("Search users...");
                   searchField.setForeground(new Color(150, 150, 150));
               }
           }
       });
       
       // Search button
       JButton searchButton = createStyledButton("Search", new Color(255, 50, 180));
       searchButton.addActionListener(e -> searchUsers());
       
       // Filter by role dropdown
       JLabel filterLabel = new JLabel("Filter by Role:");
       filterLabel.setFont(new Font("Arial", Font.PLAIN, 14));
       filterLabel.setForeground(Color.WHITE);
       
       String[] roles = {"All Roles", "ADMIN", "USER", "STUDENT", "FACULTY", "STAFF"};
       roleFilterComboBox = new JComboBox<>(roles);
       roleFilterComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
       roleFilterComboBox.setBackground(new Color(50, 45, 90));
       roleFilterComboBox.setForeground(Color.WHITE);
       roleFilterComboBox.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true));
       roleFilterComboBox.addActionListener(e -> filterUsers());
       
       // Add components to search panel
       searchFilterPanel.add(searchField);
       searchFilterPanel.add(searchButton);
       searchFilterPanel.add(Box.createHorizontalStrut(20));
       searchFilterPanel.add(filterLabel);
       searchFilterPanel.add(roleFilterComboBox);
       
       // Add title and search panel to header
       headerPanel.add(titleLabel, BorderLayout.WEST);
       headerPanel.add(searchFilterPanel, BorderLayout.EAST);
       
       // Create users container with grid layout
       usersContainer = new JPanel();
       usersContainer.setLayout(new WrapLayout(FlowLayout.LEFT, 20, 20));
       usersContainer.setBackground(parentFrame.getBackgroundColor());
       
       // Add scroll pane for users container
       scrollPane = new JScrollPane(usersContainer);
       scrollPane.setBorder(null);
       scrollPane.getVerticalScrollBar().setUnitIncrement(16);
       scrollPane.getViewport().setBackground(parentFrame.getBackgroundColor());
       
       // Create button panel
       JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
       buttonPanel.setOpaque(false);
       
       addButton = createStyledButton("Add User", new Color(255, 50, 180));
       refreshButton = createStyledButton("Refresh", new Color(50, 150, 255));
       
       buttonPanel.add(addButton);
       buttonPanel.add(refreshButton);
       
       // Add action listeners
       addButton.addActionListener(e -> showAddUserDialog());
       refreshButton.addActionListener(e -> loadUsers());
       
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
               g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
               
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
       button.setPreferredSize(new Dimension(130, 40));
       button.setCursor(new Cursor(Cursor.HAND_CURSOR));
       
       return button;
   }
   
   public void loadUsers() {
       SwingWorker<List<User>, Void> worker = new SwingWorker<>() {
           @Override
           protected List<User> doInBackground() {
               // Load users
               return DataManager.getInstance().getAllUsers();
           }
           
           @Override
           protected void done() {
               try {
                   currentUsers = get();
                   displayUsers(currentUsers);
                   parentFrame.setStatusMessage("Users loaded successfully");
               } catch (Exception e) {
                   e.printStackTrace();
                   JOptionPane.showMessageDialog(UserManagementPanel.this,
                       "Error loading users: " + e.getMessage(),
                       "Error",
                       JOptionPane.ERROR_MESSAGE);
               }
           }
       };
       
       worker.execute();
   }
   
   private void displayUsers(List<User> users) {
       // Clear existing user cards
       usersContainer.removeAll();
       
       if (users.isEmpty()) {
           JLabel noUsersLabel = new JLabel("No users found");
           noUsersLabel.setFont(new Font("Arial", Font.BOLD, 18));
           noUsersLabel.setForeground(Color.WHITE);
           usersContainer.add(noUsersLabel);
       } else {
           // Add user cards
           for (User user : users) {
               usersContainer.add(createUserCard(user));
           }
       }
       
       // Refresh UI
       usersContainer.revalidate();
       usersContainer.repaint();
   }
   
   private JPanel createUserCard(User user) {
    // Create card panel with rounded corners and improved styling
    JPanel cardPanel = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw rounded rectangle background with darker color
            g2d.setColor(new Color(40, 35, 80));
            g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
        }
    };
    cardPanel.setOpaque(false);
    cardPanel.setPreferredSize(new Dimension(280, 180));
    cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
    // Create top panel for user info
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setOpaque(false);
    
    // Create name label with larger font
    JLabel nameLabel = new JLabel(user.getFullName());
    nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
    nameLabel.setForeground(Color.WHITE);
    
    // Create username label
    JLabel usernameLabel = new JLabel("@" + user.getUsername());
    usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    usernameLabel.setForeground(new Color(180, 180, 200));
    
    // Create email label
    JLabel emailLabel = new JLabel(user.getEmail());
    emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    emailLabel.setForeground(new Color(180, 180, 200));
    
    // Create role icon panel
    JPanel roleIconPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw circle with color based on role
            Color roleColor = getRoleColor(user.getRole());
            g2d.setColor(roleColor);
            g2d.fillOval(0, 0, getWidth(), getHeight());
            
            // Draw role text
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2d.getFontMetrics();
            String roleText = getRoleShortText(user.getRole());
            int textWidth = fm.stringWidth(roleText);
            int textHeight = fm.getHeight();
            g2d.drawString(roleText, (getWidth() - textWidth) / 2, 
                         (getHeight() - textHeight) / 2 + fm.getAscent());
        }
    };
    roleIconPanel.setPreferredSize(new Dimension(50, 50));
    roleIconPanel.setOpaque(false);
    
    // Create info panel for username and email
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setOpaque(false);
    infoPanel.add(nameLabel);
    infoPanel.add(Box.createVerticalStrut(5));
    infoPanel.add(usernameLabel);
    infoPanel.add(Box.createVerticalStrut(5));
    infoPanel.add(emailLabel);
    
    // Add role icon and info to top panel
    topPanel.add(roleIconPanel, BorderLayout.WEST);
    topPanel.add(infoPanel, BorderLayout.CENTER);
    
    // Create middle panel for role and status
    JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    middlePanel.setOpaque(false);
    
    // Create role label
    JLabel roleLabel = new JLabel("Role: " + user.getRole());
    roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
    roleLabel.setForeground(getRoleColor(user.getRole()));
    
    // Create status panel
    JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    statusPanel.setOpaque(false);
    
    JLabel statusLabel = new JLabel("Status:");
    statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
    statusLabel.setForeground(Color.WHITE);
    
    JPanel statusIndicator = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw circle with color based on active status
            g2d.setColor(user.isActive() ? new Color(0, 200, 0) : new Color(200, 0, 0));
            g2d.fillOval(0, 0, getWidth(), getHeight());
        }
    };
    statusIndicator.setPreferredSize(new Dimension(12, 12));
    statusIndicator.setOpaque(false);
    
    JLabel activeLabel = new JLabel(user.isActive() ? "Active" : "Inactive");
    activeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    activeLabel.setForeground(user.isActive() ? new Color(0, 200, 0) : new Color(200, 0, 0));
    
    statusPanel.add(statusLabel);
    statusPanel.add(statusIndicator);
    statusPanel.add(activeLabel);
    
    // Add role and status to middle panel
    middlePanel.add(roleLabel);
    middlePanel.add(Box.createHorizontalStrut(20));
    middlePanel.add(statusPanel);
    
    // Create buttons panel
    JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
    buttonsPanel.setOpaque(false);
    
    JButton editButton = createActionButton("Edit", new Color(64, 80, 255));
    JButton resetButton = createActionButton("Reset Password", new Color(64, 180, 64));
    
    // Add action listeners
    editButton.addActionListener(e -> showEditUserDialog(user));
    resetButton.addActionListener(e -> resetUserPassword(user));
    
    buttonsPanel.add(editButton);
    buttonsPanel.add(resetButton);
    
    // Add components to card
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setOpaque(false);
    contentPanel.add(topPanel);
    contentPanel.add(Box.createVerticalStrut(10));
    contentPanel.add(middlePanel);
    contentPanel.add(Box.createVerticalStrut(15));
    contentPanel.add(buttonsPanel);
    
    cardPanel.add(contentPanel, BorderLayout.CENTER);
    
    return cardPanel;
}
   
   private Color getRoleColor(User.UserRole role) {
    switch (role) {
        case ADMIN:
            return new Color(255, 50, 180); // Bright pink
        case STUDENT:
            return new Color(100, 220, 100); // Bright green
        case FACULTY:
            return new Color(255, 180, 50); // Bright orange
        case STAFF:
            return new Color(180, 100, 255); // Bright purple
        default:
            return new Color(50, 150, 255); // Bright blue
    }
}
   
   private JButton createActionButton(String text, Color color) {
       JButton button = new JButton(text) {
           @Override
           protected void paintComponent(Graphics g) {
               Graphics2D g2d = (Graphics2D) g;
               g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               
               if (!isEnabled()) {
                   g2d.setColor(new Color(150, 150, 150));
               } else {
                   g2d.setColor(getModel().isPressed() ? color.darker() : color);
               }
               
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
       
       button.setFont(new Font("Arial", Font.BOLD, 12));
       button.setForeground(Color.WHITE);
       button.setBackground(color);
       button.setFocusPainted(false);
       button.setBorderPainted(false);
       button.setContentAreaFilled(false);
       button.setPreferredSize(new Dimension(120, 30));
       button.setCursor(new Cursor(Cursor.HAND_CURSOR));
       
       return button;
   }
   
   private void searchUsers() {
       String searchTerm = searchField.getText().toLowerCase();
       if (searchTerm.equals("search users...")) {
           searchTerm = "";
       }
       
       final String term = searchTerm;
       
       if (term.isEmpty() && roleFilterComboBox.getSelectedIndex() == 0) {
           // If no search term and no filter, load all users
           loadUsers();
           return;
       }
       
       // Filter users based on search term and role
       List<User> filteredUsers = new ArrayList<>();
       
       for (User user : currentUsers) {
           boolean matchesSearch = term.isEmpty() ||
               user.getUsername().toLowerCase().contains(term) ||
               user.getFullName().toLowerCase().contains(term) ||
               user.getEmail().toLowerCase().contains(term);
               
           boolean matchesRole = roleFilterComboBox.getSelectedIndex() == 0 ||
               user.getRole().toString().equals(roleFilterComboBox.getSelectedItem());
               
           if (matchesSearch && matchesRole) {
               filteredUsers.add(user);
           }
       }
       
       displayUsers(filteredUsers);
       parentFrame.setStatusMessage("Found " + filteredUsers.size() + " users matching search criteria");
   }
   
   private void filterUsers() {
       // This will apply both the search term and the role filter
       searchUsers();
   }
   
   private void showAddUserDialog() {
       // Create a modern dialog for adding a new user
       JDialog dialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "Add New User", true);
       dialog.setSize(500, 550);
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
       JTextField usernameField = createStyledTextField("");
       JPasswordField passwordField = createStyledPasswordField();
       JPasswordField confirmPasswordField = createStyledPasswordField();
       JTextField fullNameField = createStyledTextField("");
       JTextField emailField = createStyledTextField("");
       JTextField phoneField = createStyledTextField("");
       
       JComboBox<User.UserRole> roleComboBox = new JComboBox<>(User.UserRole.values());
       styleComboBox(roleComboBox);
       
       JCheckBox activeCheckBox = new JCheckBox("Active");
       activeCheckBox.setFont(new Font("Arial", Font.BOLD, 14));
       activeCheckBox.setForeground(Color.WHITE);
       activeCheckBox.setBackground(parentFrame.getFieldBgColor());
       activeCheckBox.setSelected(true);
       
       // Add form fields to panel
       addFormField(formPanel, gbc, 0, "Username:", usernameField);
       addFormField(formPanel, gbc, 1, "Password:", passwordField);
       addFormField(formPanel, gbc, 2, "Confirm Password:", confirmPasswordField);
       addFormField(formPanel, gbc, 3, "Full Name:", fullNameField);
       addFormField(formPanel, gbc, 4, "Email:", emailField);
       addFormField(formPanel, gbc, 5, "Phone:", phoneField);
       addFormField(formPanel, gbc, 6, "Role:", roleComboBox);
       addFormField(formPanel, gbc, 7, "Status:", activeCheckBox);
       
       // Create buttons panel
       JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
       buttonsPanel.setOpaque(false);
       
       JButton saveButton = createStyledButton("Save", parentFrame.getPrimaryAccentColor());
       JButton cancelButton = createStyledButton("Cancel", new Color(150, 150, 150));
       
       // Add action listeners
       saveButton.addActionListener(e -> {
           // Validate input
           if (usernameField.getText().trim().isEmpty() ||
               passwordField.getPassword().length == 0 ||
               confirmPasswordField.getPassword().length == 0 ||
               fullNameField.getText().trim().isEmpty() ||
               emailField.getText().trim().isEmpty()) {
               showErrorMessage(dialog, "Please fill in all required fields.");
               return;
           }
           
           // Check if passwords match
           if (!new String(passwordField.getPassword()).equals(new String(confirmPasswordField.getPassword()))) {
               showErrorMessage(dialog, "Passwords do not match.");
               return;
           }
           
           // Check if username already exists
           if (DataManager.getInstance().getUserByUsername(usernameField.getText().trim()) != null) {
               showErrorMessage(dialog, "Username already exists. Please choose a different username.");
               return;
           }
           
           // Create new user
           String userId = "U-" + UUID.randomUUID().toString().substring(0, 8);
           User user = new User(
               userId,
               usernameField.getText().trim(),
               new String(passwordField.getPassword()),
               fullNameField.getText().trim(),
               emailField.getText().trim(),
               phoneField.getText().trim(),
               (User.UserRole) roleComboBox.getSelectedItem()
           );
           
           user.setActive(activeCheckBox.isSelected());
           
           // Save user
           DataManager.getInstance().addUser(user);
           
           // Close dialog and refresh
           dialog.dispose();
           loadUsers();
           
           // Show success message
           showSuccessMessage("User added successfully");
       });
       
       cancelButton.addActionListener(e -> dialog.dispose());
       
       buttonsPanel.add(saveButton);
       buttonsPanel.add(cancelButton);
       
       // Add components to content panel
       contentPanel.add(new JLabel("Add New User", JLabel.CENTER) {{
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
   
   private void showEditUserDialog(User user) {
       // Create a modern dialog for editing a user
       JDialog dialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "Edit User", true);
       dialog.setSize(500, 500);
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
       JTextField usernameField = createStyledTextField(user.getUsername());
       JTextField fullNameField = createStyledTextField(user.getFullName());
       JTextField emailField = createStyledTextField(user.getEmail());
       JTextField phoneField = createStyledTextField(user.getPhoneNumber());
       
       JComboBox<User.UserRole> roleComboBox = new JComboBox<>(User.UserRole.values());
       roleComboBox.setSelectedItem(user.getRole());
       styleComboBox(roleComboBox);
       
       JCheckBox activeCheckBox = new JCheckBox("Active");
       activeCheckBox.setFont(new Font("Arial", Font.BOLD, 14));
       activeCheckBox.setForeground(Color.WHITE);
       activeCheckBox.setBackground(parentFrame.getFieldBgColor());
       activeCheckBox.setSelected(user.isActive());
       
       // Add form fields to panel
       addFormField(formPanel, gbc, 0, "Username:", usernameField);
       addFormField(formPanel, gbc, 1, "Full Name:", fullNameField);
       addFormField(formPanel, gbc, 2, "Email:", emailField);
       addFormField(formPanel, gbc, 3, "Phone:", phoneField);
       addFormField(formPanel, gbc, 4, "Role:", roleComboBox);
       addFormField(formPanel, gbc, 5, "Status:", activeCheckBox);
       
       // Create buttons panel
       JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
       buttonsPanel.setOpaque(false);
       
       JButton saveButton = createStyledButton("Save", parentFrame.getPrimaryAccentColor());
       JButton cancelButton = createStyledButton("Cancel", new Color(150, 150, 150));
       
       // Add action listeners
       saveButton.addActionListener(e -> {
           // Validate input
           if (usernameField.getText().trim().isEmpty() ||
               fullNameField.getText().trim().isEmpty() ||
               emailField.getText().trim().isEmpty()) {
               showErrorMessage(dialog, "Please fill in all required fields.");
               return;
           }
           
           // Check if username already exists (if changed)
           if (!usernameField.getText().trim().equals(user.getUsername()) &&
               DataManager.getInstance().getUserByUsername(usernameField.getText().trim()) != null) {
               showErrorMessage(dialog, "Username already exists. Please choose a different username.");
               return;
           }
           
           // Update user
           user.setUsername(usernameField.getText().trim());
           user.setFullName(fullNameField.getText().trim());
           user.setEmail(emailField.getText().trim());
           user.setPhoneNumber(phoneField.getText().trim());
           user.setRole((User.UserRole) roleComboBox.getSelectedItem());
           user.setActive(activeCheckBox.isSelected());
           
           // Save user
           DataManager.getInstance().updateUser(user);
           
           // Close dialog and refresh
           dialog.dispose();
           loadUsers();
           
           // Show success message
           showSuccessMessage("User updated successfully");
       });
       
       cancelButton.addActionListener(e -> dialog.dispose());
       
       buttonsPanel.add(saveButton);
       buttonsPanel.add(cancelButton);
       
       // Add components to content panel
       contentPanel.add(new JLabel("Edit User", JLabel.CENTER) {{
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
   
   private void resetUserPassword(User user) {
       // Create a modern dialog for resetting password
       JDialog dialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "Reset Password", true);
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
       JLabel userLabel = new JLabel("Resetting password for: " + user.getFullName());
       userLabel.setFont(new Font("Arial", Font.BOLD, 14));
       userLabel.setForeground(Color.WHITE);
       
       JPasswordField newPasswordField = createStyledPasswordField();
       JPasswordField confirmPasswordField = createStyledPasswordField();
       
       // Add components to form panel
       gbc.gridx = 0;
       gbc.gridy = 0;
       gbc.gridwidth = 2;
       formPanel.add(userLabel, gbc);
       
       gbc.gridx = 0;
       gbc.gridy = 1;
       gbc.gridwidth = 1;
       JLabel newPasswordLabel = new JLabel("New Password:");
       newPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
       newPasswordLabel.setForeground(Color.WHITE);
       formPanel.add(newPasswordLabel, gbc);
       
       gbc.gridx = 1;
       formPanel.add(newPasswordField, gbc);
       
       gbc.gridx = 0;
       gbc.gridy = 2;
       JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
       confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
       confirmPasswordLabel.setForeground(Color.WHITE);
       formPanel.add(confirmPasswordLabel, gbc);
       
       gbc.gridx = 1;
       formPanel.add(confirmPasswordField, gbc);
       
       // Create buttons panel
       JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
       buttonsPanel.setOpaque(false);
       
       JButton resetButton = createStyledButton("Reset Password", parentFrame.getPrimaryAccentColor());
       JButton cancelButton = createStyledButton("Cancel", new Color(150, 150, 150));
       
       // Add action listeners
       resetButton.addActionListener(e -> {
           // Validate input
           if (newPasswordField.getPassword().length == 0 ||
               confirmPasswordField.getPassword().length == 0) {
               showErrorMessage(dialog, "Please enter and confirm the new password.");
               return;
           }
           
           String newPassword = new String(newPasswordField.getPassword());
           String confirmPassword = new String(confirmPasswordField.getPassword());
           
           if (!newPassword.equals(confirmPassword)) {
               showErrorMessage(dialog, "Passwords do not match.");
               return;
           }
           
           // Update user password
           user.setPassword(newPassword);
           
           // Save user
           DataManager.getInstance().updateUser(user);
           
           // Close dialog
           dialog.dispose();
           
           // Show success message
           showSuccessMessage("Password has been reset successfully");
       });
       
       cancelButton.addActionListener(e -> dialog.dispose());
       
       buttonsPanel.add(resetButton);
       buttonsPanel.add(cancelButton);
       
       // Add components to content panel
       contentPanel.add(new JLabel("Reset Password", JLabel.CENTER) {{
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
   
   private void deleteUser(User user) {
       // Prevent deleting the current user
       if (user.getUserId().equals(DataManager.getInstance().getCurrentUser().getUserId())) {
           showErrorMessage(null, "You cannot delete your own account.");
           return;
       }
       
       // Show confirmation dialog
       int choice = JOptionPane.showConfirmDialog(this,
           "Are you sure you want to delete the user: " + user.getFullName() + "?",
           "Confirm Deletion",
           JOptionPane.YES_NO_OPTION,
           JOptionPane.WARNING_MESSAGE);
           
       if (choice == JOptionPane.YES_OPTION) {
           DataManager.getInstance().deleteUser(user.getUserId());
           loadUsers();
           
           // Show success message
           showSuccessMessage("User deleted successfully");
       }
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
   
   private JPasswordField createStyledPasswordField() {
       JPasswordField passwordField = new JPasswordField(20);
       passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
       passwordField.setForeground(Color.WHITE);
       passwordField.setBackground(parentFrame.getBackgroundColor().brighter());
       passwordField.setBorder(BorderFactory.createCompoundBorder(
           BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
           BorderFactory.createEmptyBorder(8, 10, 8, 10)
       ));
       passwordField.setCaretColor(Color.WHITE);
       return passwordField;
   }
   
   private void styleComboBox(JComboBox<?> comboBox) {
       comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
       comboBox.setForeground(Color.WHITE);
       comboBox.setBackground(parentFrame.getBackgroundColor().brighter());
       comboBox.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true));
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
   
   private void showErrorMessage(JDialog parent, String message) {
    // Create a modern error notification
    JDialog errorDialog;
    if (parent != null) {
        errorDialog = new JDialog(parent, "", true);
    } else {
        errorDialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "", true);
    }
    
    errorDialog.setUndecorated(true);
    errorDialog.setSize(350, 80);
    errorDialog.setLocationRelativeTo(parent != null ? parent : this);
    
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
}
   
   private void showSuccessMessage(String message) {
       // Create a modern success notification
       JDialog successDialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "", false);
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

private String getRoleShortText(User.UserRole role) {
    switch (role) {
        case ADMIN:
            return "ADMIN";
        case STUDENT:
            return "STUDENT";
        case FACULTY:
            return "FACULTY";
        case STAFF:
            return "STAFF";
        default:
            return "USER";
    }
}
}
