import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.Reservation;
import model.User;
import model.Vehicle;

/**
 * Modern panel for generating reports with interactive UI
 */
public class ReportsPanel extends JPanel {
    private MainFrameInterface parentFrame;
    private JPanel reportConfigPanel;
    private JPanel reportResultPanel;
    private JComboBox<String> reportTypeComboBox;
    private JPanel chartPanel;
    private JPanel dataPanel;
    private CardLayout cardLayout;
    private List<Vehicle> allVehicles;
    
    // Animation components
    private Timer animationTimer = new Timer(16, null); // Initialize here
    private float animationProgress = 0.0f;
    private boolean animatingIn = true;
    
    // Date formatters
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

    // Report types
    private static final String VEHICLE_USAGE = "Vehicle Usage Report";
    private static final String RESERVATION_STATUS = "Reservation Status Report";
    private static final String USER_ACTIVITY = "User Activity Report";
    private static final String USAGE_STATISTICS = "Usage Statistics Report";

    // Modern vibrant colors for charts
    private final Color[] CHART_COLORS = {
            new Color(255, 64, 180),  // Vibrant pink
            new Color(64, 180, 255),  // Vibrant blue
            new Color(180, 64, 255),  // Vibrant purple
            new Color(255, 180, 64),  // Vibrant orange
            new Color(64, 255, 180),  // Vibrant teal
            new Color(180, 255, 64),  // Vibrant lime
            new Color(255, 64, 64)    // Vibrant red
    };
    
    // UI Constants
    private static final Color BACKGROUND_COLOR = new Color(27, 20, 70);
    private static final Color CARD_BACKGROUND = new Color(30, 30, 50);
    private static final Color ACCENT_COLOR = new Color(255, 64, 180);
    private static final Color SECONDARY_COLOR = new Color(64, 180, 255);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color SUBTITLE_COLOR = new Color(200, 200, 200);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font VALUE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final int CARD_RADIUS = 15;
    private static final int ANIMATION_DURATION = 500; // milliseconds

    public ReportsPanel(MainFrameInterface parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(BACKGROUND_COLOR);

        // Initialize allVehicles
        allVehicles = DataManager.getInstance().getAllVehicles();
        
        initComponents();
        startEntranceAnimation();
    }

    private void initComponents() {
        // Create header panel with title and description
        JPanel headerPanel = createHeaderPanel();

        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);

        // Create report selection panel
        JPanel selectionPanel = createReportSelectionPanel();

        // Create report configuration panel
        reportConfigPanel = new JPanel();
        reportConfigPanel.setLayout(new CardLayout());
        reportConfigPanel.setOpaque(false);

        // Add different report config panels
        reportConfigPanel.add(createVehicleUsageConfigPanel(), VEHICLE_USAGE);
        reportConfigPanel.add(createReservationStatusConfigPanel(), RESERVATION_STATUS);
        reportConfigPanel.add(createUserActivityConfigPanel(), USER_ACTIVITY);
        reportConfigPanel.add(createUsageStatisticsConfigPanel(), USAGE_STATISTICS);

        // Create report result panel with card layout
        reportResultPanel = new JPanel();
        cardLayout = new CardLayout();
        reportResultPanel.setLayout(cardLayout);
        reportResultPanel.setOpaque(false);

        // Create chart and data panels
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setOpaque(false);

        dataPanel = new JPanel(new BorderLayout());
        dataPanel.setOpaque(false);

        // Add panels to result panel
        reportResultPanel.add(chartPanel, "chart");
        reportResultPanel.add(dataPanel, "data");
        cardLayout.show(reportResultPanel, "chart");

        // Add components to content panel
        contentPanel.add(selectionPanel, BorderLayout.NORTH);
        contentPanel.add(reportConfigPanel, BorderLayout.CENTER);
        contentPanel.add(reportResultPanel, BorderLayout.SOUTH);

        // Add header and content to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Configure initial report panel
        configureReportPanel();
    }

    // Add these animation methods after the initComponents() method:

    private void startEntranceAnimation() {
        // Reset animation progress
        animationProgress = 0.0f;
        animatingIn = true;
        
        // Configure animation timer
        if (animationTimer.isRunning()) {
            animationTimer.stop();
        }
        
        animationTimer = new Timer(16, e -> {
            if (animatingIn) {
                animationProgress += 0.05f;
                if (animationProgress >= 1.0f) {
                    animationProgress = 1.0f;
                    animationTimer.stop();
                }
            } else {
                animationProgress -= 0.05f;
                if (animationProgress <= 0.0f) {
                    animationProgress = 0.0f;
                    animationTimer.stop();
                }
            }
            repaint();
        });
        
        animationTimer.start();
    }

    private void animateConfigPanelChange() {
        // Animate out current panel
        animatingIn = false;
        animationTimer = new Timer(16, e -> {
            animationProgress -= 0.05f;
            if (animationProgress <= 0.0f) {
                animationProgress = 0.0f;
                animationTimer.stop();
                
                // Show new panel
                configureReportPanel();
                
                // Animate in new panel
                animatingIn = true;
                animationTimer = new Timer(16, e2 -> {
                    animationProgress += 0.05f;
                    if (animationProgress >= 1.0f) {
                        animationProgress = 1.0f;
                        animationTimer.stop();
                    }
                    repaint();
                });
                animationTimer.start();
            }
            repaint();
        });
        animationTimer.start();
    }

    private void animateCardChange(String cardName) {
        // Animate out current card
        animatingIn = false;
        animationTimer = new Timer(16, e -> {
            animationProgress -= 0.05f;
            if (animationProgress <= 0.0f) {
                animationProgress = 0.0f;
                animationTimer.stop();
                
                // Show new card
                cardLayout.show(reportResultPanel, cardName);
                
                // Animate in new card
                animatingIn = true;
                animationTimer = new Timer(16, e2 -> {
                    animationProgress += 0.05f;
                    if (animationProgress >= 1.0f) {
                        animationProgress = 1.0f;
                        animationTimer.stop();
                    }
                    repaint();
                });
                animationTimer.start();
            }
            repaint();
        });
        animationTimer.start();
    }

    private void animateReportGeneration(Runnable generateReport) {
        // Show loading animation
        JPanel loadingPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CARD_RADIUS, CARD_RADIUS);
                
                // Draw loading text
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(LABEL_FONT);
                String message = "Generating report...";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(message);
                int textHeight = fm.getHeight();
                g2d.drawString(message, (getWidth() - textWidth) / 2, (getHeight() - textHeight) / 2 + fm.getAscent());
                
                // Draw loading spinner
                int spinnerSize = 40;
                int spinnerX = (getWidth() - spinnerSize) / 2;
                int spinnerY = (getHeight() - spinnerSize) / 2 - 40;
                
                g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setColor(new Color(80, 80, 100, 100));
                g2d.drawArc(spinnerX, spinnerY, spinnerSize, spinnerSize, 0, 360);
                
                g2d.setColor(ACCENT_COLOR);
                g2d.drawArc(spinnerX, spinnerY, spinnerSize, spinnerSize, 
                        (int)(System.currentTimeMillis() / 10) % 360, 120);
            }
        };
        loadingPanel.setOpaque(false);
        
        // Add loading panel to chart panel
        chartPanel.removeAll();
        chartPanel.add(loadingPanel, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
        
        // Show chart panel
        cardLayout.show(reportResultPanel, "chart");
        
        // Run report generation in background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Simulate delay for animation
                Thread.sleep(500);
                return null;
            }
            
            @Override
            protected void done() {
                // Generate report
                generateReport.run();
            }
        };
        
        worker.execute();
    }

    private void configureReportPanel() {
        // Get selected report type
        String selectedReport = (String) reportTypeComboBox.getSelectedItem();

        if (selectedReport == null) {
            return;
        }

        // Show appropriate configuration panel
        CardLayout cardLayout = (CardLayout) reportConfigPanel.getLayout();
        cardLayout.show(reportConfigPanel, selectedReport);

        // Clear result panels
        chartPanel.removeAll();
        dataPanel.removeAll();

        // Add placeholder text
        JPanel placeholderPanel = createPlaceholderPanel("Configure and generate a report to see results");
        
        chartPanel.add(placeholderPanel, BorderLayout.CENTER);

        // Refresh panels
        chartPanel.revalidate();
        chartPanel.repaint();
        dataPanel.revalidate();
        dataPanel.repaint();
    }

    private JPanel createPlaceholderPanel(String message) {
        JPanel placeholderPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CARD_RADIUS, CARD_RADIUS);
                
                // Draw message
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(LABEL_FONT);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(message);
                int textHeight = fm.getHeight();
                g2d.drawString(message, (getWidth() - textWidth) / 2, (getHeight() - textHeight) / 2 + fm.getAscent());
            }
        };
        placeholderPanel.setOpaque(false);
        placeholderPanel.setPreferredSize(new Dimension(400, 300));
        return placeholderPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Reports Dashboard");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);

        JLabel descLabel = new JLabel("Generate and visualize data reports for the motor pool");
        descLabel.setFont(SUBTITLE_FONT);
        descLabel.setForeground(SUBTITLE_COLOR);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(descLabel, BorderLayout.CENTER);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createReportSelectionPanel() {
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setOpaque(false);
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel reportTypeLabel = new JLabel("Report Type:");
        reportTypeLabel.setFont(LABEL_FONT);
        reportTypeLabel.setForeground(TEXT_COLOR);

        String[] reportTypes = {
                VEHICLE_USAGE,
                RESERVATION_STATUS,
                USER_ACTIVITY,
                USAGE_STATISTICS
        };

        reportTypeComboBox = new JComboBox<>(reportTypes);
        reportTypeComboBox.setFont(VALUE_FONT);
        reportTypeComboBox.setBackground(CARD_BACKGROUND);
        reportTypeComboBox.setForeground(TEXT_COLOR);
        reportTypeComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        reportTypeComboBox.setPreferredSize(new Dimension(250, 40));
        reportTypeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(ACCENT_COLOR);
                    c.setForeground(TEXT_COLOR);
                } else {
                    c.setBackground(CARD_BACKGROUND);
                    c.setForeground(new Color(255, 255, 255)); // Bright white for better visibility
                }
                return c;
            }
        });

        reportTypeComboBox.addActionListener(e -> {
            animateConfigPanelChange();
        });

        JButton toggleViewButton = createStyledButton("Toggle View", SECONDARY_COLOR);
        toggleViewButton.addActionListener(e -> {
            // Check which card is visible and show the other one
            Component[] components = reportResultPanel.getComponents();
            for (Component component : components) {
                if (component.isVisible()) {
                    if (component == chartPanel) {
                        animateCardChange("data");
                    } else {
                        animateCardChange("chart");
                    }
                    break;
                }
            }
        });

        selectionPanel.add(reportTypeLabel);
        selectionPanel.add(Box.createHorizontalStrut(10));
        selectionPanel.add(reportTypeComboBox);
        selectionPanel.add(Box.createHorizontalStrut(20));
        selectionPanel.add(toggleViewButton);

        return selectionPanel;
    }

    private JPanel createVehicleUsageConfigPanel() {
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setOpaque(false);
        configPanel.setBorder(createPanelBorder("Report Configuration"));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Date range selection
        JLabel startLabel = new JLabel("Start Date:");
        startLabel.setFont(LABEL_FONT);
        startLabel.setForeground(TEXT_COLOR);

        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "MM/dd/yyyy");
        startDateSpinner.setEditor(startDateEditor);
        styleSpinner(startDateSpinner);

        JLabel endLabel = new JLabel("End Date:");
        endLabel.setFont(LABEL_FONT);
        endLabel.setForeground(TEXT_COLOR);

        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "MM/dd/yyyy");
        endDateSpinner.setEditor(endDateEditor);
        styleSpinner(endDateSpinner);

        // Vehicle selection
        JLabel vehicleLabel = new JLabel("Vehicle (optional):");
        vehicleLabel.setFont(LABEL_FONT);
        vehicleLabel.setForeground(TEXT_COLOR);

        List<Vehicle> vehicles = DataManager.getInstance().getAllVehicles();
        Vehicle[] vehicleArray = new Vehicle[vehicles.size() + 1];
        vehicleArray[0] = null; // For "All Vehicles" option
        for (int i = 0; i < vehicles.size(); i++) {
            vehicleArray[i + 1] = vehicles.get(i);
        }

        JComboBox<Vehicle> vehicleComboBox = new JComboBox<>(vehicleArray);
        vehicleComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    value = "All Vehicles";
                }
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(ACCENT_COLOR);
                    c.setForeground(TEXT_COLOR);
                } else {
                    c.setBackground(CARD_BACKGROUND);
                    c.setForeground(new Color(255, 255, 255)); // Bright white for better visibility
                }
                return c;
            }
        });
        styleComboBox(vehicleComboBox);

        // Add components to form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(startLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(startDateSpinner, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        formPanel.add(endLabel, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        formPanel.add(endDateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(vehicleLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        formPanel.add(vehicleComboBox, gbc);

        // Generate button
        JButton generateButton = createStyledButton("Generate Report", ACCENT_COLOR);
        generateButton.addActionListener(e -> {
            // Get selected date range
            java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
            java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();

            LocalDate start = startDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate end = endDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            // Get selected vehicle
            Vehicle selectedVehicle = (Vehicle) vehicleComboBox.getSelectedItem();

            // Generate report with animation
            animateReportGeneration(() -> generateVehicleUsageReport(start, end, selectedVehicle));
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(generateButton);

        // Add components to config panel
        configPanel.add(formPanel);
        configPanel.add(Box.createVerticalStrut(10));
        configPanel.add(buttonPanel);

        return configPanel;
    }

    private JPanel createReservationStatusConfigPanel() {
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setOpaque(false);
        configPanel.setBorder(createPanelBorder("Report Configuration"));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Date range selection
        JLabel startLabel = new JLabel("Start Date:");
        startLabel.setFont(LABEL_FONT);
        startLabel.setForeground(TEXT_COLOR);

        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "MM/dd/yyyy");
        startDateSpinner.setEditor(startDateEditor);
        styleSpinner(startDateSpinner);

        JLabel endLabel = new JLabel("End Date:");
        endLabel.setFont(LABEL_FONT);
        endLabel.setForeground(TEXT_COLOR);

        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "MM/dd/yyyy");
        endDateSpinner.setEditor(endDateEditor);
        styleSpinner(endDateSpinner);

        // Status selection
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(LABEL_FONT);
        statusLabel.setForeground(TEXT_COLOR);

        String[] statuses = {"All", "PENDING", "APPROVED", "REJECTED", "CANCELLED", "COMPLETED"};
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        styleComboBox(statusComboBox);

        // Add components to form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(startLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(startDateSpinner, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        formPanel.add(endLabel, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        formPanel.add(endDateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(statusLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        formPanel.add(statusComboBox, gbc);

        // Generate button
        JButton generateButton = createStyledButton("Generate Report", ACCENT_COLOR);
        generateButton.addActionListener(e -> {
            // Get selected date range
            java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
            java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();

            LocalDate start = startDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate end = endDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            // Get selected status
            String selectedStatus = (String) statusComboBox.getSelectedItem();

            // Generate report with animation
            animateReportGeneration(() -> generateReservationStatusReport(start, end, selectedStatus));
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(generateButton);

        // Add components to config panel
        configPanel.add(formPanel);
        configPanel.add(Box.createVerticalStrut(10));
        configPanel.add(buttonPanel);

        return configPanel;
    }

    private JPanel createUserActivityConfigPanel() {
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setOpaque(false);
        configPanel.setBorder(createPanelBorder("Report Configuration"));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Date range selection
        JLabel startLabel = new JLabel("Start Date:");
        startLabel.setFont(LABEL_FONT);
        startLabel.setForeground(TEXT_COLOR);

        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "MM/dd/yyyy");
        startDateSpinner.setEditor(startDateEditor);
        styleSpinner(startDateSpinner);

        JLabel endLabel = new JLabel("End Date:");
        endLabel.setFont(LABEL_FONT);
        endLabel.setForeground(TEXT_COLOR);

        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "MM/dd/yyyy");
        endDateSpinner.setEditor(endDateEditor);
        styleSpinner(endDateSpinner);

        // User role selection
        JLabel roleLabel = new JLabel("User Role:");
        roleLabel.setFont(LABEL_FONT);
        roleLabel.setForeground(TEXT_COLOR);

        String[] roles = {"All", "ADMIN", "USER", "STUDENT", "FACULTY", "STAFF"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        styleComboBox(roleComboBox);

        // Add components to form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(startLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(startDateSpinner, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        formPanel.add(endLabel, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        formPanel.add(endDateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        formPanel.add(roleComboBox, gbc);

        // Generate button
        JButton generateButton = createStyledButton("Generate Report", ACCENT_COLOR);
        generateButton.addActionListener(e -> {
            // Get selected date range
            java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
            java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();

            LocalDate start = startDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate end = endDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            // Get selected role
            String selectedRole = (String) roleComboBox.getSelectedItem();

            // Generate report with animation
            animateReportGeneration(() -> generateUserActivityReport(start, end, selectedRole));
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(generateButton);

        // Add components to config panel
        configPanel.add(formPanel);
        configPanel.add(Box.createVerticalStrut(10));
        configPanel.add(buttonPanel);

        return configPanel;
    }

    private JPanel createUsageStatisticsConfigPanel() {
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setOpaque(false);
        configPanel.setBorder(createPanelBorder("Report Configuration"));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Date range selection
        JLabel startLabel = new JLabel("Start Date:");
        startLabel.setFont(LABEL_FONT);
        startLabel.setForeground(TEXT_COLOR);

        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "MM/dd/yyyy");
        startDateSpinner.setEditor(startDateEditor);
        styleSpinner(startDateSpinner);

        JLabel endLabel = new JLabel("End Date:");
        endLabel.setFont(LABEL_FONT);
        endLabel.setForeground(TEXT_COLOR);

        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "MM/dd/yyyy");
        endDateSpinner.setEditor(endDateEditor);
        styleSpinner(endDateSpinner);

        // Top N selection
        JLabel topNLabel = new JLabel("Show Top:");
        topNLabel.setFont(LABEL_FONT);
        topNLabel.setForeground(TEXT_COLOR);

        Integer[] topOptions = {5, 10, 15, 20, Integer.MAX_VALUE};
        JComboBox<Integer> topNComboBox = new JComboBox<>(topOptions);
        styleComboBox(topNComboBox);
        topNComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value != null && value.equals(Integer.MAX_VALUE)) {
                    value = "All";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        // Add components to form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(startLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(startDateSpinner, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        formPanel.add(endLabel, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        formPanel.add(endDateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(topNLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        formPanel.add(topNComboBox, gbc);

        // Generate button
        JButton generateButton = createStyledButton("Generate Report", ACCENT_COLOR);
        generateButton.addActionListener(e -> {
            // Get selected date range
            java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
            java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();

            LocalDate start = startDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate end = endDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            // Get selected top N
            Integer topN = (Integer) topNComboBox.getSelectedItem();

            // Generate report with animation
            animateReportGeneration(() -> generateUsageStatisticsReport(start, end, topN));
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(generateButton);

        // Add components to config panel
        configPanel.add(formPanel);
        configPanel.add(Box.createVerticalStrut(10));
        configPanel.add(buttonPanel);

        return configPanel;
    }

    private void generateVehicleUsageReport(LocalDate startDate, LocalDate endDate, Vehicle selectedVehicle) {
        // Clear result panels
        chartPanel.removeAll();
        dataPanel.removeAll();

        // Get reservations
        List<Reservation> reservations = DataManager.getInstance().getAllReservations();

        // Group reservations by vehicle
        Map<String, Integer> reservationCountMap = new HashMap<>();
        Map<String, Double> totalHoursMap = new HashMap<>();
        Map<String, Double> totalMilesMap = new HashMap<>();
        Map<String, String> vehicleNameMap = new HashMap<>();

        for (Reservation reservation : reservations) {
            // Skip if not in date range
            if (reservation.getStartDateTime().toLocalDate().isBefore(startDate) ||
                    reservation.getEndDateTime().toLocalDate().isAfter(endDate)) {
                continue;
            }

            // Skip if not selected vehicle
            if (selectedVehicle != null && !reservation.getVehicleId().equals(selectedVehicle.getVehicleId())) {
                continue;
            }

            // Skip if not completed
            if (reservation.getStatus() != Reservation.ReservationStatus.COMPLETED) {
                continue;
            }

            // Update maps
            String vehicleId = reservation.getVehicleId();
            Vehicle vehicle = DataManager.getInstance().getVehicleById(vehicleId);
            if (vehicle == null) continue;

            // Store vehicle name
            vehicleNameMap.put(vehicleId, vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel());

            // Increment reservation count
            reservationCountMap.put(vehicleId, reservationCountMap.getOrDefault(vehicleId, 0) + 1);

            // Calculate hours
            double hours = ChronoUnit.HOURS.between(reservation.getStartDateTime(), reservation.getEndDateTime());
            totalHoursMap.put(vehicleId, totalHoursMap.getOrDefault(vehicleId, 0.0) + hours);

            // Calculate miles
            double miles = reservation.getFinalMileage() - reservation.getInitialMileage();
            totalMilesMap.put(vehicleId, totalMilesMap.getOrDefault(vehicleId, 0.0) + miles);
        }

        // Create table model for data panel
        String[] columns = {"Vehicle", "Total Reservations", "Total Hours", "Avg Hours/Reservation", "Total Miles"};
        Object[][] data = new Object[reservationCountMap.size()][5];

        int row = 0;
        for (String vehicleId : reservationCountMap.keySet()) {
            int reservationCount = reservationCountMap.get(vehicleId);
            double totalHours = totalHoursMap.get(vehicleId);
            double avgHours = totalHours / reservationCount;
            double totalMiles = totalMilesMap.get(vehicleId);
            String vehicleName = vehicleNameMap.get(vehicleId);

            // Add to table data
            data[row][0] = vehicleName;
            data[row][1] = reservationCount;
            data[row][2] = String.format("%.1f", totalHours);
            data[row][3] = String.format("%.1f", avgHours);
            data[row][4] = String.format("%.1f", totalMiles);

            row++;
        }

        // Create dashboard-style layout with multiple charts
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        dashboardPanel.setOpaque(false);

        // Create modern circular progress indicators for key metrics
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        metricsPanel.setOpaque(false);

        // Calculate total metrics
        int totalReservations = reservationCountMap.values().stream().mapToInt(Integer::intValue).sum();
        double totalHours = totalHoursMap.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalMiles = totalMilesMap.values().stream().mapToDouble(Double::doubleValue).sum();
        
        // Add circular progress indicators
        int totalVehicles = allVehicles.size();
        metricsPanel.add(createCircularProgressPanel("Vehicles", reservationCountMap.size(), totalVehicles, CHART_COLORS[0]));
        metricsPanel.add(createCircularProgressPanel("Reservations", totalReservations, totalReservations, CHART_COLORS[1]));
        metricsPanel.add(createCircularProgressPanel("Hours", (int)totalHours, (int)totalHours, CHART_COLORS[2]));
        metricsPanel.add(createCircularProgressPanel("Miles", (int)totalMiles, (int)totalMiles, CHART_COLORS[3]));

        // Create modern bar chart for vehicle usage
        JPanel barChartPanel = createModernBarChart(
                vehicleNameMap.values().toArray(new String[0]),
                totalHoursMap.values().toArray(new Double[0]),
                totalMilesMap.values().toArray(new Double[0]),
                "Vehicle Usage - Hours and Miles"
        );

        // Create modern donut chart for reservations
        JPanel donutChartPanel = createModernDonutChart(
                vehicleNameMap.values().toArray(new String[0]),
                reservationCountMap.values().toArray(new Integer[0]),
                "Reservations by Vehicle"
        );

        // Create radar chart for vehicle metrics
        JPanel radarChartPanel = createRadarChart(
                vehicleNameMap.values().toArray(new String[0]),
                totalHoursMap.values().toArray(new Double[0]),
                totalMilesMap.values().toArray(new Double[0]),
                "Vehicle Metrics Comparison"
        );

        // Add charts to dashboard
        dashboardPanel.add(metricsPanel);
        dashboardPanel.add(barChartPanel);
        dashboardPanel.add(donutChartPanel);
        dashboardPanel.add(radarChartPanel);

        // Create table for data panel
        JTable resultTable = new JTable(data, columns);
        resultTable.setFillsViewportHeight(true);
        resultTable.setRowHeight(30);
        resultTable.setFont(VALUE_FONT);
        resultTable.getTableHeader().setFont(LABEL_FONT);

        // Style table
        styleTable(resultTable);

        JScrollPane tableScrollPane = new JScrollPane(resultTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Add components to panels
        JPanel chartContentPanel = new JPanel(new BorderLayout());
        chartContentPanel.setOpaque(false);
        chartContentPanel.setBorder(createPanelBorder("Vehicle Usage Report"));
        chartContentPanel.add(dashboardPanel, BorderLayout.CENTER);

        JPanel dataContentPanel = new JPanel(new BorderLayout());
        dataContentPanel.setOpaque(false);
        dataContentPanel.setBorder(createPanelBorder("Vehicle Usage Data"));
        dataContentPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Add export button
        JButton exportButton = createStyledButton("Export to CSV", SECONDARY_COLOR);
        exportButton.addActionListener(e -> exportToCSV(resultTable, "vehicle_usage_report"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(exportButton);

        dataContentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add to result panels
        chartPanel.add(chartContentPanel, BorderLayout.CENTER);
        
        // Enhance data panel with summary metrics
        Map<String, Object> summaryMetrics = new HashMap<>();
        summaryMetrics.put("Total Reservations", totalReservations);
        summaryMetrics.put("Total Hours", String.format("%.1f", totalHours));
        summaryMetrics.put("Total Miles", String.format("%.1f", totalMiles));
        
        JPanel enhancedDataPanel = enhanceDataPanel(dataContentPanel, "Vehicle Usage Data", summaryMetrics);
        dataPanel.add(enhancedDataPanel, BorderLayout.CENTER);

        // Refresh panels
        chartPanel.revalidate();
        chartPanel.repaint();
        dataPanel.revalidate();
        dataPanel.repaint();

        // Show success message
        parentFrame.setStatusMessage("Vehicle usage report generated successfully");
    }

    private void generateReservationStatusReport(LocalDate startDate, LocalDate endDate, String selectedStatus) {
        // Clear result panels
        chartPanel.removeAll();
        dataPanel.removeAll();

        // Get reservations
        List<Reservation> reservations = DataManager.getInstance().getAllReservations();

        // Filter reservations
        List<Reservation> filteredReservations = new ArrayList<>();
        Map<Reservation.ReservationStatus, Integer> statusCountMap = new HashMap<>();

        for (Reservation reservation : reservations) {
            // Skip if not in date range
            if (reservation.getStartDateTime().toLocalDate().isBefore(startDate) ||
                    reservation.getEndDateTime().toLocalDate().isAfter(endDate)) {
                continue;
            }

            // Filter by status if not "All"
            if (!selectedStatus.equals("All")) {
                if (reservation.getStatus() == null || !reservation.getStatus().toString().equals(selectedStatus)) {
                    continue;
                }
            }

            // Add to filtered list
            filteredReservations.add(reservation);

            // Update status count
            Reservation.ReservationStatus status = reservation.getStatus();
            statusCountMap.put(status, statusCountMap.getOrDefault(status, 0) + 1);
        }

        // Create table model for data panel
        String[] columns = {"ID", "User", "Vehicle", "Start Date", "End Date", "Status"};
        Object[][] data = new Object[filteredReservations.size()][6];

        for (int i = 0; i < filteredReservations.size(); i++) {
            Reservation reservation = filteredReservations.get(i);
            User user = DataManager.getInstance().getUserById(reservation.getUserId());
            Vehicle vehicle = DataManager.getInstance().getVehicleById(reservation.getVehicleId());

            data[i][0] = reservation.getReservationId();
            data[i][1] = user != null ? user.getFullName() : "Unknown";
            data[i][2] = vehicle != null ?
                    vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel() : "Unknown";
            data[i][3] = reservation.getStartDateTime().format(DATE_TIME_FORMATTER);
            data[i][4] = reservation.getEndDateTime().format(DATE_TIME_FORMATTER);
            data[i][5] = reservation.getStatus();
        }

        // Create dashboard-style layout with multiple charts
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        dashboardPanel.setOpaque(false);

        // Create status distribution data
        String[] statusLabels = new String[statusCountMap.size()];
        Integer[] statusCounts = new Integer[statusCountMap.size()];

        int index = 0;
        for (Map.Entry<Reservation.ReservationStatus, Integer> entry : statusCountMap.entrySet()) {
            statusLabels[index] = entry.getKey().toString();
            statusCounts[index] = entry.getValue();
            index++;
        }

        // Create metrics panel with circular indicators
        JPanel metricsPanel = new JPanel(new GridLayout(1, statusCountMap.size(), 10, 0));
        metricsPanel.setOpaque(false);

        index = 0;
        int totalReservations = filteredReservations.size();
        for (Map.Entry<Reservation.ReservationStatus, Integer> entry : statusCountMap.entrySet()) {
            int count = entry.getValue();
            int percentage = (totalReservations > 0) ? (int)(((double)count / totalReservations) * 100) : 0;
            metricsPanel.add(createCircularProgressPanel(
                entry.getKey().toString(), 
                count, 
                totalReservations, 
                CHART_COLORS[index % CHART_COLORS.length]
            ));
            index++;
        }

        // Create modern donut chart for status distribution
        JPanel donutChartPanel = createModernDonutChart(
                statusLabels,
                statusCounts,
                "Reservations by Status"
        );

        // Create time-based line chart for reservations over time
        Map<String, Integer> reservationsByMonth = new TreeMap<>();
        Map<String, Map<Reservation.ReservationStatus, Integer>> statusByMonth = new TreeMap<>();
        
        // Group reservations by month and status
        for (Reservation reservation : filteredReservations) {
            String month = reservation.getStartDateTime().getMonth().toString();
            reservationsByMonth.put(month, reservationsByMonth.getOrDefault(month, 0) + 1);
            
            statusByMonth.putIfAbsent(month, new HashMap<>());
            Map<Reservation.ReservationStatus, Integer> monthStatusMap = statusByMonth.get(month);
            monthStatusMap.put(reservation.getStatus(), monthStatusMap.getOrDefault(reservation.getStatus(), 0) + 1);
        }
        
        // Create line chart
        JPanel lineChartPanel = createModernLineChart(
            reservationsByMonth.keySet().toArray(new String[0]),
            reservationsByMonth.values().toArray(new Integer[0]),
            "Reservations Over Time"
        );

        // Create stacked bar chart for status distribution over time
        JPanel stackedBarChartPanel = createStackedBarChart(
            statusByMonth,
            "Status Distribution by Month"
        );

        // Add charts to dashboard
        dashboardPanel.add(metricsPanel);
        dashboardPanel.add(donutChartPanel);
        dashboardPanel.add(lineChartPanel);
        dashboardPanel.add(stackedBarChartPanel);

        // Create table for data panel
        JTable resultTable = new JTable(data, columns);
        resultTable.setFillsViewportHeight(true);
        resultTable.setRowHeight(30);
        resultTable.setFont(VALUE_FONT);
        resultTable.getTableHeader().setFont(LABEL_FONT);

        // Style table
        styleTable(resultTable);

        JScrollPane tableScrollPane = new JScrollPane(resultTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Add components to panels
        JPanel chartContentPanel = new JPanel(new BorderLayout());
        chartContentPanel.setOpaque(false);
        chartContentPanel.setBorder(createPanelBorder("Reservation Status Report"));
        chartContentPanel.add(dashboardPanel, BorderLayout.CENTER);

        JPanel dataContentPanel = new JPanel(new BorderLayout());
        dataContentPanel.setOpaque(false);
        dataContentPanel.setBorder(createPanelBorder("Reservation Data"));
        dataContentPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Add export button
        JButton exportButton = createStyledButton("Export to CSV", SECONDARY_COLOR);
        exportButton.addActionListener(e -> exportToCSV(resultTable, "reservation_status_report"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(exportButton);

        dataContentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add to result panels
        chartPanel.add(chartContentPanel, BorderLayout.CENTER);
        
        // Enhance data panel with summary metrics
        Map<String, Object> summaryMetrics = new HashMap<>();
        summaryMetrics.put("Total Reservations", totalReservations);
        summaryMetrics.put("Pending", statusCountMap.getOrDefault(Reservation.ReservationStatus.PENDING, 0));
        summaryMetrics.put("Approved", statusCountMap.getOrDefault(Reservation.ReservationStatus.APPROVED, 0));
        summaryMetrics.put("Rejected", statusCountMap.getOrDefault(Reservation.ReservationStatus.REJECTED, 0));
        summaryMetrics.put("Cancelled", statusCountMap.getOrDefault(Reservation.ReservationStatus.CANCELLED, 0));
        summaryMetrics.put("Completed", statusCountMap.getOrDefault(Reservation.ReservationStatus.COMPLETED, 0));
        
        JPanel enhancedDataPanel = enhanceDataPanel(dataContentPanel, "Reservation Data", summaryMetrics);
        dataPanel.add(enhancedDataPanel, BorderLayout.CENTER);

        // Refresh panels
        chartPanel.revalidate();
        chartPanel.repaint();
        dataPanel.revalidate();
        dataPanel.repaint();

        // Show success message
        parentFrame.setStatusMessage("Reservation status report generated successfully");
    }

    private void generateUserActivityReport(LocalDate startDate, LocalDate endDate, String selectedRole) {
        // Clear result panels
        chartPanel.removeAll();
        dataPanel.removeAll();

        // Get users and reservations
        List<User> users = DataManager.getInstance().getAllUsers();
        List<Reservation> reservations = DataManager.getInstance().getAllReservations();

        // Filter users by role
        if (!selectedRole.equals("All")) {
            users = users.stream()
                    .filter(u -> u.getRole().toString().equals(selectedRole))
                    .collect(Collectors.toList());
        }

        // Count reservations by user
        Map<String, Integer> userReservationCountMap = new HashMap<>();
        Map<String, String> userNameMap = new HashMap<>();

        for (User user : users) {
            userNameMap.put(user.getUserId(), user.getFullName());
            userReservationCountMap.put(user.getUserId(), 0);
        }

        for (Reservation reservation : reservations) {
            // Skip if not in date range
            if (reservation.getStartDateTime().toLocalDate().isBefore(startDate) ||
                    reservation.getEndDateTime().toLocalDate().isAfter(endDate)) {
                continue;
            }

            String userId = reservation.getUserId();
            if (userReservationCountMap.containsKey(userId)) {
                userReservationCountMap.put(userId, userReservationCountMap.getOrDefault(userId, 0) + 1);
            }
        }

        // Create table model for data panel
        String[] columns = {"User", "Role", "Reservations"};
        Object[][] data = new Object[users.size()][3];

        // Prepare data for pie chart - only include users with reservations
        List<String> activeUserNames = new ArrayList<>();
        List<Integer> activeUserCounts = new ArrayList<>();

        int row = 0;
        for (User user : users) {
            String userId = user.getUserId();
            int reservationCount = userReservationCountMap.get(userId);

            // Add to table data
            data[row][0] = user.getFullName();
            data[row][1] = user.getRole();
            data[row][2] = reservationCount;

            // Only add to pie chart if they have reservations
            if (reservationCount > 0) {
                activeUserNames.add(user.getFullName());
                activeUserCounts.add(reservationCount);
            }

            row++;
        }

        // Create dashboard-style layout with multiple charts
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        dashboardPanel.setOpaque(false);

        // Create metrics panel with circular indicators
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        metricsPanel.setOpaque(false);

        int totalUsers = users.size();
        int activeUsers = (int) users.stream().filter(u -> userReservationCountMap.get(u.getUserId()) > 0).count();
        int totalReservations = userReservationCountMap.values().stream().mapToInt(Integer::intValue).sum();
        double avgReservationsPerUser = totalUsers > 0 ? (double) totalReservations / totalUsers : 0;

        metricsPanel.add(createCircularProgressPanel("Total Users", totalUsers, totalUsers, CHART_COLORS[0]));
        metricsPanel.add(createCircularProgressPanel("Active Users", activeUsers, totalUsers, CHART_COLORS[1]));
        metricsPanel.add(createCircularProgressPanel("Total Reservations", totalReservations, totalReservations, CHART_COLORS[2]));
        metricsPanel.add(createCircularProgressPanel("Avg. Per User", (int)avgReservationsPerUser, (int)avgReservationsPerUser * 2, CHART_COLORS[3]));

        // Create modern donut chart for user activity
        JPanel donutChartPanel;
        if (!activeUserNames.isEmpty()) {
            donutChartPanel = createModernDonutChart(
                    activeUserNames.toArray(new String[0]),
                    activeUserCounts.toArray(new Integer[0]),
                    "User Activity - Reservations"
            );
        } else {
            donutChartPanel = createPlaceholderPanel("No reservation activity in selected period");
        }

        // Create bar chart for user activity
        JPanel barChartPanel;
        if (!activeUserNames.isEmpty()) {
            barChartPanel = createModernBarChart(
                    activeUserNames.toArray(new String[0]),
                    activeUserCounts.stream().map(Double::valueOf).toArray(Double[]::new),
                    null,
                    "User Reservation Activity"
            );
        } else {
            barChartPanel = createPlaceholderPanel("No reservation activity in selected period");
        }

        // Create role distribution chart
        Map<User.UserRole, Integer> roleCountMap = new HashMap<>();
        for (User user : users) {
            roleCountMap.put(user.getRole(), roleCountMap.getOrDefault(user.getRole(), 0) + 1);
        }

        String[] roleLabels = roleCountMap.keySet().stream().map(Enum::toString).toArray(String[]::new);
        Integer[] roleCounts = roleCountMap.values().toArray(new Integer[0]);

        JPanel roleChartPanel = createModernDonutChart(
                roleLabels,
                roleCounts,
                "User Role Distribution"
        );

        // Add charts to dashboard
        dashboardPanel.add(metricsPanel);
        dashboardPanel.add(donutChartPanel);
        dashboardPanel.add(barChartPanel);
        dashboardPanel.add(roleChartPanel);

        // Create table for data panel
        JTable resultTable = new JTable(data, columns);
        resultTable.setFillsViewportHeight(true);
        resultTable.setRowHeight(30);
        resultTable.setFont(VALUE_FONT);
        resultTable.getTableHeader().setFont(LABEL_FONT);

        // Style table
        styleTable(resultTable);

        JScrollPane tableScrollPane = new JScrollPane(resultTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Add components to panels
        JPanel chartContentPanel = new JPanel(new BorderLayout());
        chartContentPanel.setOpaque(false);
        chartContentPanel.setBorder(createPanelBorder("User Activity Report"));
        chartContentPanel.add(dashboardPanel, BorderLayout.CENTER);

        JPanel dataContentPanel = new JPanel(new BorderLayout());
        dataContentPanel.setOpaque(false);
        dataContentPanel.setBorder(createPanelBorder("User Activity Data"));
        dataContentPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Add export button
        JButton exportButton = createStyledButton("Export to CSV", SECONDARY_COLOR);
        exportButton.addActionListener(e -> exportToCSV(resultTable, "user_activity_report"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(exportButton);

        dataContentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add to result panels
        chartPanel.add(chartContentPanel, BorderLayout.CENTER);
        
        // Enhance data panel with summary metrics
        Map<String, Object> summaryMetrics = new HashMap<>();
        summaryMetrics.put("Total Users", totalUsers);
        summaryMetrics.put("Active Users", activeUsers);
        summaryMetrics.put("Total Reservations", totalReservations);
        summaryMetrics.put("Avg. Per User", String.format("%.1f", avgReservationsPerUser));
        
        JPanel enhancedDataPanel = enhanceDataPanel(dataContentPanel, "User Activity Data", summaryMetrics);
        dataPanel.add(enhancedDataPanel, BorderLayout.CENTER);

        // Refresh panels
        chartPanel.revalidate();
        chartPanel.repaint();
        dataPanel.revalidate();
        dataPanel.repaint();

        // Show success message
        parentFrame.setStatusMessage("User activity report generated successfully");
    }

    private void generateUsageStatisticsReport(LocalDate startDate, LocalDate endDate, Integer topN) {
        // Clear result panels
        chartPanel.removeAll();
        dataPanel.removeAll();

        // Get reservations
        List<Reservation> reservations = DataManager.getInstance().getAllReservations();

        // Filter reservations by date range
        List<Reservation> filteredReservations = reservations.stream()
                .filter(r -> !r.getStartDateTime().toLocalDate().isBefore(startDate) && 
                             !r.getEndDateTime().toLocalDate().isAfter(endDate))
                .collect(Collectors.toList());

        // Count reservations by vehicle
        Map<String, Integer> vehicleReservationCount = new HashMap<>();
        Map<String, String> vehicleNameMap = new HashMap<>();
        
        // Count reservations by user
        Map<String, Integer> userReservationCount = new HashMap<>();
        Map<String, String> userNameMap = new HashMap<>();

        for (Reservation reservation : filteredReservations) {
            // Count vehicle reservations
            String vehicleId = reservation.getVehicleId();
            Vehicle vehicle = DataManager.getInstance().getVehicleById(vehicleId);
            if (vehicle != null) {
                String vehicleName = vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel();
                vehicleNameMap.put(vehicleId, vehicleName);
                vehicleReservationCount.put(vehicleId, vehicleReservationCount.getOrDefault(vehicleId, 0) + 1);
            }
            
            // Count user reservations
            String userId = reservation.getUserId();
            User user = DataManager.getInstance().getUserById(userId);
            if (user != null) {
                String userName = user.getFullName();
                userNameMap.put(userId, userName);
                userReservationCount.put(userId, userReservationCount.getOrDefault(userId, 0) + 1);
            }
        }

        // Sort vehicles by reservation count (descending)
        List<Map.Entry<String, Integer>> sortedVehicles = new ArrayList<>(vehicleReservationCount.entrySet());
        sortedVehicles.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        
        // Sort users by reservation count (descending)
        List<Map.Entry<String, Integer>> sortedUsers = new ArrayList<>(userReservationCount.entrySet());
        sortedUsers.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Limit to top N if specified
        int vehicleLimit = (topN != null && topN != Integer.MAX_VALUE) ? Math.min(topN, sortedVehicles.size()) : sortedVehicles.size();
        int userLimit = (topN != null && topN != Integer.MAX_VALUE) ? Math.min(topN, sortedUsers.size()) : sortedUsers.size();
        
        List<Map.Entry<String, Integer>> topVehicles = sortedVehicles.subList(0, vehicleLimit);
        List<Map.Entry<String, Integer>> topUsers = sortedUsers.subList(0, userLimit);

        // Create dashboard-style layout with multiple charts
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        dashboardPanel.setOpaque(false);

        // Create metrics panel with circular indicators
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        metricsPanel.setOpaque(false);

        int totalReservations = filteredReservations.size();
        int uniqueVehicles = vehicleReservationCount.size();
        int uniqueUsers = userReservationCount.size();
        
        // Find most used vehicle and most active user
        String mostUsedVehicleId = !sortedVehicles.isEmpty() ? sortedVehicles.get(0).getKey() : null;
        String mostActiveUserId = !sortedUsers.isEmpty() ? sortedUsers.get(0).getKey() : null;
        
        int mostUsedVehicleCount = mostUsedVehicleId != null ? vehicleReservationCount.get(mostUsedVehicleId) : 0;
        int mostActiveUserCount = mostActiveUserId != null ? userReservationCount.get(mostActiveUserId) : 0;

        metricsPanel.add(createCircularProgressPanel("Total Reservations", totalReservations, totalReservations, CHART_COLORS[0]));
        metricsPanel.add(createCircularProgressPanel("Unique Vehicles", uniqueVehicles, uniqueVehicles, CHART_COLORS[1]));
        metricsPanel.add(createCircularProgressPanel("Unique Users", uniqueUsers, uniqueUsers, CHART_COLORS[2]));
        metricsPanel.add(createCircularProgressPanel("Avg. Per Vehicle", totalReservations / Math.max(1, uniqueVehicles), totalReservations, CHART_COLORS[3]));

        // Create bar chart for top vehicles
        JPanel vehicleBarChartPanel;
        if (!topVehicles.isEmpty()) {
            String[] vehicleLabels = topVehicles.stream()
                    .map(e -> vehicleNameMap.get(e.getKey()))
                    .toArray(String[]::new);
            
            Double[] vehicleCounts = topVehicles.stream()
                    .map(e -> e.getValue().doubleValue())
                    .toArray(Double[]::new);
            
            vehicleBarChartPanel = createModernBarChart(
                    vehicleLabels,
                    vehicleCounts,
                    null,
                    "Most Reserved Vehicles"
            );
        } else {
            vehicleBarChartPanel = createPlaceholderPanel("No vehicle reservation data available");
        }

        // Create bar chart for top users
        JPanel userBarChartPanel;
        if (!topUsers.isEmpty()) {
            String[] userLabels = topUsers.stream()
                    .map(e -> userNameMap.get(e.getKey()))
                    .toArray(String[]::new);
            
            Double[] userCounts = topUsers.stream()
                    .map(e -> e.getValue().doubleValue())
                    .toArray(Double[]::new);
            
            userBarChartPanel = createModernBarChart(
                    userLabels,
                    userCounts,
                    null,
                    "Most Active Users"
            );
        } else {
            userBarChartPanel = createPlaceholderPanel("No user activity data available");
        }

        // Create donut chart for vehicle usage distribution
        JPanel vehicleDonutChartPanel;
        if (!topVehicles.isEmpty()) {
            String[] vehicleLabels = topVehicles.stream()
                    .map(e -> vehicleNameMap.get(e.getKey()))
                    .toArray(String[]::new);
            
            Integer[] vehicleCounts = topVehicles.stream()
                    .map(Map.Entry::getValue)
                    .toArray(Integer[]::new);
            
            vehicleDonutChartPanel = createModernDonutChart(
                    vehicleLabels,
                    vehicleCounts,
                    "Vehicle Usage Distribution"
            );
        } else {
            vehicleDonutChartPanel = createPlaceholderPanel("No vehicle reservation data available");
        }

        // Add charts to dashboard
        dashboardPanel.add(metricsPanel);
        dashboardPanel.add(vehicleBarChartPanel);
        dashboardPanel.add(userBarChartPanel);
        dashboardPanel.add(vehicleDonutChartPanel);

        // Create table model for data panel - two tables side by side
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        tablesPanel.setOpaque(false);
        
        // Vehicle table
        String[] vehicleColumns = {"Rank", "Vehicle", "Reservations", "% of Total"};
        Object[][] vehicleData = new Object[topVehicles.size()][4];
        
        for (int i = 0; i < topVehicles.size(); i++) {
            Map.Entry<String, Integer> entry = topVehicles.get(i);
            String vehicleId = entry.getKey();
            int count = entry.getValue();
            double percentage = (double) count / totalReservations * 100;
            
            vehicleData[i][0] = i + 1;
            vehicleData[i][1] = vehicleNameMap.get(vehicleId);
            vehicleData[i][2] = count;
            vehicleData[i][3] = String.format("%.1f%%", percentage);
        }
        
        JTable vehicleTable = new JTable(vehicleData, vehicleColumns);
        vehicleTable.setFillsViewportHeight(true);
        vehicleTable.setRowHeight(30);
        vehicleTable.setFont(VALUE_FONT);
        vehicleTable.getTableHeader().setFont(LABEL_FONT);
        styleTable(vehicleTable);
        
        JScrollPane vehicleScrollPane = new JScrollPane(vehicleTable);
        vehicleScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        JPanel vehicleTablePanel = new JPanel(new BorderLayout());
        vehicleTablePanel.setOpaque(false);
        vehicleTablePanel.setBorder(createPanelBorder("Most Reserved Vehicles"));
        vehicleTablePanel.add(vehicleScrollPane, BorderLayout.CENTER);
        
        // User table
        String[] userColumns = {"Rank", "User", "Reservations", "% of Total"};
        Object[][] userData = new Object[topUsers.size()][4];
        
        for (int i = 0; i < topUsers.size(); i++) {
            Map.Entry<String, Integer> entry = topUsers.get(i);
            String userId = entry.getKey();
            int count = entry.getValue();
            double percentage = (double) count / totalReservations * 100;
            
            userData[i][0] = i + 1;
            userData[i][1] = userNameMap.get(userId);
            userData[i][2] = count;
            userData[i][3] = String.format("%.1f%%", percentage);
        }
        
        JTable userTable = new JTable(userData, userColumns);
        userTable.setFillsViewportHeight(true);
        userTable.setRowHeight(30);
        userTable.setFont(VALUE_FONT);
        userTable.getTableHeader().setFont(LABEL_FONT);
        styleTable(userTable);
        
        JScrollPane userScrollPane = new JScrollPane(userTable);
        userScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        JPanel userTablePanel = new JPanel(new BorderLayout());
        userTablePanel.setOpaque(false);
        userTablePanel.setBorder(createPanelBorder("Most Active Users"));
        userTablePanel.add(userScrollPane, BorderLayout.CENTER);
        
        tablesPanel.add(vehicleTablePanel);
        tablesPanel.add(userTablePanel);

        // Add components to panels
        JPanel chartContentPanel = new JPanel(new BorderLayout());
        chartContentPanel.setOpaque(false);
        chartContentPanel.setBorder(createPanelBorder("Usage Statistics Report"));
        chartContentPanel.add(dashboardPanel, BorderLayout.CENTER);

        JPanel dataContentPanel = new JPanel(new BorderLayout());
        dataContentPanel.setOpaque(false);
        dataContentPanel.setBorder(createPanelBorder("Usage Statistics Data"));
        dataContentPanel.add(tablesPanel, BorderLayout.CENTER);

        // Add export buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton exportVehiclesButton = createStyledButton("Export Vehicles", SECONDARY_COLOR);
        exportVehiclesButton.addActionListener(e -> exportToCSV(vehicleTable, "most_used_vehicles"));
        
        JButton exportUsersButton = createStyledButton("Export Users", SECONDARY_COLOR);
        exportUsersButton.addActionListener(e -> exportToCSV(userTable, "most_active_users"));
        
        buttonPanel.add(exportVehiclesButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(exportUsersButton);
        
        dataContentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add to result panels
        chartPanel.add(chartContentPanel, BorderLayout.CENTER);
        
        // Enhance data panel with summary metrics
        Map<String, Object> summaryMetrics = new HashMap<>();
        summaryMetrics.put("Total Reservations", totalReservations);
        summaryMetrics.put("Unique Vehicles", uniqueVehicles);
        summaryMetrics.put("Unique Users", uniqueUsers);
        
        JPanel enhancedDataPanel = enhanceDataPanel(dataContentPanel, "Usage Statistics Data", summaryMetrics);
        dataPanel.add(enhancedDataPanel, BorderLayout.CENTER);

        // Refresh panels
        chartPanel.revalidate();
        chartPanel.repaint();
        dataPanel.revalidate();
        dataPanel.repaint();

        // Show success message
        String message = "Usage Statistics Report generated successfully";
        if (mostUsedVehicleId != null && mostActiveUserId != null) {
            message += "\nMost used vehicle: " + vehicleNameMap.get(mostUsedVehicleId) + 
                      " (" + mostUsedVehicleCount + " reservations)" +
                      "\nMost active user: " + userNameMap.get(mostActiveUserId) + 
                      " (" + mostActiveUserCount + " reservations)";
        }
        parentFrame.setStatusMessage(message);
    }

    // Create modern circular progress indicator with animation
    private JPanel createCircularProgressPanel(String label, int value, int maxValue, Color color) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CARD_RADIUS, CARD_RADIUS);
                
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        
        JPanel circlePanel = new JPanel() {
            private float animatedValue = 0;
            private Timer animTimer = new Timer(16, null); // Initialize here
            
            {
                // Initialize animation timer
                animTimer = new Timer(16, e -> {
                    if (animatedValue < value) {
                        animatedValue = Math.min(value, animatedValue + Math.max(1, value / 30));
                        repaint();
                    } else {
                        animTimer.stop();
                    }
                });
                
                // Start animation when panel becomes visible
                addHierarchyListener(e -> {
                    if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                        if (isShowing()) {
                            animatedValue = 0;
                            animTimer.start();
                        } else {
                            animTimer.stop();
                        }
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = Math.min(getWidth(), getHeight()) - 20;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Calculate percentage
                int percentage = 0;
                if (maxValue > 0) {
                    percentage = (int)(((double)animatedValue / maxValue) * 100);
                }
                
                // Draw background circle
                g2d.setColor(new Color(60, 60, 80));
                g2d.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawArc(x, y, size, size, 0, 360);
                
                // Draw progress arc with gradient
                int startAngle = 90;
                int arcAngle = (int)(-3.6 * percentage);
                
                // Create gradient for progress arc
                Point2D center = new Point2D.Float(x + size/2, y + size/2);
                float radius = size/2;
                float[] dist = {0.0f, 1.0f};
                Color[] colors = {color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 150)};
                RadialGradientPaint paint = new RadialGradientPaint(center, radius, dist, colors);
                g2d.setPaint(paint);
                g2d.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawArc(x, y, size, size, startAngle, arcAngle);
                
                // Draw percentage text
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
                String percentText = percentage + "%";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(percentText);
                int textHeight = fm.getHeight();
                g2d.setColor(TEXT_COLOR);
                g2d.drawString(percentText, x + (size - textWidth)/2, y + (size + textHeight)/2 - 5);
                
                // Draw value
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
                String valueText = String.valueOf((int)animatedValue);
                textWidth = fm.stringWidth(valueText);
                g2d.drawString(valueText, x + (size - textWidth)/2, y + (size + textHeight)/2 + 20);
                
                g2d.dispose();
            }
        };
        circlePanel.setOpaque(false);
        circlePanel.setPreferredSize(new Dimension(150, 150));
        
        JLabel titleLabel = new JLabel(label, JLabel.CENTER);
        titleLabel.setFont(LABEL_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        
        panel.add(circlePanel, BorderLayout.CENTER);
        panel.add(titleLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Create modern donut chart with animation
    private JPanel createModernDonutChart(String[] labels, Number[] values, String title) {
        JPanel chartPanel = new JPanel() {
            private float[] animatedValues;
            private Timer animTimer = new Timer(16, null); // Initialize here
            
            {
                // Initialize animation values
                if (values != null && values.length > 0) {
                    animatedValues = new float[values.length];
                    
                    // Initialize animation timer
                    animTimer = new Timer(16, e -> {
                        boolean allDone = true;
                        for (int i = 0; i < values.length; i++) {
                            float target = values[i] != null ? values[i].floatValue() : 0;
                            if (animatedValues[i] < target) {
                                animatedValues[i] = Math.min(target, animatedValues[i] + Math.max(0.1f, target / 30));
                                allDone = false;
                            }
                        }
                        repaint();
                        if (allDone) {
                            animTimer.stop();
                        }
                    });
                    
                    // Start animation when panel becomes visible
                    addHierarchyListener(e -> {
                        if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                            if (isShowing()) {
                                for (int i = 0; i < animatedValues.length; i++) {
                                    animatedValues[i] = 0;
                                }
                                animTimer.start();
                            } else {
                                animTimer.stop();
                            }
                        }
                    });
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Draw background
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRoundRect(0, 0, width, height, CARD_RADIUS, CARD_RADIUS);
                
                // Draw title
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(CARD_TITLE_FONT);
                FontMetrics titleMetrics = g2d.getFontMetrics();
                int titleWidth = titleMetrics.stringWidth(title);
                g2d.drawString(title, (width - titleWidth) / 2, 25);
                
                // Calculate total value
                double total = 0;
                if (animatedValues != null) {
                    for (float value : animatedValues) {
                        total += value;
                    }
                }
                
                if (total <= 0 || animatedValues == null) {
                    g2d.setFont(VALUE_FONT);
                    g2d.drawString("No data available", width / 2 - 50, height / 2);
                    return;
                }
                
                // Draw donut chart
                int donutSize = Math.min(width, height) - 100;
                int centerX = width / 2;
                int centerY = height / 2;
                int innerRadius = donutSize / 3;
                int outerRadius = donutSize / 2;
                
                double currentAngle = 0;
                
                // Draw donut slices with gradient and 3D effect
                for (int i = 0; i < animatedValues.length; i++) {
                    if (animatedValues[i] <= 0) continue;
                    
                    double sliceAngle = 360.0 * animatedValues[i] / total;
                    
                    // Create a gradient for the slice
                    Color baseColor = CHART_COLORS[i % CHART_COLORS.length];
                    
                    // Create radial gradient
                    Point2D center = new Point2D.Float(centerX, centerY);
                    float radius = outerRadius;
                    float[] dist = {0.0f, 1.0f};
                    Color[] colors = {baseColor, new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 200)};
                    RadialGradientPaint paint = new RadialGradientPaint(center, radius, dist, colors);
                    
                    // Draw outer arc
                    g2d.setPaint(paint);
                    g2d.setStroke(new BasicStroke(outerRadius - innerRadius, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                    g2d.drawArc(centerX - outerRadius + (outerRadius - innerRadius)/2, 
                               centerY - outerRadius + (outerRadius - innerRadius)/2, 
                               2 * outerRadius - (outerRadius - innerRadius), 
                               2 * outerRadius - (outerRadius - innerRadius), 
                               (int) currentAngle, 
                               (int) sliceAngle);
                    
                    // Draw percentage on the slice if it's large enough
                    if (sliceAngle > 20) {
                        double middleAngle = Math.toRadians(currentAngle + sliceAngle / 2);
                        int labelRadius = (innerRadius + outerRadius) / 2;
                        int labelX = (int) (centerX + labelRadius * Math.cos(middleAngle));
                        int labelY = (int) (centerY + labelRadius * Math.sin(middleAngle));
                        
                        String percentText = String.format("%.0f%%", 100.0 * animatedValues[i] / total);
                        g2d.setColor(TEXT_COLOR);
                        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        FontMetrics percentFm = g2d.getFontMetrics();
                        g2d.drawString(percentText,
                                labelX - percentFm.stringWidth(percentText) / 2,
                                labelY + percentFm.getAscent() / 2);
                    }
                    
                    currentAngle += sliceAngle;
                }
                
                // Draw inner circle
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillOval(centerX - innerRadius, centerY - innerRadius, innerRadius * 2, innerRadius * 2);
                
                // Draw total in center
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
                String totalText = String.format("%.0f", total);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(totalText, centerX - fm.stringWidth(totalText) / 2, centerY + fm.getAscent() / 2);
                
                // Draw legend
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                FontMetrics metrics = g2d.getFontMetrics();
                int legendY = height - 40;
                int legendX = 20;
                int legendHeight = metrics.getHeight() + 5;
                int legendsPerRow = 3;
                int legendWidth = width / legendsPerRow;
                
                for (int i = 0; i < labels.length; i++) {
                    // Skip items with zero value
                    if (animatedValues[i] <= 0) continue;
                    
                    int row = i / legendsPerRow;
                    int col = i % legendsPerRow;
                    
                    int x = legendX + col * legendWidth;
                    int y = legendY - row * legendHeight;
                    
                    g2d.setColor(CHART_COLORS[i % CHART_COLORS.length]);
                    g2d.fillRoundRect(x, y - 10, 12, 12, 4, 4);
                    
                    g2d.setColor(TEXT_COLOR);
                    String label = labels[i];
                    if (label != null && label.length() > 20) {
                        label = label.substring(0, 17) + "...";
                    }
                    g2d.drawString(label, x + 18, y);
                }
                
                g2d.dispose();
            }
        };
        
        chartPanel.setPreferredSize(new Dimension(400, 300));
        return chartPanel;
    }
    
    // Create modern bar chart with animation
    private JPanel createModernBarChart(String[] labels, Double[] values1, Double[] values2, String title) {
        JPanel chartPanel = new JPanel() {
            private float[] animatedValues1;
            private float[] animatedValues2;
            private Timer animTimer = new Timer(16, null); // Initialize here
        
            {
                // Initialize animation values
                if (values1 != null && values1.length > 0) {
                    animatedValues1 = new float[values1.length];
                    
                    if (values2 != null && values2.length > 0) {
                        animatedValues2 = new float[values2.length];
                    }
                    
                    // Initialize animation timer
                    animTimer = new Timer(16, e -> {
                        boolean allDone = true;
                        
                        // Animate first series
                        for (int i = 0; i < values1.length; i++) {
                            float target = values1[i] != null ? values1[i].floatValue() : 0;
                            if (animatedValues1[i] < target) {
                                animatedValues1[i] = Math.min(target, animatedValues1[i] + Math.max(0.1f, target / 30));
                                allDone = false;
                            }
                        }
                        
                        // Animate second series if present
                        if (animatedValues2 != null) {
                            for (int i = 0; i < values2.length; i++) {
                                float target = values2[i] != null ? values2[i].floatValue() : 0;
                                if (animatedValues2[i] < target) {
                                    animatedValues2[i] = Math.min(target, animatedValues2[i] + Math.max(0.1f, target / 30));
                                    allDone = false;
                                }
                            }
                        }
                        
                        repaint();
                        if (allDone) {
                            animTimer.stop();
                        }
                    });
                    
                    // Start animation when panel becomes visible
                    addHierarchyListener(e -> {
                        if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                            if (isShowing()) {
                                for (int i = 0; i < animatedValues1.length; i++) {
                                    animatedValues1[i] = 0;
                                }
                                if (animatedValues2 != null) {
                                    for (int i = 0; i < animatedValues2.length; i++) {
                                        animatedValues2[i] = 0;
                                    }
                                }
                                animTimer.start();
                            } else {
                                animTimer.stop();
                            }
                        }
                    });
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Draw background
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRoundRect(0, 0, width, height, CARD_RADIUS, CARD_RADIUS);
                
                // Draw title
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(CARD_TITLE_FONT);
                FontMetrics titleMetrics = g2d.getFontMetrics();
                int titleWidth = titleMetrics.stringWidth(title);
                g2d.drawString(title, (width - titleWidth) / 2, 25);
                
                if (animatedValues1 == null || labels == null || labels.length == 0) {
                    g2d.setFont(VALUE_FONT);
                    g2d.drawString("No data available", width / 2 - 50, height / 2);
                    return;
                }
                
                int padding = 50;
                int chartWidth = width - 2 * padding;
                int chartHeight = height - 2 * padding - 20;
                
                // Find max value for scaling
                float maxValue = 0;
                for (float value : animatedValues1) {
                    maxValue = Math.max(maxValue, value);
                }
                
                if (animatedValues2 != null) {
                    for (float value : animatedValues2) {
                        maxValue = Math.max(maxValue, value);
                    }
                }
                
                // Add 10% padding to max value
                maxValue = maxValue * 1.1f;
                
                // Draw grid lines
                g2d.setColor(new Color(80, 80, 100, 100));
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
                int numGridLines = 5;
                for (int i = 0; i <= numGridLines; i++) {
                    int y = height - padding - (int) (chartHeight * i / numGridLines);
                    g2d.drawLine(padding, y, width - padding, y);
                }
                
                // Draw axes
                g2d.setColor(new Color(150, 150, 170));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
                g2d.drawLine(padding, height - padding, padding, padding); // Y-axis
                
                // Draw Y-axis labels
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                FontMetrics metrics = g2d.getFontMetrics();
                int labelHeight = metrics.getHeight();
                
                for (int i = 0; i <= numGridLines; i++) {
                    int value = (int) (maxValue * i / numGridLines);
                    String label = String.valueOf(value);
                    int labelWidth = metrics.stringWidth(label);
                    int y = height - padding - (int) (chartHeight * i / numGridLines);
                    
                    g2d.setColor(TEXT_COLOR);
                    g2d.drawString(label, padding - labelWidth - 5, y + labelHeight / 2 - 2);
                }
                
                // Draw bars
                int barWidth = chartWidth / (labels.length * 3); // Make bars narrower with spacing
                int barSpacing = barWidth / 2;
                
                for (int i = 0; i < labels.length; i++) {
                    int x = padding + i * (chartWidth / labels.length) + (chartWidth / labels.length) / 4;
                    int y = height - padding;
                    
                    // Draw first series bar
                    if (i < animatedValues1.length) {
                        int barHeight = (int) (chartHeight * animatedValues1[i] / maxValue);
                        
                        // Create gradient for bar
                        Color baseColor = CHART_COLORS[0];
                        GradientPaint gradient = new GradientPaint(
                                x, y - barHeight, baseColor,
                                x, y, new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 150)
                        );
                        g2d.setPaint(gradient);
                        
                        // Draw bar with rounded top
                        RoundRectangle2D.Double bar = new RoundRectangle2D.Double(
                                x, y - barHeight, barWidth, barHeight, 5, 5);
                        g2d.fill(bar);
                        
                        // Draw value on top of bar
                        String valueText = String.format("%.1f", animatedValues1[i]);
                        int valueWidth = metrics.stringWidth(valueText);
                        g2d.setColor(TEXT_COLOR);
                        g2d.drawString(valueText, x + barWidth/2 - valueWidth/2, y - barHeight - 5);
                    }
                    
                    // Draw second series bar if present
                    if (animatedValues2 != null && i < animatedValues2.length) {
                        int x2 = x + barWidth + barSpacing;
                        int barHeight = (int) (chartHeight * animatedValues2[i] / maxValue);
                        
                        // Create gradient for bar
                        Color baseColor = CHART_COLORS[1];
                        GradientPaint gradient = new GradientPaint(
                                x2, y - barHeight, baseColor,
                                x2, y, new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 150)
                        );
                        g2d.setPaint(gradient);
                        
                        // Draw bar with rounded top
                        RoundRectangle2D.Double bar = new RoundRectangle2D.Double(
                                x2, y - barHeight, barWidth, barHeight, 5, 5);
                        g2d.fill(bar);
                        
                        // Draw value on top of bar
                        String valueText = String.format("%.1f", animatedValues2[i]);
                        int valueWidth = metrics.stringWidth(valueText);
                        g2d.setColor(TEXT_COLOR);
                        g2d.drawString(valueText, x2 + barWidth/2 - valueWidth/2, y - barHeight - 5);
                    }
                    
                    // Draw X-axis label
                    String label = labels[i];
                    if (label.length() > 10) {
                        label = label.substring(0, 7) + "...";
                    }
                    int labelWidth = metrics.stringWidth(label);
                    g2d.setColor(TEXT_COLOR);
                    g2d.drawString(label, x + barWidth/2 - labelWidth/2, height - padding + labelHeight + 5);
                }
                
                // Draw legend if we have two series
                if (animatedValues2 != null) {
                    int legendY = 40;
                    int legendX = width - 150;
                    
                    g2d.setColor(CHART_COLORS[0]);
                    g2d.fillRoundRect(legendX, legendY, 15, 15, 4, 4);
                    g2d.setColor(TEXT_COLOR);
                    g2d.drawString("Hours", legendX + 20, legendY + 12);
                    
                    g2d.setColor(CHART_COLORS[1]);
                    g2d.fillRoundRect(legendX, legendY + 20, 15, 15, 4, 4);
                    g2d.setColor(TEXT_COLOR);
                    g2d.drawString("Miles", legendX + 20, legendY + 32);
                }
                
                g2d.dispose();
            }
        };
        
        chartPanel.setPreferredSize(new Dimension(400, 300));
        return chartPanel;
    }
    
    // Create modern line chart with animation
    private JPanel createModernLineChart(String[] labels, Number[] values, String title) {
        JPanel chartPanel = new JPanel() {
            private float[] animatedValues;
            private Timer animTimer = new Timer(16, null); // Initialize here
        
            {
                // Initialize animation values
                if (values != null && values.length > 0) {
                    animatedValues = new float[values.length];
                    
                    // Initialize animation timer
                    animTimer = new Timer(16, e -> {
                        boolean allDone = true;
                        for (int i = 0; i < values.length; i++) {
                            float target = values[i] != null ? values[i].floatValue() : 0;
                            if (animatedValues[i] < target) {
                                animatedValues[i] = Math.min(target, animatedValues[i] + Math.max(0.1f, target / 30));
                                allDone = false;
                            }
                        }
                        repaint();
                        if (allDone) {
                            animTimer.stop();
                        }
                    });
                    
                    // Start animation when panel becomes visible
                    addHierarchyListener(e -> {
                        if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                            if (isShowing()) {
                                for (int i = 0; i < animatedValues.length; i++) {
                                    animatedValues[i] = 0;
                                }
                                animTimer.start();
                            } else {
                                animTimer.stop();
                            }
                        }
                    });
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Draw background
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRoundRect(0, 0, width, height, CARD_RADIUS, CARD_RADIUS);
                
                // Draw title
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(CARD_TITLE_FONT);
                FontMetrics titleMetrics = g2d.getFontMetrics();
                int titleWidth = titleMetrics.stringWidth(title);
                g2d.drawString(title, (width - titleWidth) / 2, 25);
                
                if (animatedValues == null || labels == null || labels.length == 0) {
                    g2d.setFont(VALUE_FONT);
                    g2d.drawString("No data available", width / 2 - 50, height / 2);
                    return;
                }
                
                int padding = 50;
                int chartWidth = width - 2 * padding;
                int chartHeight = height - 2 * padding - 20;
                
                // Find max value for scaling
                float maxValue = 0;
                for (float value : animatedValues) {
                    maxValue = Math.max(maxValue, value);
                }
                
                // Add 10% padding to max value
                maxValue = maxValue * 1.1f;
                
                // Draw grid lines
                g2d.setColor(new Color(80, 80, 100, 100));
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
                int numGridLines = 5;
                for (int i = 0; i <= numGridLines; i++) {
                    int y = height - padding - (int) (chartHeight * i / numGridLines);
                    g2d.drawLine(padding, y, width - padding, y);
                }
                
                // Draw axes
                g2d.setColor(new Color(150, 150, 170));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
                g2d.drawLine(padding, height - padding, padding, padding); // Y-axis
                
                // Draw Y-axis labels
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                FontMetrics metrics = g2d.getFontMetrics();
                int labelHeight = metrics.getHeight();
                
                for (int i = 0; i <= numGridLines; i++) {
                    int value = (int) (maxValue * i / numGridLines);
                    String label = String.valueOf(value);
                    int labelWidth = metrics.stringWidth(label);
                    int y = height - padding - (int) (chartHeight * i / numGridLines);
                    
                    g2d.setColor(TEXT_COLOR);
                    g2d.drawString(label, padding - labelWidth - 5, y + labelHeight / 2 - 2);
                }
                
                // Draw line chart
                int pointSpacing = chartWidth / (labels.length - 1);
                if (labels.length == 1) {
                    pointSpacing = 0;
                }
                
                // Create points for the line
                int[] xPoints = new int[labels.length];
                int[] yPoints = new int[labels.length];
                
                for (int i = 0; i < labels.length; i++) {
                    xPoints[i] = padding + i * pointSpacing;
                    yPoints[i] = height - padding - (int) (chartHeight * animatedValues[i] / maxValue);
                }
                
                // Draw filled area under the line
                g2d.setColor(new Color(CHART_COLORS[0].getRed(), CHART_COLORS[0].getGreen(), CHART_COLORS[0].getBlue(), 50));
                
                // Create polygon for filled area
                Polygon fillPolygon = new Polygon();
                fillPolygon.addPoint(xPoints[0], height - padding); // Bottom left
                for (int i = 0; i < labels.length; i++) {
                    fillPolygon.addPoint(xPoints[i], yPoints[i]);
                }
                fillPolygon.addPoint(xPoints[labels.length - 1], height - padding); // Bottom right
                
                g2d.fill(fillPolygon);
                
                // Draw line with gradient
                g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                // Create gradient for line
                GradientPaint gradient = new GradientPaint(
                        padding, 0, CHART_COLORS[0],
                        width - padding, 0, CHART_COLORS[1]
                );
                g2d.setPaint(gradient);
                
                // Draw line segments
                for (int i = 0; i < labels.length - 1; i++) {
                    g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
                }
                
                // Draw points
                for (int i = 0; i < labels.length; i++) {
                    // Draw outer circle (white)
                    g2d.setColor(TEXT_COLOR);
                    g2d.fillOval(xPoints[i] - 5, yPoints[i] - 5, 10, 10);
                    
                    // Draw inner circle (colored)
                    g2d.setColor(CHART_COLORS[i % CHART_COLORS.length]);
                    g2d.fillOval(xPoints[i] - 3, yPoints[i] - 3, 6, 6);
                    
                    // Draw value above point
                    String valueText = String.format("%.1f", animatedValues[i]);
                    int valueWidth = metrics.stringWidth(valueText);
                    g2d.setColor(TEXT_COLOR);
                    g2d.drawString(valueText, xPoints[i] - valueWidth / 2, yPoints[i] - 10);
                }
                
                // Draw X-axis labels
                for (int i = 0; i < labels.length; i++) {
                    String label = labels[i];
                    if (label.length() > 10) {
                        label = label.substring(0, 7) + "...";
                    }
                    int labelWidth = metrics.stringWidth(label);
                    g2d.setColor(TEXT_COLOR);
                    g2d.drawString(label, xPoints[i] - labelWidth / 2, height - padding + labelHeight + 5);
                }
                
                g2d.dispose();
            }
        };
        
        chartPanel.setPreferredSize(new Dimension(400, 300));
        return chartPanel;
    }
    
    // Create stacked bar chart with animation
    private JPanel createStackedBarChart(Map<String, Map<Reservation.ReservationStatus, Integer>> data, String title) {
        JPanel chartPanel = new JPanel() {
            private Map<String, Map<Reservation.ReservationStatus, Float>> animatedData = new HashMap<>();
            private Timer animTimer = new Timer(16, null); // Initialize here
        
            {
                // Initialize animation data
                if (data != null && !data.isEmpty()) {
                    // Initialize animated data structure
                    for (String month : data.keySet()) {
                        Map<Reservation.ReservationStatus, Float> animatedValues = new HashMap<>();
                        for (Reservation.ReservationStatus status : data.get(month).keySet()) {
                            animatedValues.put(status, 0.0f);
                        }
                        animatedData.put(month, animatedValues);
                    }
                    
                    // Initialize animation timer
                    animTimer = new Timer(16, e -> {
                        boolean allDone = true;
                        
                        for (String month : data.keySet()) {
                            Map<Reservation.ReservationStatus, Integer> targetValues = data.get(month);
                            Map<Reservation.ReservationStatus, Float> currentValues = animatedData.get(month);
                            
                            for (Reservation.ReservationStatus status : targetValues.keySet()) {
                                float target = targetValues.get(status);
                                float current = currentValues.getOrDefault(status, 0.0f);
                                
                                if (current < target) {
                                    currentValues.put(status, Math.min(target, current + Math.max(0.1f, target / 30)));
                                    allDone = false;
                                }
                            }
                        }
                        
                        repaint();
                        if (allDone) {
                            animTimer.stop();
                        }
                    });
                    
                    // Start animation when panel becomes visible
                    addHierarchyListener(e -> {
                        if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                            if (isShowing()) {
                                // Reset animation values
                                for (String month : data.keySet()) {
                                    Map<Reservation.ReservationStatus, Float> animatedValues = animatedData.get(month);
                                    for (Reservation.ReservationStatus status : animatedValues.keySet()) {
                                        animatedValues.put(status, 0.0f);
                                    }
                                }
                                animTimer.start();
                            } else {
                                animTimer.stop();
                            }
                        }
                    });
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Draw background
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRoundRect(0, 0, width, height, CARD_RADIUS, CARD_RADIUS);
                
                // Draw title
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(CARD_TITLE_FONT);
                FontMetrics titleMetrics = g2d.getFontMetrics();
                int titleWidth = titleMetrics.stringWidth(title);
                g2d.drawString(title, (width - titleWidth) / 2, 25);
                
                int padding = 50;
                int chartWidth = width - 2 * padding;
                int chartHeight = height - 2 * padding - 20;
                
                if (data == null || data.isEmpty() || animatedData.isEmpty()) {
                    g2d.setFont(VALUE_FONT);
                    g2d.drawString("No data available", width / 2 - 50, height / 2);
                    return;
                }
                
                // Get all status types and months
                Set<Reservation.ReservationStatus> allStatuses = new HashSet<>();
                for (Map<Reservation.ReservationStatus, Integer> monthData : data.values()) {
                    allStatuses.addAll(monthData.keySet());
                }
                
                // Convert to arrays for easier processing
                String[] months = data.keySet().toArray(new String[0]);
                Reservation.ReservationStatus[] statuses = allStatuses.toArray(new Reservation.ReservationStatus[0]);
                
                // Find max value for scaling
                int maxStackHeight = 0;
                for (Map<Reservation.ReservationStatus, Integer> monthData : data.values()) {
                    int stackHeight = monthData.values().stream().mapToInt(Integer::intValue).sum();
                    if (stackHeight > maxStackHeight) {
                        maxStackHeight = stackHeight;
                    }
                }
                
                // Add 10% padding to max value
                maxStackHeight = (int)(maxStackHeight * 1.1);
                
                // Draw grid lines
                g2d.setColor(new Color(80, 80, 100, 100));
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
                int numGridLines = 5;
                for (int i = 0; i <= numGridLines; i++) {
                    int y = height - padding - (int) (chartHeight * i / numGridLines);
                    g2d.drawLine(padding, y, width - padding, y);
                }
                
                // Draw axes
                g2d.setColor(new Color(150, 150, 170));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
                g2d.drawLine(padding, height - padding, padding, padding); // Y-axis
                
                // Draw Y-axis labels
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                FontMetrics metrics = g2d.getFontMetrics();
                int labelHeight = metrics.getHeight();
                
                for (int i = 0; i <= numGridLines; i++) {
                    int value = maxStackHeight * i / numGridLines;
                    String label = String.valueOf(value);
                    int labelWidth = metrics.stringWidth(label);
                    int y = height - padding - (int) (chartHeight * i / numGridLines);
                    
                    g2d.setColor(TEXT_COLOR);
                    g2d.drawString(label, padding - labelWidth - 5, y + labelHeight / 2 - 2);
                }
                
                // Draw stacked bars and X-axis labels
                int barWidth = chartWidth / (months.length * 2); // Make bars narrower with spacing
                
                for (int i = 0; i < months.length; i++) {
                    String month = months[i];
                    Map<Reservation.ReservationStatus, Float> monthData = animatedData.get(month);
                    
                    int x = padding + i * (chartWidth / months.length) + (chartWidth / months.length) / 4;
                    int y = height - padding;
                    
                    // Draw each segment of the stacked bar
                    for (int j = 0; j < statuses.length; j++) {
                        Reservation.ReservationStatus status = statuses[j];
                        float value = monthData.getOrDefault(status, 0.0f);
                        
                        if (value > 0) {
                            int barHeight = (int) (chartHeight * value / maxStackHeight);
                            
                            // Create gradient for bar segment
                            Color baseColor = CHART_COLORS[j % CHART_COLORS.length];
                            GradientPaint gradient = new GradientPaint(
                                    x, y - barHeight, baseColor,
                                    x, y, new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 150)
                            );
                            g2d.setPaint(gradient);
                            
                            // Draw bar segment with rounded corners for top segment
                            if (j == 0) { // First segment (bottom)
                                g2d.fillRect(x, y - barHeight, barWidth, barHeight);
                            } else if (j == statuses.length - 1 || !monthData.containsKey(statuses[j+1]) || monthData.get(statuses[j+1]) <= 0) {
                                // Last segment (top) - rounded top
                                RoundRectangle2D.Double bar = new RoundRectangle2D.Double(
                                        x, y - barHeight, barWidth, barHeight, 5, 5);
                                g2d.fill(bar);
                            } else {
                                // Middle segment
                                g2d.fillRect(x, y - barHeight, barWidth, barHeight);
                            }
                            
                            // Draw outline
                            g2d.setColor(new Color(255, 255, 255, 100));
                            g2d.drawRect(x, y - barHeight, barWidth, barHeight);
                            
                            // Update y position for next segment
                            y -= barHeight;
                        }
                    }
                    
                    // Draw X-axis label
                    g2d.setColor(TEXT_COLOR);
                    int labelWidth = metrics.stringWidth(month);
                    g2d.drawString(month, x + barWidth/2 - labelWidth/2, height - padding + labelHeight + 5);
                }
                
                // Draw legend
                int legendY = 40;
                int legendX = width - 150;
                
                for (int i = 0; i < statuses.length; i++) {
                    Reservation.ReservationStatus status = statuses[i];
                    Color color = CHART_COLORS[i % CHART_COLORS.length];
                    
                    g2d.setColor(color);
                    g2d.fillRoundRect(legendX, legendY + i*20, 15, 15, 4, 4);
                    g2d.setColor(TEXT_COLOR);
                    g2d.drawString(status.toString(), legendX + 20, legendY + i*20 + 12);
                }
                
                g2d.dispose();
            }
        };
        
        chartPanel.setPreferredSize(new Dimension(400, 300));
        return chartPanel;
    }
    
    // Create radar chart for vehicle metrics comparison
    private JPanel createRadarChart(String[] labels, Double[] values1, Double[] values2, String title) {
        JPanel chartPanel = new JPanel() {
            private float[] animatedValues1;
            private float[] animatedValues2;
            private Timer animTimer = new Timer(16, null); // Initialize here
        
            {
                // Initialize animation values
                if (values1 != null && values1.length > 0) {
                    animatedValues1 = new float[values1.length];
                    
                    if (values2 != null && values2.length > 0) {
                        animatedValues2 = new float[values2.length];
                    }
                    
                    // Initialize animation timer
                    animTimer = new Timer(16, e -> {
                        boolean allDone = true;
                        
                        // Animate first series
                        for (int i = 0; i < values1.length; i++) {
                            float target = values1[i] != null ? values1[i].floatValue() : 0;
                            if (animatedValues1[i] < target) {
                                animatedValues1[i] = Math.min(target, animatedValues1[i] + Math.max(0.1f, target / 30));
                                allDone = false;
                            }
                        }
                        
                        // Animate second series if present
                        if (animatedValues2 != null) {
                            for (int i = 0; i < values2.length; i++) {
                                float target = values2[i] != null ? values2[i].floatValue() : 0;
                                if (animatedValues2[i] < target) {
                                    animatedValues2[i] = Math.min(target, animatedValues2[i] + Math.max(0.1f, target / 30));
                                    allDone = false;
                                }
                            }
                        }
                        
                        repaint();
                        if (allDone) {
                            animTimer.stop();
                        }
                    });
                    
                    // Start animation when panel becomes visible
                    addHierarchyListener(e -> {
                        if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                            if (isShowing()) {
                                for (int i = 0; i < animatedValues1.length; i++) {
                                    animatedValues1[i] = 0;
                                }
                                if (animatedValues2 != null) {
                                    for (int i = 0; i < animatedValues2.length; i++) {
                                        animatedValues2[i] = 0;
                                    }
                                }
                                animTimer.start();
                            } else {
                                animTimer.stop();
                            }
                        }
                    });
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Draw background
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRoundRect(0, 0, width, height, CARD_RADIUS, CARD_RADIUS);
                
                // Draw title
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(CARD_TITLE_FONT);
                FontMetrics titleMetrics = g2d.getFontMetrics();
                int titleWidth = titleMetrics.stringWidth(title);
                g2d.drawString(title, (width - titleWidth) / 2, 25);
                
                if (animatedValues1 == null || labels == null || labels.length == 0) {
                    g2d.setFont(VALUE_FONT);
                    g2d.drawString("No data available", width / 2 - 50, height / 2);
                    return;
                }
                
                int centerX = width / 2;
                int centerY = height / 2;
                int radius = Math.min(width, height) / 3;
                
                // Find max value for scaling
                float maxValue = 0;
                for (float value : animatedValues1) {
                    maxValue = Math.max(maxValue, value);
                }
                
                if (animatedValues2 != null) {
                    for (float value : animatedValues2) {
                        maxValue = Math.max(maxValue, value);
                    }
                }
                
                // Add 10% padding to max value
                maxValue = maxValue * 1.1f;
                
                // Draw radar grid
                g2d.setColor(new Color(80, 80, 100, 100));
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
                
                // Draw concentric circles
                int numCircles = 5;
                for (int i = 1; i <= numCircles; i++) {
                    int circleRadius = radius * i / numCircles;
                    g2d.drawOval(centerX - circleRadius, centerY - circleRadius, circleRadius * 2, circleRadius * 2);
                }
                
                // Draw axis lines
                int numAxes = labels.length;
                for (int i = 0; i < numAxes; i++) {
                    double angle = Math.toRadians(360.0 * i / numAxes - 90);
                    int x = (int) (centerX + radius * Math.cos(angle));
                    int y = (int) (centerY + radius * Math.sin(angle));
                    g2d.drawLine(centerX, centerY, x, y);
                    
                    // Draw axis labels
                    String label = labels[i];
                    if (label.length() > 15) {
                        label = label.substring(0, 12) + "...";
                    }
                    
                    FontMetrics fm = g2d.getFontMetrics();
                    int labelWidth = fm.stringWidth(label);
                    int labelHeight = fm.getHeight();
                    
                    // Position label outside the radar
                    int labelX = (int) (centerX + (radius + 20) * Math.cos(angle)) - labelWidth / 2;
                    int labelY = (int) (centerY + (radius + 20) * Math.sin(angle)) + labelHeight / 4;
                    
                    g2d.setColor(TEXT_COLOR);
                    g2d.drawString(label, labelX, labelY);
                }
                
                // Draw data polygons
                if (animatedValues1.length > 0) {
                    // First series
                    drawRadarPolygon(g2d, centerX, centerY, radius, numAxes, animatedValues1, maxValue, CHART_COLORS[0]);
                    
                    // Second series if present
                    if (animatedValues2 != null && animatedValues2.length > 0) {
                        drawRadarPolygon(g2d, centerX, centerY, radius, numAxes, animatedValues2, maxValue, CHART_COLORS[1]);
                    }
                }
                
                // Draw legend if we have two series
                if (animatedValues2 != null) {
                    int legendY = 40;
                    int legendX = width - 150;
                    
                    g2d.setColor(CHART_COLORS[0]);
                    g2d.fillRoundRect(legendX, legendY, 15, 15, 4, 4);
                    g2d.setColor(TEXT_COLOR);
                    g2d.drawString("Hours", legendX + 20, legendY + 12);
                    
                    g2d.setColor(CHART_COLORS[1]);
                    g2d.fillRoundRect(legendX, legendY + 20, 15, 15, 4, 4);
                    g2d.setColor(TEXT_COLOR);
                    g2d.drawString("Miles", legendX + 20, legendY + 32);
                }
                
                g2d.dispose();
            }
            
            private void drawRadarPolygon(Graphics2D g2d, int centerX, int centerY, int radius, int numAxes, float[] values, float maxValue, Color color) {
                // Create points for the polygon
                int[] xPoints = new int[numAxes];
                int[] yPoints = new int[numAxes];
                
                for (int i = 0; i < numAxes; i++) {
                    double angle = Math.toRadians(360.0 * i / numAxes - 90);
                    float value = i < values.length ? values[i] : 0;
                    float scaledRadius = radius * value / maxValue;
                    
                    xPoints[i] = (int) (centerX + scaledRadius * Math.cos(angle));
                    yPoints[i] = (int) (centerY + scaledRadius * Math.sin(angle));
                }
                
                // Draw filled polygon with transparency
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
                g2d.fillPolygon(xPoints, yPoints, numAxes);
                
                // Draw polygon outline
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawPolygon(xPoints, yPoints, numAxes);
                
                // Draw points
                for (int i = 0; i < numAxes; i++) {
                    g2d.setColor(TEXT_COLOR);
                    g2d.fillOval(xPoints[i] - 4, yPoints[i] - 4, 8, 8);
                    g2d.setColor(color);
                    g2d.fillOval(xPoints[i] - 2, yPoints[i] - 2, 4, 4);
                }
            }
        };
        
        chartPanel.setPreferredSize(new Dimension(400, 300));

        return chartPanel;
    }

    private javax.swing.border.Border createPanelBorder(String title) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
                        title,
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        CARD_TITLE_FONT,
                        TEXT_COLOR
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(VALUE_FONT);
        spinner.getEditor().setBackground(CARD_BACKGROUND);
        spinner.getEditor().getComponent(0).setForeground(TEXT_COLOR);
        spinner.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true));
        
        // Style the text field inside the spinner
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor) editor;
            defaultEditor.getTextField().setBackground(CARD_BACKGROUND);
            defaultEditor.getTextField().setForeground(TEXT_COLOR);
            defaultEditor.getTextField().setCaretColor(TEXT_COLOR);
        }
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setForeground(new Color(255, 255, 255)); // Bright white for better visibility
        comboBox.setBackground(new Color(45, 35, 95)); // Slightly lighter background for contrast
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 70, 120), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        // Style the dropdown
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(ACCENT_COLOR);
                    c.setForeground(TEXT_COLOR);
                } else {
                    c.setBackground(CARD_BACKGROUND);
                    c.setForeground(new Color(255, 255, 255)); // Bright white for dropdown items
                }
                return c;
            }
        });
    }

    // Update the styleTable method to create a more professional and attractive table appearance
private void styleTable(JTable table) {
    // Set font and row height for better readability
    table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    table.setRowHeight(40);
    
    // Create custom cell renderer for all data cells
    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            
            // Set alignment based on column content type
            // Check if column is numeric or has a numeric-style name
if (value instanceof Number) {
    label.setHorizontalAlignment(JLabel.CENTER);
} else if (column == 0) {
    // First column (usually ID, Rank, etc.)
    label.setHorizontalAlignment(JLabel.CENTER);
} else {
    // Check column name if available
    final String columnName;
    try {
        columnName = table.getColumnName(column);
        
        if (columnName != null) {
            if (columnName.contains("Total") || 
                columnName.contains("Hours") || 
                columnName.contains("Miles") || 
                columnName.contains("%") || 
                columnName.equals("Reservations") ||
                columnName.equals("Rank") ||
                columnName.equals("ID")) {
                label.setHorizontalAlignment(JLabel.CENTER);
            } else {
                label.setHorizontalAlignment(JLabel.LEFT);
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            }
        } else {
            label.setHorizontalAlignment(JLabel.LEFT);
        }
    } catch (Exception e) {
        // If we can't get the column name, use default left alignment
        label.setHorizontalAlignment(JLabel.LEFT);
    }
}
            
            // Format numbers with commas and proper decimal places
            if (value instanceof Number) {
                if (value instanceof Double || value instanceof Float) {
                    double doubleValue = ((Number) value).doubleValue();
                    if (doubleValue == (int) doubleValue) {
                        label.setText(String.format("%,d", (int) doubleValue));
                    } else {
                        label.setText(String.format("%,.1f", doubleValue));
                    }
                } else {
                    label.setText(String.format("%,d", ((Number) value).intValue()));
                }
            }
            
            // Style status cells with colored indicators
            try {
    final String statusColumnName = table.getColumnName(column);
    if (statusColumnName != null && statusColumnName.equals("Status") && value != null) {
        String status = value.toString();
        label.setHorizontalAlignment(JLabel.CENTER);
        
        // Create a panel with status indicator
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(true);
        
        // Set background based on selection state and row parity
        if (isSelected) {
            statusPanel.setBackground(ACCENT_COLOR);
        } else {
            statusPanel.setBackground(row % 2 == 0 ? new Color(32, 32, 48) : new Color(27, 27, 40));
        }
        
        // Create status label with colored indicator
        JLabel statusLabel = new JLabel(status);
        statusLabel.setForeground(isSelected ? Color.WHITE : getStatusColor(status));
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Add a colored dot before the status text
        statusLabel.setIcon(createStatusIcon(status, 8));
        statusLabel.setIconTextGap(8);
        
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        return statusPanel;
    }
} catch (Exception e) {
    // If we can't get the column name, just continue with normal rendering
}

// Style role cells with colored badges
try {
    final String roleColumnName = table.getColumnName(column);
    if (roleColumnName != null && roleColumnName.equals("Role") && value != null) {
        final String role = value.toString();
        
        // Create a panel with role badge
        JPanel rolePanel = new JPanel(new BorderLayout());
        rolePanel.setOpaque(true);
        
        // Set background based on selection state and row parity
        if (isSelected) {
            rolePanel.setBackground(ACCENT_COLOR);
        } else {
            rolePanel.setBackground(row % 2 == 0 ? new Color(32, 32, 48) : new Color(27, 27, 40));
        }
        
        // Create role badge
        JPanel badgePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle with role-specific color
                g2d.setColor(getRoleColor(role));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Draw role text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(role);
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(role, x, y);
                
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                FontMetrics fm = getFontMetrics(new Font("Segoe UI", Font.BOLD, 11));
                int textWidth = fm.stringWidth(role);
                return new Dimension(textWidth + 20, 22);
            }
        };
        badgePanel.setOpaque(false);
        
        // Center the badge in the cell
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        centerPanel.add(badgePanel);
        rolePanel.add(centerPanel, BorderLayout.CENTER);
        
        return rolePanel;
    }
} catch (Exception e) {
    // If we can't get the column name, just continue with normal rendering
}

// Add visual indicators for numeric values
if (value instanceof Number && !(value instanceof Integer && ((Integer)value) == 0)) {
    try {
        final String metricColumnName = table.getColumnName(column);
        
        // For reservation counts, hours, miles, etc.
        if (metricColumnName != null && (
            metricColumnName.equals("Total Reservations") || 
            metricColumnName.equals("Reservations") ||
            metricColumnName.equals("Total Hours") ||
            metricColumnName.equals("Total Miles"))) {
            
            // Create a panel with value and indicator
            JPanel valuePanel = new JPanel(new BorderLayout());
            valuePanel.setOpaque(true);
            
            // Set background based on selection state and row parity
            if (isSelected) {
                valuePanel.setBackground(ACCENT_COLOR);
            } else {
                valuePanel.setBackground(row % 2 == 0 ? new Color(32, 32, 48) : new Color(27, 27, 40));
            }
            
            // Create value label with indicator
            JLabel valueLabel = new JLabel(label.getText());
            valueLabel.setForeground(isSelected ? Color.WHITE : TEXT_COLOR);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            valueLabel.setHorizontalAlignment(JLabel.CENTER);
            
            // Add a small bar indicator based on value
            final double numValue = ((Number)value).doubleValue();
            final String finalMetricColumnName = metricColumnName;
            
            JPanel indicatorPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Determine color based on column
                    Color indicatorColor;
                    if (finalMetricColumnName.equals("Total Reservations") || 
                        finalMetricColumnName.equals("Reservations")) {
                        indicatorColor = CHART_COLORS[0];
                    } else if (finalMetricColumnName.equals("Total Hours")) {
                        indicatorColor = CHART_COLORS[1];
                    } else {
                        indicatorColor = CHART_COLORS[2];
                    }
                    
                    // Draw indicator bar
                    int barHeight = 3;
                    int barWidth = (int)(getWidth() * 0.7);
                    int x = (getWidth() - barWidth) / 2;
                    int y = getHeight() - barHeight - 2;
                    
                    // Background
                    g2d.setColor(new Color(60, 60, 80));
                    g2d.fillRoundRect(x, y, barWidth, barHeight, 2, 2);
                    
                    // Foreground - scale based on value relative to max in column
                    int maxValue = 1;
                    for (int i = 0; i < table.getRowCount(); i++) {
                        Object cellValue = table.getValueAt(i, column);
                        if (cellValue instanceof Number) {
                            maxValue = Math.max(maxValue, ((Number)cellValue).intValue());
                        }
                    }
                    
                    int fillWidth = (int)(barWidth * (numValue / maxValue));
                    g2d.setColor(indicatorColor);
                    g2d.fillRoundRect(x, y, fillWidth, barHeight, 2, 2);
                    
                    g2d.dispose();
                }
            };
            indicatorPanel.setPreferredSize(new Dimension(10, 6));
            indicatorPanel.setOpaque(false);
            
            // Add components to panel
            valuePanel.add(valueLabel, BorderLayout.CENTER);
            valuePanel.add(indicatorPanel, BorderLayout.SOUTH);
            
            return valuePanel;
        }
        
        // For percentage values
        if (metricColumnName != null && metricColumnName.equals("% of Total")) {
            // Create a panel with percentage and progress bar
            JPanel percentPanel = new JPanel(new BorderLayout());
            percentPanel.setOpaque(true);
            
            // Set background based on selection state and row parity
            if (isSelected) {
                percentPanel.setBackground(ACCENT_COLOR);
            } else {
                percentPanel.setBackground(row % 2 == 0 ? new Color(32, 32, 48) : new Color(27, 27, 40));
            }
            
            // Parse percentage value
            String percentText = label.getText();
            final double percent = parsePercentage(percentText);
            
            // Create percentage label
            JLabel percentLabel = new JLabel(String.format("%.1f%%", percent));
            percentLabel.setForeground(isSelected ? Color.WHITE : TEXT_COLOR);
            percentLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            percentLabel.setHorizontalAlignment(JLabel.CENTER);
            
            // Create progress bar
            JPanel progressPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw progress bar
                    int barHeight = 6;
                    int barWidth = getWidth() - 10;
                    int x = 5;
                    int y = (getHeight() - barHeight) / 2;
                    
                    // Background
                    g2d.setColor(new Color(60, 60, 80));
                    g2d.fillRoundRect(x, y, barWidth, barHeight, 3, 3);
                    
                    // Foreground
                    int fillWidth = (int)(barWidth * (percent / 100.0));
                    g2d.setColor(getPercentColor(percent));
                    g2d.fillRoundRect(x, y, fillWidth, barHeight, 3, 3);
                    
                    g2d.dispose();
                }
            };
            progressPanel.setPreferredSize(new Dimension(10, 20));
            progressPanel.setOpaque(false);
            
            // Add components to panel
            percentPanel.add(percentLabel, BorderLayout.NORTH);
            percentPanel.add(progressPanel, BorderLayout.CENTER);
            
            return percentPanel;
        }
    } catch (Exception e) {
        // If we can't get the column name, just continue with normal rendering
    }
}
            
            // Set colors based on selection state and row parity
            if (isSelected) {
                label.setBackground(ACCENT_COLOR);
                label.setForeground(Color.WHITE);
            } else {
                // Alternating row colors for better readability
                label.setBackground(row % 2 == 0 ? new Color(32, 32, 48) : new Color(27, 27, 40));
                label.setForeground(Color.WHITE);
            }
            
            // Add subtle border
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(45, 45, 65)),
                label.getBorder()
            ));
            
            return label;
        }
    });
    
    // Style header
    JTableHeader header = table.getTableHeader();
    header.setDefaultRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            
            // Set header style
            label.setBackground(new Color(40, 35, 80));
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)
            ));
            
            return label;
        }
    });
    
    // Set header height
    header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
    
    // Remove grid lines for cleaner look
    table.setShowGrid(false);
    table.setIntercellSpacing(new Dimension(0, 0));
    
    // Style table scrollbars
    JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, table);
    if (scrollPane != null) {
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI(ACCENT_COLOR));
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI(ACCENT_COLOR));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(CARD_BACKGROUND);
        
        // Add corner decoration
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, createCornerComponent());
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, createCornerComponent());
        scrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, createCornerComponent());
        scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, createCornerComponent());
    }
    
    // Set selection mode
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    // Add hover effect
    table.addMouseMotionListener(new MouseMotionAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            if (row >= 0 && !table.isRowSelected(row)) {
                table.repaint();
            }
        }
    });
}

// Add these missing helper methods after the styleTable method:

// Helper method to create a colored status icon
private Icon createStatusIcon(String status, int size) {
    return new Icon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getStatusColor(status));
            g2d.fillOval(x, y, size, size);
            g2d.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    };
}

// Helper method to get color based on status
private Color getStatusColor(String status) {
    if (status == null) return Color.GRAY;
    
    switch (status.toUpperCase()) {
        case "COMPLETED":
            return new Color(46, 204, 113); // Green
        case "APPROVED":
            return new Color(52, 152, 219); // Blue
        case "PENDING":
            return new Color(241, 196, 15); // Yellow
        case "REJECTED":
            return new Color(231, 76, 60);  // Red
        case "CANCELLED":
            return new Color(149, 165, 166); // Gray
        default:
            return Color.WHITE;
    }
}

// Helper method to get color based on role
private Color getRoleColor(String role) {
    if (role == null) return new Color(149, 165, 166); // Default gray
    
    switch (role.toUpperCase()) {
        case "ADMIN":
            return new Color(231, 76, 60);  // Red
        case "USER":
            return new Color(52, 152, 219); // Blue
        case "STUDENT":
            return new Color(46, 204, 113); // Green
        case "FACULTY":
            return new Color(155, 89, 182); // Purple
        case "STAFF":
            return new Color(241, 196, 15); // Yellow
        default:
            return new Color(149, 165, 166); // Gray
    }
}

// Helper method to get color based on percentage
private Color getPercentColor(double percent) {
    if (percent >= 75) {
        return new Color(46, 204, 113); // Green for high percentage
    } else if (percent >= 50) {
        return new Color(52, 152, 219); // Blue for medium percentage
    } else if (percent >= 25) {
        return new Color(241, 196, 15); // Yellow for low-medium percentage
    } else {
        return new Color(231, 76, 60);  // Red for low percentage
    }
}

// Helper method to parse percentage text
private double parsePercentage(String percentText) {
    double percent = 0;
    try {
        percent = Double.parseDouble(percentText.replace("%", "").trim());
    } catch (NumberFormatException e) {
        // Use default 0
    }
    return percent;
}

// Helper method to create corner component for scroll pane
private Component createCornerComponent() {
    JPanel corner = new JPanel();
    corner.setBackground(new Color(40, 35, 80));
    return corner;
}

// Helper method to escape CSV values
private String escapeCSV(String value) {
    if (value == null) return "";
    
    boolean needsQuotes = value.contains(",") || value.contains("\"") || value.contains("\n");
    if (needsQuotes) {
        // Escape quotes by doubling them
        String escapedValue = value.replace("\"", "\"\"");
        return "\"" + escapedValue + "\"";
    }
    return value;
}

// Add this method to enhance data panels with cards and summary metrics
private JPanel enhanceDataPanel(JPanel dataPanel, String title, Map<String, Object> summaryMetrics) {
    // Create enhanced panel with card layout
    JPanel enhancedPanel = new JPanel(new BorderLayout(15, 15));
    enhancedPanel.setOpaque(false);
    enhancedPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    // Create summary metrics panel
    if (summaryMetrics != null && !summaryMetrics.isEmpty()) {
        JPanel metricsPanel = new JPanel(new GridLayout(1, summaryMetrics.size(), 10, 0));
        metricsPanel.setOpaque(false);
        
        for (Map.Entry<String, Object> entry : summaryMetrics.entrySet()) {
            String metricName = entry.getKey();
            Object metricValue = entry.getValue();
            
            // Create metric card
            JPanel metricCard = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw card background with gradient
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(40, 35, 80),
                        0, getHeight(), new Color(30, 25, 60)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    
                    // Add subtle highlight
                    g2d.setColor(new Color(255, 255, 255, 20));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight()/3, 15, 15);
                    
                    g2d.dispose();
                }
            };
            metricCard.setOpaque(false);
            metricCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Create metric value label
            JLabel valueLabel = new JLabel(metricValue.toString());
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            valueLabel.setForeground(Color.WHITE);
            valueLabel.setHorizontalAlignment(JLabel.CENTER);
            
            // Create metric name label
            JLabel nameLabel = new JLabel(metricName);
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            nameLabel.setForeground(new Color(200, 200, 220));
            nameLabel.setHorizontalAlignment(JLabel.CENTER);
            
            // Add to card
            metricCard.add(valueLabel, BorderLayout.CENTER);
            metricCard.add(nameLabel, BorderLayout.SOUTH);
            
            // Add to metrics panel
            metricsPanel.add(metricCard);
        }
        
        // Add metrics panel to enhanced panel
        enhancedPanel.add(metricsPanel, BorderLayout.NORTH);
    }
    
    // Add data panel to enhanced panel
    enhancedPanel.add(dataPanel, BorderLayout.CENTER);
    
    return enhancedPanel;
}

// Update the createStyledButton method for more professional buttons
private JButton createStyledButton(String text, Color color) {
    JButton button = new JButton(text) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw rounded rectangle background with gradient
            if (!isEnabled()) {
                g2d.setColor(new Color(80, 80, 100, 150));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            } else if (getModel().isPressed()) {
                // Pressed state - darker gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, color.darker(),
                    0, getHeight(), color.darker().darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            } else if (getModel().isRollover()) {
                // Hover state - brighter gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, color.brighter(),
                    0, getHeight(), color
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            } else {
                // Normal state - standard gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, color,
                    0, getHeight(), color.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
            
            // Add subtle highlight at top
            if (isEnabled() && !getModel().isPressed()) {
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() / 3, 18, 18);
            }
            
            // Draw text with shadow effect
            if (isEnabled()) {
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
    
    button.setFont(new Font("Segoe UI", Font.BOLD, 14));
    button.setForeground(Color.WHITE);
    button.setBackground(color);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setPreferredSize(new Dimension(150, 40));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    // Add hover effect
    button.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            button.repaint();
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            button.repaint();
        }
        
    });
    
    return button;
}

// Add ModernScrollBarUI class
private class ModernScrollBarUI extends BasicScrollBarUI {
    private Color thumbColor;
    
    public ModernScrollBarUI(Color thumbColor) {
        this.thumbColor = thumbColor;
    }
    
    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = thumbColor;
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
        
        // Draw rounded rectangle thumb
        g2d.setColor(thumbColor);
        g2d.fillRoundRect(thumbBounds.x, thumbBounds.y, 
                thumbBounds.width, thumbBounds.height, 10, 10);
        
        g2d.dispose();
    }
    
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(CARD_BACKGROUND);
        g2d.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        g2d.dispose();
    }
}

// Fix the showExportSuccessMessage method to address syntax errors

private void showExportSuccessMessage(final String filename) {
    // Create success panel with gradient background
    JPanel messagePanel = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Create gradient background
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(46, 204, 113),
                getWidth(), 0, new Color(26, 188, 156)
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        }
    };
    messagePanel.setOpaque(false);
    messagePanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
    
    // Create success icon
    JLabel iconLabel = new JLabel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw checkmark
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            
            int[] xPoints = {5, 10, 18};
            int[] yPoints = {12, 18, 6};
            g2d.drawPolyline(xPoints, yPoints, 3);
        }
    };
    iconLabel.setPreferredSize(new Dimension(24, 24));
    
    // Create message label
    JLabel messageLabel = new JLabel("Report successfully exported to " + filename);
    messageLabel.setForeground(Color.WHITE);
    messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    
    // Add components to panel
    messagePanel.add(iconLabel, BorderLayout.WEST);
    messagePanel.add(messageLabel, BorderLayout.CENTER);
    
    // Show message as popup
    final JDialog messageDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "", false);
    messageDialog.setUndecorated(true);
    messageDialog.setContentPane(messagePanel);
    messageDialog.pack();
    
    // Add drop shadow
    messageDialog.getRootPane().setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 1),
        BorderFactory.createEmptyBorder(4, 4, 4, 4)
    ));
    
    // Position at bottom right of parent frame
    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
    int x = parentFrame.getX() + parentFrame.getWidth() - messageDialog.getWidth() - 20;
    int y = parentFrame.getY() + parentFrame.getHeight() - messageDialog.getHeight() - 20;
    messageDialog.setLocation(x, y);
    
    // Create a shared opacity array for animations
    final float[] opacity = {0.0f};
    
    // Initialize all timers first
    final Timer fadeInTimer = new Timer(10, null);
    final Timer closeTimer = new Timer(3000, null);
    final Timer fadeOutTimer = new Timer(10, null);
    
    // Create fade out action
    ActionListener fadeOutAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            opacity[0] -= 0.05f;
            if (opacity[0] <= 0.0f) {
                opacity[0] = 0.0f;
                fadeOutTimer.stop();
                messageDialog.dispose();
            } else {
                messageDialog.setOpacity(opacity[0]);
            }
        }
    };
    fadeOutTimer.addActionListener(fadeOutAction);
    
    // Create close action
    ActionListener closeAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            fadeOutTimer.start();
        }
    };
    closeTimer.addActionListener(closeAction);
    closeTimer.setRepeats(false);
    
    // Create fade in action
    ActionListener fadeInAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            opacity[0] += 0.05f;
            if (opacity[0] >= 1.0f) {
                opacity[0] = 1.0f;
                fadeInTimer.stop();
                closeTimer.start();
            } else {
                messageDialog.setOpacity(opacity[0]);
            }
        }
    };
    fadeInTimer.addActionListener(fadeInAction);
    
    // Start the animation sequence
    messageDialog.setOpacity(0.0f);
    messageDialog.setVisible(true);
    fadeInTimer.start();
}

// Add this method to export JTable data to CSV file
private void exportToCSV(JTable table, String reportName) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save CSV File");
    fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
    
    // Suggest a filename based on the report type
    fileChooser.setSelectedFile(new File(reportName + ".csv"));
    
    int userSelection = fileChooser.showSaveDialog(this);
    
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();
        
        // Ensure the file has a .csv extension
        if (!filePath.toLowerCase().endsWith(".csv")) {
            filePath += ".csv";
            fileToSave = new File(filePath);
        }
        
        try (PrintWriter pw = new PrintWriter(new File(filePath))) {
            StringBuilder sb = new StringBuilder();
            
            // Write column names
            for (int i = 0; i < table.getColumnCount(); i++) {
                sb.append(escapeCSV(table.getColumnName(i)));
                if (i < table.getColumnCount() - 1) {
                    sb.append(',');
                }
            }
            sb.append('\n');
            
          
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    Object cellValue = table.getValueAt(i, j);
                    String value = (cellValue != null) ? cellValue.toString() : "";
                    sb.append(escapeCSV(value));
                    if (j < table.getColumnCount() - 1) {
                        sb.append(',');
                    }
                }
                sb.append('\n');
            }
            
            pw.write(sb.toString());
            
          
            showExportSuccessMessage(fileToSave.getName());
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
}
