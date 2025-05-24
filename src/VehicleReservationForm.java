import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javax.swing.*;
import javax.swing.border.*;
import model.Reservation;
import model.User;
import model.Vehicle;

/**
 * A formal vehicle reservation form with a professional application layout
 */
public class VehicleReservationForm extends JDialog {
    private MainFrameInterface parentFrame;
    private Vehicle vehicle;
    
    // Form fields
    private JTextField nameField;
    private JTextField ageField;
    private JTextField emailField;
    private JTextField addressField;
    private JTextField positionField;
    private JTextField departmentField;
    private JSpinner capacitySpinner;
    private JTextField contactNumberField;
    private JTextField destinationField;
    private JTextArea purposeArea;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    
    // Form colors - Professional color scheme
    private final Color PRIMARY_COLOR = new Color(0, 51, 102);      // Dark blue
    private final Color SECONDARY_COLOR = new Color(0, 102, 153);   // Medium blue
    private final Color ACCENT_COLOR = new Color(204, 153, 0);      // Gold
    private final Color HEADER_BG = PRIMARY_COLOR;
    private final Color HEADER_FG = Color.WHITE;
    private final Color FORM_BG = Color.WHITE;
    private final Color FORM_FG = new Color(51, 51, 51);           // Dark gray
    private final Color BORDER_COLOR = new Color(220, 220, 220);    // Light gray
    private final Color BUTTON_BG = SECONDARY_COLOR;
    private final Color BUTTON_FG = Color.WHITE;
    private final Color SECTION_BG = new Color(245, 245, 245);      // Very light gray
    
    public VehicleReservationForm(JFrame parent, Vehicle vehicle, MainFrameInterface parentFrame) {
        super(parent, "Vehicle Reservation Application", true);
        this.vehicle = vehicle;
        this.parentFrame = parentFrame;
        
        setSize(850, 750);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        initComponents();
    }
    
    private void initComponents() {
        // Main panel with white background
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(FORM_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create form header
        JPanel headerPanel = createHeaderPanel();
        
        // Create form content
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(FORM_BG);
        
        // Add form sections
        formPanel.add(createVehicleInfoSection());
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(createApplicantSection());
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(createTripDetailsSection());
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(createDeclarationSection());
        
        // Create buttons panel
        JPanel buttonsPanel = createButtonsPanel();
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Add a scroll pane with custom styling
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Set content pane
        setContentPane(mainPanel);
        
        // Pre-fill user information
        prefillUserInformation();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FORM_BG);
        panel.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 2, 0, ACCENT_COLOR),
            new EmptyBorder(0, 0, 20, 0)
        ));
        
        // Create logo panel
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(FORM_BG);
        logoPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Create logo
        JLabel logoLabel = createLogoLabel();
        logoPanel.add(logoLabel, BorderLayout.WEST);
        
        // Organization name with formal styling
        JPanel orgPanel = new JPanel(new GridLayout(2, 1));
        orgPanel.setBackground(FORM_BG);
        
        JLabel titleLabel = new JLabel("Vehicle Reservation Application");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JLabel orgLabel = new JLabel("Motor Pool Management System");
        orgLabel.setFont(new Font("Arial", Font.BOLD, 16));
        orgLabel.setForeground(SECONDARY_COLOR);
        
        orgPanel.add(titleLabel);
        orgPanel.add(orgLabel);
        logoPanel.add(orgPanel, BorderLayout.CENTER);
        
        // Reference number panel
        JPanel refPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        refPanel.setBackground(FORM_BG);
        refPanel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel refLabel = new JLabel("Reference No:");
        refLabel.setFont(new Font("Arial", Font.BOLD, 12));
        refLabel.setForeground(FORM_FG);
        
        JLabel dateLabel = new JLabel("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(FORM_FG);
        
        refPanel.add(refLabel);
        refPanel.add(dateLabel);
        logoPanel.add(refPanel, BorderLayout.EAST);
        
        // Add instruction
        JPanel instructionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        instructionPanel.setBackground(FORM_BG);
        
        JLabel instructionLabel = new JLabel("Please complete all fields. Required fields are marked with *");
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        instructionLabel.setForeground(FORM_FG);
        instructionPanel.add(instructionLabel);
        
        panel.add(logoPanel, BorderLayout.NORTH);
        panel.add(instructionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JLabel createLogoLabel() {
        // Create a simple logo
        int size = 60;
        BufferedImage logo = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = logo.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw circular background
        g2d.setColor(PRIMARY_COLOR);
        g2d.fillOval(0, 0, size, size);
        
        // Draw car silhouette
        g2d.setColor(Color.WHITE);
        int carWidth = 40;
        int carHeight = 15;
        g2d.fillRoundRect(size/2 - carWidth/2, size/2 - carHeight/2, carWidth, carHeight, 8, 8);
        g2d.fillRoundRect(size/2 - carWidth/3, size/2 - carHeight - 5, 2*carWidth/3, carHeight, 8, 8);
        
        // Draw wheels
        g2d.setColor(ACCENT_COLOR);
        g2d.fillOval(size/2 - carWidth/2 + 5, size/2 + carHeight/2 - 3, 10, 10);
        g2d.fillOval(size/2 + carWidth/2 - 15, size/2 + carHeight/2 - 3, 10, 10);
        
        g2d.dispose();
        return new JLabel(new ImageIcon(logo));
    }
    
    private JPanel createVehicleInfoSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(FORM_BG);
        
        // Section header
        JPanel headerPanel = createSectionHeader("Vehicle Information");
        
        // Section content
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contentPanel.setBackground(SECTION_BG);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Vehicle details
        JPanel detailsPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        detailsPanel.setBackground(SECTION_BG);
        
        JLabel vehicleIdLabel = new JLabel("Vehicle ID: " + vehicle.getVehicleId());
        vehicleIdLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        vehicleIdLabel.setForeground(FORM_FG);
        
        JLabel vehicleNameLabel = new JLabel(vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel());
        vehicleNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        vehicleNameLabel.setForeground(PRIMARY_COLOR);
        
        JLabel vehicleTypeLabel = new JLabel("Type: " + vehicle.getType());
        vehicleTypeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        vehicleTypeLabel.setForeground(FORM_FG);
        
        JLabel capacityLabel = new JLabel("Capacity: " + vehicle.getCapacity() + " passengers");
        capacityLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        capacityLabel.setForeground(FORM_FG);
        
        detailsPanel.add(vehicleNameLabel);
        detailsPanel.add(vehicleIdLabel);
        detailsPanel.add(vehicleTypeLabel);
        detailsPanel.add(capacityLabel);
        
        // Vehicle image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setBorder(new LineBorder(BORDER_COLOR));
        imagePanel.setPreferredSize(new Dimension(200, 140));
        
        // Get vehicle icon based on type
        ImageIcon vehicleIcon = getVehicleIcon(vehicle);
        JLabel imageLabel = new JLabel(vehicleIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        contentPanel.add(detailsPanel);
        contentPanel.add(imagePanel);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createApplicantSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(FORM_BG);
        
        // Section header
        JPanel headerPanel = createSectionHeader("Applicant's Details");
        
        // Section content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(SECTION_BG);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Create form fields with improved styling
        nameField = createStyledTextField("");
        ageField = createStyledTextField("");
        emailField = createStyledTextField("");
        addressField = createStyledTextField("");
        positionField = createStyledTextField("");
        departmentField = createStyledTextField("");
        contactNumberField = createStyledTextField("");
        
        // Add form fields to panel with improved layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        contentPanel.add(createFormLabel("Full Name: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        contentPanel.add(createFormLabel("Age: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(ageField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        contentPanel.add(createFormLabel("Email: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(emailField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        contentPanel.add(createFormLabel("Address: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(addressField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        contentPanel.add(createFormLabel("Position/Title: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(positionField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        contentPanel.add(createFormLabel("Department: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(departmentField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        contentPanel.add(createFormLabel("Contact Number: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(contactNumberField, gbc);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTripDetailsSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(FORM_BG);
        
        // Section header
        JPanel headerPanel = createSectionHeader("Trip Details");
        
        // Section content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(SECTION_BG);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Create form fields with improved styling
        destinationField = createStyledTextField("");
        
        // Styled spinner for capacity
        SpinnerNumberModel capacityModel = new SpinnerNumberModel(1, 1, vehicle.getCapacity(), 1);
        capacitySpinner = new JSpinner(capacityModel);
        capacitySpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        JComponent capacityEditor = capacitySpinner.getEditor();
        JFormattedTextField capacityField = ((JSpinner.DefaultEditor) capacityEditor).getTextField();
        capacityField.setColumns(5);
        capacityField.setBorder(createStyledBorder());
        
        // Styled text area for purpose
        purposeArea = new JTextArea(4, 20);
        purposeArea.setFont(new Font("Arial", Font.PLAIN, 14));
        purposeArea.setLineWrap(true);
        purposeArea.setWrapStyleWord(true);
        purposeArea.setBorder(createStyledBorder());
        JScrollPane purposeScrollPane = new JScrollPane(purposeArea);
        purposeScrollPane.setBorder(null);
        
        // Styled date spinners
        startDateSpinner = createStyledDateSpinner();
        endDateSpinner = createStyledDateSpinner();
        
        // Set default start time to next hour
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.HOUR_OF_DAY, 1);
        calendar.set(java.util.Calendar.MINUTE, 0);
        startDateSpinner.setValue(calendar.getTime());
        
        // Set default end time to 3 hours after start
        calendar.add(java.util.Calendar.HOUR_OF_DAY, 3);
        endDateSpinner.setValue(calendar.getTime());
        
        // Add form fields to panel with improved layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        contentPanel.add(createFormLabel("Destination: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(destinationField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        contentPanel.add(createFormLabel("Number of Passengers: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(capacitySpinner, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        contentPanel.add(createFormLabel("Trip Purpose: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(purposeScrollPane, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridheight = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(createFormLabel("Start Date/Time: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(startDateSpinner, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        contentPanel.add(createFormLabel("End Date/Time: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(endDateSpinner, gbc);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDeclarationSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(FORM_BG);
        
        // Section header
        JPanel headerPanel = createSectionHeader("Declaration");
        
        // Section content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(SECTION_BG);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Create a styled declaration text area
        JTextArea declarationArea = new JTextArea(
            "I, the undersigned, hereby declare that the information provided in this application is true and correct to the best of my knowledge. " +
            "I understand that I am responsible for the vehicle during the reservation period and will comply with all organizational policies regarding vehicle usage. " +
            "I agree to return the vehicle in the same condition as received and report any incidents or damages immediately."
        );
        declarationArea.setFont(new Font("Arial", Font.ITALIC, 14));
        declarationArea.setLineWrap(true);
        declarationArea.setWrapStyleWord(true);
        declarationArea.setEditable(false);
        declarationArea.setBackground(SECTION_BG);
        declarationArea.setBorder(null);
        declarationArea.setForeground(FORM_FG);
        
        // Create a more formal signature panel
        JPanel signaturePanel = new JPanel(new GridLayout(1, 2, 15, 0));
        signaturePanel.setBackground(SECTION_BG);
        signaturePanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Signature box with improved styling
        JPanel signatureBox = new JPanel(new BorderLayout());
        signatureBox.setBackground(Color.WHITE);
        signatureBox.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
            ),
            "Applicant's Signature",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12),
            FORM_FG
        ));
        signatureBox.setPreferredSize(new Dimension(0, 80));
        
        // Date box with improved styling
        JPanel dateBox = new JPanel(new BorderLayout());
        dateBox.setBackground(Color.WHITE);
        dateBox.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
            ),
            "Date",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12),
            FORM_FG
        ));
        dateBox.setPreferredSize(new Dimension(0, 80));
        
        JLabel dateLabel = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setHorizontalAlignment(JLabel.CENTER);
        dateBox.add(dateLabel, BorderLayout.CENTER);
        
        signaturePanel.add(signatureBox);
        signaturePanel.add(dateBox);
        
        // Add official use only section
        JPanel officialUsePanel = new JPanel(new BorderLayout());
        officialUsePanel.setBackground(SECTION_BG);
        officialUsePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
            ),
            "FOR OFFICIAL USE ONLY",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12),
            FORM_FG
        ));
        
        JPanel officialGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        officialGrid.setBackground(SECTION_BG);
        
        JPanel approvedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        approvedPanel.setBackground(SECTION_BG);
        JCheckBox approvedBox = new JCheckBox("Approved");
        approvedBox.setEnabled(false);
        approvedBox.setBackground(SECTION_BG);
        approvedPanel.add(approvedBox);
        
        JPanel rejectedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rejectedPanel.setBackground(SECTION_BG);
        JCheckBox rejectedBox = new JCheckBox("Rejected");
        rejectedBox.setEnabled(false);
        rejectedBox.setBackground(SECTION_BG);
        rejectedPanel.add(rejectedBox);
        
        JPanel authorizedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        authorizedPanel.setBackground(SECTION_BG);
        JLabel authorizedLabel = new JLabel("Authorized by: ___________________");
        authorizedPanel.add(authorizedLabel);
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.setBackground(SECTION_BG);
        JLabel officialDateLabel = new JLabel("Date: ___________________");
        datePanel.add(officialDateLabel);
        
        officialGrid.add(approvedPanel);
        officialGrid.add(rejectedPanel);
        officialGrid.add(authorizedPanel);
        officialGrid.add(datePanel);
        
        officialUsePanel.add(officialGrid, BorderLayout.CENTER);
        
        // Add all components to the content panel
        contentPanel.add(declarationArea, BorderLayout.NORTH);
        contentPanel.add(signaturePanel, BorderLayout.CENTER);
        contentPanel.add(officialUsePanel, BorderLayout.SOUTH);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(FORM_BG);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Create styled buttons
        JButton submitButton = createStyledButton("Submit Application", true);
        submitButton.setPreferredSize(new Dimension(200, 45));
        submitButton.addActionListener(e -> submitApplication());
        
        JButton cancelButton = createStyledButton("Cancel", false);
        cancelButton.setPreferredSize(new Dimension(120, 45));
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(cancelButton);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(submitButton);
        
        return panel;
    }
    
    private JPanel createSectionHeader(String title) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setForeground(HEADER_FG);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(FORM_FG);
        return label;
    }
    
    private JTextField createStyledTextField(String text) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(createStyledBorder());
        
        // Add focus listener for visual feedback
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(SECONDARY_COLOR, 2),
                    new EmptyBorder(6, 8, 6, 8)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(createStyledBorder());
            }
        });
        
        return textField;
    }
    
    private Border createStyledBorder() {
        return BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        );
    }
    
    private JSpinner createStyledDateSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "MM/dd/yyyy HH:mm");
        spinner.setEditor(editor);
        spinner.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Style the text field inside the spinner
        JComponent spinnerEditor = spinner.getEditor();
        JFormattedTextField textField = ((JSpinner.DefaultEditor) spinnerEditor).getTextField();
        textField.setBorder(createStyledBorder());
        
        return spinner;
    }
    
    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        
        if (isPrimary) {
            button.setBackground(BUTTON_BG);
            button.setForeground(BUTTON_FG);
        } else {
            button.setBackground(Color.LIGHT_GRAY);
            button.setForeground(FORM_FG);
        }
        
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(new LineBorder(isPrimary ? SECONDARY_COLOR : BORDER_COLOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isPrimary) {
                    button.setBackground(SECONDARY_COLOR.brighter());
                } else {
                    button.setBackground(Color.LIGHT_GRAY.brighter());
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (isPrimary) {
                    button.setBackground(BUTTON_BG);
                } else {
                    button.setBackground(Color.LIGHT_GRAY);
                }
            }
        });
        
        return button;
    }
    
    private void prefillUserInformation() {
        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            nameField.setText(currentUser.getFullName());
            emailField.setText(currentUser.getEmail());
            contactNumberField.setText(currentUser.getPhoneNumber());
        }
    }
    
    private boolean validateForm() {
        // Validate required fields
        if (nameField.getText().trim().isEmpty()) {
            showError("Please enter your full name.");
            return false;
        }
        
        if (ageField.getText().trim().isEmpty()) {
            showError("Please enter your age.");
            return false;
        } else {
            try {
                int age = Integer.parseInt(ageField.getText().trim());
                if (age < 18) {
                    showError("You must be at least 18 years old to reserve a vehicle.");
                    return false;
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid age.");
                return false;
            }
        }
        
        if (emailField.getText().trim().isEmpty()) {
            showError("Please enter your email address.");
            return false;
        }
        
        if (addressField.getText().trim().isEmpty()) {
            showError("Please enter your address.");
            return false;
        }
        
        if (positionField.getText().trim().isEmpty()) {
            showError("Please enter your position/title.");
            return false;
        }
        
        if (departmentField.getText().trim().isEmpty()) {
            showError("Please enter your department.");
            return false;
        }
        
        if (contactNumberField.getText().trim().isEmpty()) {
            showError("Please enter your contact number.");
            return false;
        }
        
        if (destinationField.getText().trim().isEmpty()) {
            showError("Please enter your destination.");
            return false;
        }
        
        if (purposeArea.getText().trim().isEmpty()) {
            showError("Please enter the purpose of your trip.");
            return false;
        }
        
        // Validate dates
        java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
        java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();
        
        LocalDateTime startDateTime = startDate.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDateTime();
            
        LocalDateTime endDateTime = endDate.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDateTime();
        
        if (startDateTime.isAfter(endDateTime)) {
            showError("Start date must be before end date.");
            return false;
        }
        
        if (startDateTime.isBefore(LocalDateTime.now())) {
            showError("Start date must be in the future.");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        // Create a custom error dialog
        JDialog errorDialog = new JDialog((JFrame)this.getOwner(), "Validation Error", true);
        errorDialog.setSize(400, 200);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setLayout(new BorderLayout());
        
        // Create error icon
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        iconPanel.setBackground(Color.WHITE);
        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        iconPanel.add(iconLabel);
        
        // Create message panel
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel messageLabel = new JLabel("<html><body><p style='width: 250px;'>" + message + "</p></body></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JButton okButton = createStyledButton("OK", true);
        okButton.setPreferredSize(new Dimension(100, 35));
        okButton.addActionListener(e -> errorDialog.dispose());
        buttonPanel.add(okButton);
        
        // Add panels to dialog
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(iconPanel, BorderLayout.WEST);
        contentPanel.add(messagePanel, BorderLayout.CENTER);
        
        errorDialog.add(contentPanel, BorderLayout.CENTER);
        errorDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        errorDialog.setVisible(true);
    }
    
    private void submitApplication() {
        if (!validateForm()) {
            return;
        }
        
        try {
            // Get form values
            String name = nameField.getText().trim();
            String age = ageField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();
            String position = positionField.getText().trim();
            String department = departmentField.getText().trim();
            String contactNumber = contactNumberField.getText().trim();
            String destination = destinationField.getText().trim();
            int passengers = (Integer) capacitySpinner.getValue();
            String purpose = purposeArea.getText().trim();
            
            // Convert dates to LocalDateTime
            java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
            java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();
            
            LocalDateTime startDateTime = startDate.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
                
            LocalDateTime endDateTime = endDate.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
            
            // Create reservation ID
            String reservationId = "R-" + UUID.randomUUID().toString().substring(0, 8);
            
            // Create reservation
            Reservation reservation = new Reservation(
                reservationId,
                DataManager.getInstance().getCurrentUser().getUserId(),
                vehicle.getVehicleId(),
                startDateTime,
                endDateTime,
                purpose,
                destination,
                passengers
            );
            
            // Add additional information to notes
            StringBuilder notes = new StringBuilder();
            notes.append("Age: ").append(age).append("\n");
            notes.append("Address: ").append(address).append("\n");
            notes.append("Position: ").append(position).append("\n");
            notes.append("Department: ").append(department).append("\n");
            notes.append("Contact Number: ").append(contactNumber).append("\n");
            
            reservation.setNotes(notes.toString());
            
            // Save reservation
            DataManager.getInstance().addReservation(reservation);
            
            // Show success message with custom dialog
            showSuccessDialog(reservationId);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error submitting application: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showSuccessDialog(String reservationId) {
        // Create a custom success dialog
        JDialog successDialog = new JDialog((JFrame)this.getOwner(), "Application Submitted", true);
        successDialog.setSize(450, 300);
        successDialog.setLocationRelativeTo(this);
        successDialog.setLayout(new BorderLayout());
        
        // Create success icon
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        iconPanel.setBackground(Color.WHITE);
        
        // Create a checkmark icon
        BufferedImage checkmark = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = checkmark.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(0, 153, 51)); // Green
        g2d.fillOval(0, 0, 60, 60);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(5));
        g2d.drawLine(15, 30, 25, 40);
        g2d.drawLine(25, 40, 45, 20);
        g2d.dispose();
        
        JLabel iconLabel = new JLabel(new ImageIcon(checkmark));
        iconPanel.add(iconLabel);
        
        // Create message panel
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Application Submitted Successfully");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel messageLabel = new JLabel("<html><body><div style='text-align: center;'>" +
            "<p>Your vehicle reservation application has been submitted.</p>" +
            "<p>Application ID: <b>" + reservationId + "</b></p>" +
            "<p>An administrator will review your application and notify you of the decision.</p>" +
            "</div></body></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel textPanel = new JPanel(new BorderLayout(0, 15));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(messageLabel, BorderLayout.CENTER);
        
        messagePanel.add(textPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JButton okButton = createStyledButton("OK", true);
        okButton.setPreferredSize(new Dimension(100, 35));
        okButton.addActionListener(e -> {
            successDialog.dispose();
            dispose(); // Close the reservation form
        });
        buttonPanel.add(okButton);
        
        // Add panels to dialog
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(iconPanel, BorderLayout.NORTH);
        contentPanel.add(messagePanel, BorderLayout.CENTER);
        
        successDialog.add(contentPanel, BorderLayout.CENTER);
        successDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        successDialog.setVisible(true);
    }
    
    private ImageIcon getVehicleIcon(Vehicle vehicle) {
        // Create a more professional vehicle icon based on type
        int width = 200;
        int height = 140;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fill background with gradient
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(245, 245, 245),
            0, height, new Color(230, 230, 230)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        
        // Draw vehicle silhouette based on type
        g2d.setColor(new Color(60, 60, 60));
        int centerX = width / 2;
        int centerY = height / 2;
        
        switch (vehicle.getType()) {
            case SEDAN:
                // Draw sedan silhouette
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRoundRect(centerX - 80, centerY - 15, 160, 30, 20, 20);
                g2d.fillRoundRect(centerX - 60, centerY - 35, 120, 25, 15, 15);
                // Windows
                g2d.setColor(new Color(200, 220, 240));
                g2d.fillRoundRect(centerX - 55, centerY - 32, 110, 20, 10, 10);
                // Wheels
                g2d.setColor(Color.BLACK);
                g2d.fillOval(centerX - 60, centerY + 10, 25, 25);
                g2d.fillOval(centerX + 35, centerY + 10, 25, 25);
                // Wheel rims
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillOval(centerX - 55, centerY + 15, 15, 15);
                g2d.fillOval(centerX + 40, centerY + 15, 15, 15);
                break;
            case SUV:
                // Draw SUV silhouette
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRoundRect(centerX - 80, centerY - 15, 160, 40, 20, 20);
                g2d.fillRoundRect(centerX - 70, centerY - 40, 140, 30, 10, 10);
                // Windows
                g2d.setColor(new Color(200, 220, 240));
                g2d.fillRoundRect(centerX - 65, centerY - 37, 130, 25, 8, 8);
                // Wheels
                g2d.setColor(Color.BLACK);
                g2d.fillOval(centerX - 60, centerY + 15, 30, 30);
                g2d.fillOval(centerX + 30, centerY + 15, 30, 30);
                // Wheel rims
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillOval(centerX - 53, centerY + 22, 16, 16);
                g2d.fillOval(centerX + 37, centerY + 22, 16, 16);
                break;
            case VAN:
                // Draw van silhouette
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRoundRect(centerX - 85, centerY - 40, 170, 70, 20, 20);
                // Windows
                g2d.setColor(new Color(200, 220, 240));
                g2d.fillRoundRect(centerX - 75, centerY - 35, 50, 25, 8, 8);
                g2d.fillRect(centerX - 20, centerY - 35, 100, 25);
                // Side windows
                for (int i = 0; i < 3; i++) {
                    g2d.fillRoundRect(centerX - 10 + (i * 30), centerY - 35, 25, 25, 5, 5);
                }
                // Wheels
                g2d.setColor(Color.BLACK);
                g2d.fillOval(centerX - 60, centerY + 20, 30, 30);
                g2d.fillOval(centerX + 30, centerY + 20, 30, 30);
                // Wheel rims
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillOval(centerX - 53, centerY + 27, 16, 16);
                g2d.fillOval(centerX + 37, centerY + 27, 16, 16);
                break;
            case BUS:
                // Draw bus silhouette
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRoundRect(centerX - 100, centerY - 40, 200, 70, 15, 15);
                // Windows
                g2d.setColor(new Color(200, 220, 240));
                for (int i = 0; i < 5; i++) {
                    g2d.fillRoundRect(centerX - 85 + (i * 35), centerY - 35, 25, 25, 5, 5);
                }
                // Wheels
                g2d.setColor(Color.BLACK);
                g2d.fillOval(centerX - 70, centerY + 20, 30, 30);
                g2d.fillOval(centerX + 40, centerY + 20, 30, 30);
                // Wheel rims
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillOval(centerX - 63, centerY + 27, 16, 16);
                g2d.fillOval(centerX + 47, centerY + 27, 16, 16);
                break;
            case TRUCK:
                // Draw truck silhouette
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRoundRect(centerX - 40, centerY - 35, 80, 40, 10, 10);
                g2d.fillRect(centerX - 90, centerY - 15, 180, 40);
                // Windows
                g2d.setColor(new Color(200, 220, 240));
                g2d.fillRoundRect(centerX - 35, centerY - 30, 70, 30, 8, 8);
                // Wheels
                g2d.setColor(Color.BLACK);
                g2d.fillOval(centerX - 60, centerY + 15, 30, 30);
                g2d.fillOval(centerX + 30, centerY + 15, 30, 30);
                // Wheel rims
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillOval(centerX - 53, centerY + 22, 16, 16);
                g2d.fillOval(centerX + 37, centerY + 22, 16, 16);
                break;
            default:
                // Generic vehicle
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRoundRect(centerX - 80, centerY - 20, 160, 40, 20, 20);
                g2d.setColor(new Color(200, 220, 240));
                g2d.fillRoundRect(centerX - 60, centerY - 15, 120, 30, 15, 15);
                g2d.setColor(Color.BLACK);
                g2d.fillOval(centerX - 60, centerY + 15, 25, 25);
                g2d.fillOval(centerX + 35, centerY + 15, 25, 25);
        }
        
        // Draw vehicle type text
        g2d.setColor(FORM_FG);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String typeText = vehicle.getType().toString();
        int textWidth = fm.stringWidth(typeText);
        g2d.drawString(typeText, (width - textWidth) / 2, height - 15);
        
        // Draw vehicle details
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        String detailsText = vehicle.getMake() + " " + vehicle.getModel();
        int detailsWidth = fm.stringWidth(detailsText);
        g2d.drawString(detailsText, (width - detailsWidth) / 2, height - 35);
        
        g2d.dispose();
        return new ImageIcon(image);
    }
}
