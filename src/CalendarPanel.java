import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import model.Reservation;
import model.User;
import model.Vehicle;

/**
 * Calendar panel for displaying and managing vehicle reservations.
 * Provides a clean, formal interface for viewing and interacting with reservation data.
 */
public class CalendarPanel extends JPanel {
    // UI Components
    private JLabel monthYearLabel;
    private JPanel calendarGrid;
    private JComboBox<String> vehicleFilter;
    private JComboBox<String> statusFilter;
    private JButton todayButton;
    private JButton prevMonthButton;
    private JButton nextMonthButton;
    private JButton addReservationButton;
    private JButton weekViewButton;
    private JButton monthViewButton;
    private JButton yearViewButton;
    
    // State variables
    private LocalDate currentDate;
    private LocalDate selectedDate;
    private String currentView = "month"; // "month", "week", "year"
    private String selectedVehicleId = null;
    private Reservation.ReservationStatus selectedStatus = null;
    
    // Color scheme - more formal, corporate colors
    private Color primaryColor = new Color(16, 24, 50); // Dark navy blue
    private Color secondaryColor = new Color(24, 36, 75); // Slightly lighter navy
    private Color accentColor = new Color(64, 158, 255); // Bright blue
    private Color accentColorAlt = new Color(255, 64, 192); // Pink
    private Color highlightColor = new Color(239, 246, 255); // Very light blue
    private Color textColor = new Color(229, 231, 235); // Light gray
    private Color mutedTextColor = new Color(156, 163, 175); // Medium gray
    private Color borderColor = new Color(55, 65, 81); // Dark gray
    
    // Status colors - more formal, less vibrant
    private Map<Reservation.ReservationStatus, Color> statusColors = new HashMap<>();
    
    // References
    private MainFrameInterface mainFrame;
    private DataManager dataManager;
    
    /**
     * Constructs a new CalendarPanel.
     * 
     * @param mainFrame The main frame interface
     */
    public CalendarPanel(MainFrameInterface mainFrame) {
        this.mainFrame = mainFrame;
        this.dataManager = DataManager.getInstance();
        this.currentDate = LocalDate.now();
        this.selectedDate = currentDate;
        
        setupStatusColors();
        initializeUI();
        refreshCalendar();
    }
    
    /**
     * Sets up the status color scheme.
     */
    private void setupStatusColors() {
        statusColors.put(Reservation.ReservationStatus.APPROVED, new Color(34, 197, 94)); // Professional green
        statusColors.put(Reservation.ReservationStatus.PENDING, new Color(234, 179, 8));  // Muted yellow
        statusColors.put(Reservation.ReservationStatus.REJECTED, new Color(239, 68, 68)); // Subdued red
        statusColors.put(Reservation.ReservationStatus.CANCELLED, new Color(239, 68, 68)); // Subdued red
        statusColors.put(Reservation.ReservationStatus.COMPLETED, new Color(59, 130, 246)); // Professional blue
    }
    
    /**
     * Initializes the user interface components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(primaryColor);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel with title and controls
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Calendar grid
        JPanel calendarContainer = new JPanel(new BorderLayout());
        calendarContainer.setBackground(primaryColor);
        calendarContainer.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        calendarGrid = new JPanel();
        calendarGrid.setLayout(new GridLayout(0, 7, 1, 1));
        calendarGrid.setBackground(primaryColor);
        
        // Wrap calendar grid in a scroll pane
        JScrollPane scrollPane = new JScrollPane(calendarGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(primaryColor);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Customize scrollbar
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        
        calendarContainer.add(scrollPane, BorderLayout.CENTER);
        add(calendarContainer, BorderLayout.CENTER);
        
        // Bottom panel with view controls
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the top panel with title, navigation, and filters.
     * 
     * @return The configured top panel
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBackground(primaryColor);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Title and navigation
        JPanel titleNavPanel = new JPanel(new BorderLayout(20, 0));
        titleNavPanel.setBackground(primaryColor);
        
        // Title
        JLabel titleLabel = new JLabel("Reservation Calendar");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textColor);
        titleNavPanel.add(titleLabel, BorderLayout.WEST);
        
        // Month/Year display and navigation
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        navigationPanel.setBackground(primaryColor);
        
        // Previous month button - more visible
        prevMonthButton = createNavigationButton("◀");
        prevMonthButton.addActionListener(e -> changeMonth(-1));
        
        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        monthYearLabel.setForeground(textColor);
        monthYearLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        
        // Next month button - more visible
        nextMonthButton = createNavigationButton("▶");
        nextMonthButton.addActionListener(e -> changeMonth(1));
        
        navigationPanel.add(prevMonthButton);
        navigationPanel.add(monthYearLabel);
        navigationPanel.add(nextMonthButton);
        
        titleNavPanel.add(navigationPanel, BorderLayout.CENTER);
        
        // Today button - more visible and attractive
        JPanel todayPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        todayPanel.setBackground(primaryColor);
        
        todayButton = createGradientButton("Today", accentColor, new Color(64, 169, 255));
        todayButton.setIcon(createTodayIcon());
        todayButton.addActionListener(e -> goToToday());
        todayPanel.add(todayButton);
        
        titleNavPanel.add(todayPanel, BorderLayout.EAST);
        
        panel.add(titleNavPanel, BorderLayout.NORTH);
        
        // Filters
        JPanel filtersPanel = new JPanel();
        filtersPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filtersPanel.setBackground(primaryColor);
        filtersPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel vehicleLabel = new JLabel("Vehicle:");
        vehicleLabel.setForeground(textColor);
        vehicleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        vehicleFilter = createStyledComboBox(getVehicleOptions());
        vehicleFilter.addActionListener(e -> {
            String selected = (String) vehicleFilter.getSelectedItem();
            if (selected.equals("All Vehicles")) {
                selectedVehicleId = null;
            } else {
                // Extract vehicle ID from the selected item
                for (Vehicle vehicle : dataManager.getAllVehicles()) {
                    String displayName = vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getLicensePlate() + ")";
                    if (displayName.equals(selected)) {
                        selectedVehicleId = vehicle.getVehicleId();
                        break;
                    }
                }
            }
            refreshCalendar();
        });
        
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setForeground(textColor);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        statusFilter = createStyledComboBox(new String[]{"All", "Approved", "Pending", "Cancelled", "Completed", "Rejected"});
        statusFilter.addActionListener(e -> {
            String selected = (String) statusFilter.getSelectedItem();
            if (selected.equals("All")) {
                selectedStatus = null;
            } else {
                selectedStatus = Reservation.ReservationStatus.valueOf(selected.toUpperCase());
            }
            refreshCalendar();
        });
        
        filtersPanel.add(vehicleLabel);
        filtersPanel.add(vehicleFilter);
        filtersPanel.add(Box.createHorizontalStrut(10));
        filtersPanel.add(statusLabel);
        filtersPanel.add(statusFilter);
        
        panel.add(filtersPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the bottom panel with view controls and action buttons.
     * 
     * @return The configured bottom panel
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(primaryColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // View controls - more visible
        JPanel viewControlsPanel = new JPanel();
        viewControlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        viewControlsPanel.setBackground(primaryColor);
        
        // Create a rounded panel for the view buttons
        JPanel viewButtonsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(40, 50, 95)); // Slightly lighter than secondary color
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2d.dispose();
            }
        };
        viewButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        viewButtonsPanel.setOpaque(false);
        
        weekViewButton = createViewButton("Week", "week");
        monthViewButton = createViewButton("Month", "month");
        yearViewButton = createViewButton("Year", "year");
        
        // Set initial selection
        monthViewButton.setBackground(accentColor);
        monthViewButton.setForeground(Color.WHITE);
        
        viewButtonsPanel.add(weekViewButton);
        viewButtonsPanel.add(monthViewButton);
        viewButtonsPanel.add(yearViewButton);
        
        viewControlsPanel.add(viewButtonsPanel);
        
        panel.add(viewControlsPanel, BorderLayout.WEST);
        
        // Add reservation button - more visible and attractive
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(primaryColor);
        
        addReservationButton = createGradientButton("Add Reservation", accentColorAlt, new Color(255, 105, 180));
        addReservationButton.setIcon(createAddIcon());
        addReservationButton.addActionListener(e -> addNewReservation());
        
        actionPanel.add(addReservationButton);
        
        panel.add(actionPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Refreshes the calendar display with current data.
     */
    public void refreshCalendar() {
        calendarGrid.removeAll();
        
        // Update month/year label
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        monthYearLabel.setText(currentDate.format(formatter));
        
        // Add day headers
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String dayName : dayNames) {
            JPanel headerPanel = new JPanel();
            headerPanel.setLayout(new BorderLayout());
            headerPanel.setBackground(secondaryColor);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            
            JLabel label = new JLabel(dayName, SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setForeground(textColor);
            headerPanel.add(label, BorderLayout.CENTER);
            
            calendarGrid.add(headerPanel);
        }
        
        // Get first day of month
        LocalDate firstOfMonth = currentDate.withDayOfMonth(1);
        
        // Get day of week (1 = Monday, 7 = Sunday)
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        
        // Add empty cells for days before first of month
        for (int i = 1; i < dayOfWeek; i++) {
            calendarGrid.add(createEmptyCell());
        }
        
        // Add cells for each day of the month
        int daysInMonth = currentDate.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentDate.withDayOfMonth(day);
            calendarGrid.add(createDayCell(date));
        }
        
        // Add empty cells to complete the grid
        int remainingCells = 7 - (dayOfWeek + daysInMonth - 1) % 7;
        if (remainingCells < 7) {
            for (int i = 0; i < remainingCells; i++) {
                calendarGrid.add(createEmptyCell());
            }
        }
        
        revalidate();
        repaint();
    }
    
    /**
     * Creates an empty cell for the calendar grid.
     * 
     * @return The empty cell panel
     */
    private JPanel createEmptyCell() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(), 50));
        return panel;
    }
    
    /**
     * Creates a day cell for the calendar grid.
     * 
     * @param date The date for this cell
     * @return The configured day cell panel
     */
    private JPanel createDayCell(LocalDate date) {
        boolean isToday = date.equals(LocalDate.now());
        boolean isSelected = date.equals(selectedDate);
        
        // Create the cell panel with a clean, formal design
        JPanel cellPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fill background
                if (isSelected) {
                    g2d.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                } else if (isToday) {
                    g2d.setColor(new Color(accentColorAlt.getRed(), accentColorAlt.getGreen(), accentColorAlt.getBlue(), 20));
                } else {
                    g2d.setColor(secondaryColor);
                }
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add subtle border
                g2d.setColor(borderColor);
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                
                g2d.dispose();
            }
        };
        
        cellPanel.setLayout(new BorderLayout());
        cellPanel.setBackground(secondaryColor);
        cellPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Day number panel
        JPanel dayNumberPanel = new JPanel(new BorderLayout());
        dayNumberPanel.setOpaque(false);
        dayNumberPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        JLabel dayLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        if (isToday) {
            // Today indicator - more visible circle with number
            JPanel todayIndicator = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Create gradient for today indicator
                    GradientPaint gradient = new GradientPaint(
                        0, 0, 
                        accentColorAlt, 
                        getWidth(), getHeight(), 
                        new Color(accentColorAlt.getRed(), accentColorAlt.getGreen() + 40, accentColorAlt.getBlue())
                    );
                    g2d.setPaint(gradient);
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                    
                    // Add highlight
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(2, 2, getWidth() - 4, getHeight() / 2 - 2);
                    
                    g2d.dispose();
                }
                
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(28, 28);
                }
            };
            todayIndicator.setOpaque(false);
            
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            wrapper.setOpaque(false);
            wrapper.add(todayIndicator);
            
            dayLabel.setForeground(Color.WHITE);
            dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
            todayIndicator.setLayout(new BorderLayout());
            todayIndicator.add(dayLabel);
            
            dayNumberPanel.add(wrapper, BorderLayout.WEST);
        } else {
            dayLabel.setForeground(textColor);
            dayNumberPanel.add(dayLabel, BorderLayout.WEST);
        }
        
        cellPanel.add(dayNumberPanel, BorderLayout.NORTH);
        
        // Reservations panel
        JPanel reservationsPanel = new JPanel();
        reservationsPanel.setLayout(new BoxLayout(reservationsPanel, BoxLayout.Y_AXIS));
        reservationsPanel.setOpaque(false);
        
        // Get reservations for this day
        List<Reservation> reservations = getReservationsForDate(date);
        
        if (reservations.isEmpty()) {
            reservationsPanel.add(Box.createVerticalStrut(10));
        } else {
            for (Reservation reservation : reservations) {
                JPanel reservationItem = createReservationItem(reservation);
                reservationsPanel.add(reservationItem);
                reservationsPanel.add(Box.createVerticalStrut(3));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(reservationsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Custom scrollbar
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(3, 0));
        
        cellPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add mouse listener for selection
        cellPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedDate = date;
                refreshCalendar();
                
                if (e.getClickCount() == 2) {
                    // Double-click to add reservation
                    addNewReservation();
                }
            }
        });
        
        return cellPanel;
    }
    
    /**
     * Creates a reservation item for display in a day cell.
     * 
     * @param reservation The reservation data
     * @return The configured reservation item panel
     */
    private JPanel createReservationItem(Reservation reservation) {
        // Get vehicle and user information
        Vehicle vehicle = dataManager.getVehicleById(reservation.getVehicleId());
        User user = dataManager.getUserById(reservation.getUserId());
        
        String vehicleName = "";
        if (vehicle != null) {
            vehicleName = vehicle.getMake() + " " + vehicle.getModel();
        } else {
            vehicleName = "Unknown Vehicle";
        }
        
        String userName = "";
        if (user != null) {
            userName = user.getFullName();
        } else {
            userName = "Unknown User";
        }
        
        // Clean, formal reservation item
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 0));
        panel.setBackground(new Color(secondaryColor.getRed() + 10, secondaryColor.getGreen() + 10, secondaryColor.getBlue() + 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, statusColors.getOrDefault(reservation.getStatus(), Color.GRAY)),
            BorderFactory.createEmptyBorder(4, 5, 4, 5)
        ));
        
        String statusText = reservation.getStatus().toString();
        String tooltipText = vehicleName + " - " + userName + " (" + statusText + ")";
        panel.setToolTipText(tooltipText);
        
        // Vehicle name
        JLabel vehicleLabel = new JLabel(vehicleName);
        vehicleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        vehicleLabel.setForeground(textColor);
        
        // User name
        JLabel userLabel = new JLabel(userName);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        userLabel.setForeground(mutedTextColor);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.add(vehicleLabel);
        infoPanel.add(userLabel);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // Store the reservation ID in the panel's client properties
        panel.putClientProperty("reservationId", reservation.getReservationId());
        
        // Add mouse listener for hover effect and click
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                panel.setBackground(new Color(secondaryColor.getRed() + 20, secondaryColor.getGreen() + 20, secondaryColor.getBlue() + 20));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                panel.setBackground(new Color(secondaryColor.getRed() + 10, secondaryColor.getGreen() + 10, secondaryColor.getBlue() + 10));
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Show reservation details
                String reservationId = (String) panel.getClientProperty("reservationId");
                showReservationDetails(reservationId);
            }
        });
        
        // Add popup menu for right-click actions
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem detailsItem = new JMenuItem("View Details");
        detailsItem.addActionListener(e -> showReservationDetails(reservation.getReservationId()));
        popupMenu.add(detailsItem);

        // Add remove option for admins
        if (DataManager.getInstance().getCurrentUser() != null && 
            DataManager.getInstance().getCurrentUser().isAdmin()) {
            JMenuItem removeItem = new JMenuItem("Remove Reservation");
            removeItem.setForeground(new Color(220, 53, 69));
            removeItem.addActionListener(e -> {
                int choice = JOptionPane.showConfirmDialog(panel,
                    "Are you sure you want to permanently remove this reservation?\nThis action cannot be undone.",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                    
                if (choice == JOptionPane.YES_OPTION) {
                    try {
                        // Delete the reservation
                        dataManager.deleteReservation(reservation.getReservationId());
                        
                        // Show success message
                        JOptionPane.showMessageDialog(panel, 
                            "Reservation has been removed successfully.",
                            "Reservation Removed", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Refresh calendar
                        refreshCalendar();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel, 
                            "Error removing reservation: " + ex.getMessage(),
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            popupMenu.add(removeItem);
        }

        // Add mouse listener for right-click
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(panel, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Show reservation details
                if (SwingUtilities.isLeftMouseButton(e)) {
                    String reservationId = (String) panel.getClientProperty("reservationId");
                    showReservationDetails(reservationId);
                }
            }
        });
        
        return panel;
    }
    
    /**
     * Changes the displayed month.
     * 
     * @param delta The number of months to change by
     */
    private void changeMonth(int delta) {
        currentDate = currentDate.plusMonths(delta);
        refreshCalendar();
    }
    
    /**
     * Navigates to today's date.
     */
    private void goToToday() {
        currentDate = LocalDate.now();
        selectedDate = currentDate;
        refreshCalendar();
    }
    
    /**
     * Opens the interface to add a new reservation.
     */
    private void addNewReservation() {
        try {
            // Get a vehicle to pass to the form
            List<Vehicle> vehicles = dataManager.getAllVehicles();
            if (!vehicles.isEmpty()) {
                // Use the first available vehicle
                Vehicle vehicle = vehicles.get(0);
            
                // Open the reservation form
                JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                VehicleReservationForm reservationForm = new VehicleReservationForm(mainFrame, vehicle, this.mainFrame);
                reservationForm.setVisible(true);
            } else {
                // No vehicles available
                JOptionPane.showMessageDialog(this, 
                    "No vehicles available for reservation.",
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            // Fallback to a simple message dialog
            JOptionPane.showMessageDialog(this, 
                "Add new reservation for date: " + selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                "Add Reservation", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Shows details for a specific reservation.
     * 
     * @param reservationId The ID of the reservation to show
     */
    private void showReservationDetails(String reservationId) {
        Reservation reservation = dataManager.getReservationById(reservationId);
        if (reservation == null) {
            JOptionPane.showMessageDialog(this, 
                "Reservation not found.",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get vehicle and user information
        Vehicle vehicle = dataManager.getVehicleById(reservation.getVehicleId());
        User user = dataManager.getUserById(reservation.getUserId());
        
        // Create a formal details panel with sections
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        detailsPanel.setBackground(new Color(245, 247, 250));
        
        // Reservation header section
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(new Color(16, 24, 50));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        
        JLabel titleLabel = new JLabel("Reservation Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel idLabel = new JLabel("ID: " + reservation.getReservationId());
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        idLabel.setForeground(new Color(200, 200, 200));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(3));
        titlePanel.add(idLabel);
        
        // Status indicator
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        statusPanel.setOpaque(false);
        
        JPanel statusIndicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(statusColors.getOrDefault(reservation.getStatus(), Color.GRAY));
                g2d.fillOval(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(12, 12);
            }
        };
        
        JLabel statusValueLabel = new JLabel(reservation.getStatus().toString());
        statusValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusValueLabel.setForeground(Color.WHITE);
        
        statusPanel.add(statusIndicator);
        statusPanel.add(statusValueLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(statusPanel, BorderLayout.EAST);
        
        detailsPanel.add(headerPanel);
        detailsPanel.add(Box.createVerticalStrut(15));
        
        // Vehicle information section
        JPanel vehicleSection = createSectionPanel("Vehicle Information");
        
        if (vehicle != null) {
            addFormalDetailRow(vehicleSection, "Vehicle:", vehicle.getMake() + " " + vehicle.getModel(), true);
            addFormalDetailRow(vehicleSection, "Type:", vehicle.getType().toString(), false);
            addFormalDetailRow(vehicleSection, "License Plate:", vehicle.getLicensePlate(), true);
            // VIN is not available in the Vehicle class, so we'll skip it
            addFormalDetailRow(vehicleSection, "Capacity:", String.valueOf(vehicle.getCapacity()) + " passengers", false);
        } else {
            addFormalDetailRow(vehicleSection, "Vehicle:", "Unknown Vehicle", true);
        }
        
        detailsPanel.add(vehicleSection);
        detailsPanel.add(Box.createVerticalStrut(15));
        
        // Requester information section
        JPanel requesterSection = createSectionPanel("Requester Information");
        
        if (user != null) {
            addFormalDetailRow(requesterSection, "Reserved By:", user.getFullName(), true);
            addFormalDetailRow(requesterSection, "Email:", user.getEmail(), false);
            addFormalDetailRow(requesterSection, "Phone:", user.getPhoneNumber(), false);
            // Department and Position are not available in the User class, so we'll use notes or other fields
            addFormalDetailRow(requesterSection, "User ID:", user.getUserId(), false);
            addFormalDetailRow(requesterSection, "User Role:", user.getRole().toString(), false);
        } else {
            addFormalDetailRow(requesterSection, "Reserved By:", "Unknown User", true);
        }
        
        detailsPanel.add(requesterSection);
        detailsPanel.add(Box.createVerticalStrut(15));
        
        // Reservation details section
        JPanel reservationSection = createSectionPanel("Reservation Schedule");
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        
        String startDate = reservation.getStartDateTime().format(dateFormatter);
        String startTime = reservation.getStartDateTime().format(timeFormatter);
        String endDate = reservation.getEndDateTime().format(dateFormatter);
        String endTime = reservation.getEndDateTime().format(timeFormatter);
        
        // Calculate duration
        Duration duration = Duration.between(reservation.getStartDateTime(), reservation.getEndDateTime());
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        String durationText = hours + " hour" + (hours != 1 ? "s" : "");
        if (minutes > 0) {
            durationText += ", " + minutes + " minute" + (minutes != 1 ? "s" : "");
        }
        
        addFormalDetailRow(reservationSection, "Start Date:", startDate, true);
        addFormalDetailRow(reservationSection, "Start Time:", startTime, false);
        addFormalDetailRow(reservationSection, "End Date:", endDate, true);
        addFormalDetailRow(reservationSection, "End Time:", endTime, false);
        addFormalDetailRow(reservationSection, "Duration:", durationText, true);
        
        detailsPanel.add(reservationSection);
        detailsPanel.add(Box.createVerticalStrut(15));
        
        // Trip details section
        JPanel tripSection = createSectionPanel("Trip Details");
        
        addFormalDetailRow(tripSection, "Purpose:", reservation.getPurpose(), true);
        addFormalDetailRow(tripSection, "Destination:", reservation.getDestination(), false);
        addFormalDetailRow(tripSection, "Passengers:", String.valueOf(reservation.getEstimatedPassengers()), true);
        
        // Notes if available
        if (reservation.getNotes() != null && !reservation.getNotes().isEmpty()) {
            JPanel notesPanel = new JPanel(new BorderLayout());
            notesPanel.setBackground(new Color(245, 247, 250));
            notesPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            
            JLabel notesLabel = new JLabel("Additional Notes:");
            notesLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            notesPanel.add(notesLabel, BorderLayout.NORTH);
            
            JTextArea notesArea = new JTextArea(reservation.getNotes());
            notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            notesArea.setLineWrap(true);
            notesArea.setWrapStyleWord(true);
            notesArea.setEditable(false);
            
            notesArea.setBackground(new Color(235, 237, 240));
            notesArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            
            notesPanel.add(notesArea, BorderLayout.CENTER);
            tripSection.add(notesPanel);
        }
        
        detailsPanel.add(tripSection);
        
        // Approval details if applicable
        if (reservation.getStatus() == Reservation.ReservationStatus.APPROVED || 
            reservation.getStatus() == Reservation.ReservationStatus.COMPLETED) {
            
            detailsPanel.add(Box.createVerticalStrut(15));
            JPanel approvalSection = createSectionPanel("Approval Information");
            
            if (reservation.getApprovedBy() != null && !reservation.getApprovedBy().isEmpty()) {
                User approver = dataManager.getUserById(reservation.getApprovedBy());
                if (approver != null) {
                    addFormalDetailRow(approvalSection, "Approved By:", approver.getFullName(), true);
                    addFormalDetailRow(approvalSection, "Approver Role:", approver.getRole().toString(), false);
                } else {
                    addFormalDetailRow(approvalSection, "Approved By:", reservation.getApprovedBy(), true);
                }
            }
            
            if (reservation.getApprovalDateTime() != null) {
                String approvalDate = reservation.getApprovalDateTime().format(dateFormatter);
                String approvalTime = reservation.getApprovalDateTime().format(timeFormatter);
                addFormalDetailRow(approvalSection, "Approval Date:", approvalDate, true);
                addFormalDetailRow(approvalSection, "Approval Time:", approvalTime, false);
            }
            
            detailsPanel.add(approvalSection);
        }
        
        // Mileage details if completed
        if (reservation.getStatus() == Reservation.ReservationStatus.COMPLETED) {
            detailsPanel.add(Box.createVerticalStrut(15));
            JPanel mileageSection = createSectionPanel("Trip Metrics");
            
            double distanceTraveled = reservation.getFinalMileage() - reservation.getInitialMileage();
            
            addFormalDetailRow(mileageSection, "Initial Mileage:", String.format("%,.1f km", reservation.getInitialMileage()), true);
            addFormalDetailRow(mileageSection, "Final Mileage:", String.format("%,.1f km", reservation.getFinalMileage()), false);
            addFormalDetailRow(mileageSection, "Distance Traveled:", String.format("%,.1f km", distanceTraveled), true);
            
            detailsPanel.add(mileageSection);
        }
        
        // Add action buttons with improved styling
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(235, 237, 240));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        
        // Different buttons based on status
        if (reservation.getStatus() == Reservation.ReservationStatus.PENDING) {
            JButton approveButton = createActionButton("Approve", new Color(40, 167, 69));
            JButton rejectButton = createActionButton("Reject", new Color(220, 53, 69));
            
            buttonPanel.add(approveButton);
            buttonPanel.add(rejectButton);
            
            approveButton.addActionListener(e -> {
                // Handle approval logic
                try {
                    reservation.setStatus(Reservation.ReservationStatus.APPROVED);
                    // We can't use getCurrentUser() since it's not in MainFrameInterface
                    // Instead, we'll use a generic admin ID or the first admin we find
                    String approverId = findAdminUserId();
                    if (approverId != null) {
                        reservation.setApprovedBy(approverId);
                    }
                    reservation.setApprovalDateTime(LocalDateTime.now());
                    dataManager.updateReservation(reservation);
                    JOptionPane.showMessageDialog(this, 
                        "Reservation has been approved successfully.",
                        "Reservation Approved", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshCalendar();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error approving reservation: " + ex.getMessage(),
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(buttonPanel);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
            });
            
            rejectButton.addActionListener(e -> {
                // Handle rejection logic
                try {
                    reservation.setStatus(Reservation.ReservationStatus.REJECTED);
                    dataManager.updateReservation(reservation);
                    JOptionPane.showMessageDialog(this, 
                        "Reservation has been rejected.",
                        "Reservation Rejected", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshCalendar();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error rejecting reservation: " + ex.getMessage(),
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(buttonPanel);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
            });
        } else if (reservation.getStatus() == Reservation.ReservationStatus.APPROVED) {
            JButton cancelButton = createActionButton("Cancel", new Color(108, 117, 125));
            JButton completeButton = createActionButton("Mark Completed", new Color(0, 123, 255));
            
            buttonPanel.add(cancelButton);
            buttonPanel.add(completeButton);
            
            cancelButton.addActionListener(e -> {
                // Handle cancellation logic
                try {
                    reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
                    dataManager.updateReservation(reservation);
                    JOptionPane.showMessageDialog(this, 
                        "Reservation has been cancelled.",
                        "Reservation Cancelled", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshCalendar();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error cancelling reservation: " + ex.getMessage(),
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(buttonPanel);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
            });
            
            completeButton.addActionListener(e -> {
                // Handle completion logic
                try {
                    // Show dialog to enter final mileage
                    String finalMileageStr = JOptionPane.showInputDialog(this,
                        "Enter final mileage (km):",
                        "Complete Reservation",
                        JOptionPane.QUESTION_MESSAGE);
                    
                    if (finalMileageStr != null && !finalMileageStr.trim().isEmpty()) {
                        try {
                            double finalMileage = Double.parseDouble(finalMileageStr);
                            if (finalMileage < reservation.getInitialMileage()) {
                                JOptionPane.showMessageDialog(this,
                                    "Final mileage cannot be less than initial mileage.",
                                    "Invalid Mileage",
                                    JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            
                            reservation.setFinalMileage(finalMileage);
                            reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
                            dataManager.updateReservation(reservation);
                            JOptionPane.showMessageDialog(this, 
                                "Reservation has been marked as completed.",
                                "Reservation Completed", 
                                JOptionPane.INFORMATION_MESSAGE);
                            refreshCalendar();
                            
                            // Close the dialog
                            Window window = SwingUtilities.getWindowAncestor(buttonPanel);
                            if (window instanceof JDialog) {
                                ((JDialog) window).dispose();
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this,
                                "Please enter a valid number for mileage.",
                                "Invalid Input",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error completing reservation: " + ex.getMessage(),
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
        }
        
        // Add remove button for admins
        if (DataManager.getInstance().getCurrentUser() != null && 
            DataManager.getInstance().getCurrentUser().isAdmin()) {
            JButton removeButton = createActionButton("Remove", new Color(220, 53, 69));
            removeButton.addActionListener(e -> {
                // Handle removal logic
                int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to permanently remove this reservation?\nThis action cannot be undone.",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                    
                if (choice == JOptionPane.YES_OPTION) {
                    try {
                        // Delete the reservation
                        dataManager.deleteReservation(reservation.getReservationId());
                        
                        // Show success message
                        JOptionPane.showMessageDialog(this, 
                            "Reservation has been removed successfully.",
                            "Reservation Removed", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Close the dialog
                        Window window = SwingUtilities.getWindowAncestor(buttonPanel);
                        if (window instanceof JDialog) {
                            ((JDialog) window).dispose();
                        }
                        
                        // Refresh calendar
                        refreshCalendar();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Error removing reservation: " + ex.getMessage(),
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            buttonPanel.add(removeButton);
        }

        JButton closeButton = createActionButton("Close", new Color(108, 117, 125));
        closeButton.addActionListener(e -> {
            // Close the dialog
            Window window = SwingUtilities.getWindowAncestor(buttonPanel);
            if (window instanceof JDialog) {
                ((JDialog) window).dispose();
            }
        });
        
        buttonPanel.add(closeButton);
        
        // Create and show the dialog
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Reservation Details", true);
        dialog.setLayout(new BorderLayout());
        
        // Add a scroll pane for the details
        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Finds an admin user ID to use for approvals.
     * 
     * @return An admin user ID, or null if none found
     */
    private String findAdminUserId() {
        List<User> users = dataManager.getAllUsers();
        for (User user : users) {
            if (user.isAdmin()) {
                return user.getUserId();
            }
        }
        return null;
    }

    /**
     * Creates a section panel with a title.
     * 
     * @param title The section title
     * @return The configured section panel
     */
    private JPanel createSectionPanel(String title) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(new Color(245, 247, 250));
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionTitle.setForeground(new Color(16, 24, 50));
        sectionTitle.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(0, 0, 5, 0)
        ));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(245, 247, 250));
        titlePanel.add(sectionTitle, BorderLayout.WEST);
        
        sectionPanel.add(titlePanel);
        sectionPanel.add(Box.createVerticalStrut(8));
        
        return sectionPanel;
    }

    /**
     * Adds a formal detail row to a section panel.
     * 
     * @param panel The panel to add to
     * @param label The label text
     * @param value The value text
     * @param highlight Whether to highlight this row
     */
    private void addFormalDetailRow(JPanel panel, String label, String value, boolean highlight) {
        JPanel rowPanel = new JPanel(new BorderLayout(15, 0));
        rowPanel.setOpaque(true);
        
        if (highlight) {
            rowPanel.setBackground(new Color(240, 242, 245));
        } else {
            rowPanel.setBackground(new Color(245, 247, 250));
        }
        
        rowPanel.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelComponent.setForeground(new Color(70, 70, 70));
        labelComponent.setPreferredSize(new Dimension(120, labelComponent.getPreferredSize().height));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueComponent.setForeground(new Color(33, 37, 41));
        
        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.CENTER);
        
        panel.add(rowPanel);
    }

    /**
     * Creates an action button with custom styling.
     * 
     * @param text The button text
     * @param color The button color
     * @return The configured button
     */
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient;
                if (getModel().isPressed()) {
                    gradient = new GradientPaint(
                        0, 0, 
                        darken(color, 0.2f),
                        0, getHeight(), 
                        darken(color, 0.1f)
                    );
                } else if (getModel().isRollover()) {
                    gradient = new GradientPaint(
                        0, 0, 
                        lighten(color, 0.1f),
                        0, getHeight(), 
                        color
                    );
                } else {
                    gradient = new GradientPaint(
                        0, 0, 
                        color,
                        0, getHeight(), 
                        darken(color, 0.1f)
                    );
                }
                
                g2d.setPaint(gradient);
                
                // Draw rounded rectangle
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                
                // Add subtle border
                g2d.setColor(darken(color, 0.2f));
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        
        // Add padding
        button.setMargin(new Insets(8, 15, 8, 15));
        
        return button;
    }
    
    /**
     * Adds a detail row to the details panel.
     * 
     * @param panel The panel to add to
     * @param label The label text
     * @param value The value text
     */
    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setOpaque(false);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.CENTER);
        
        panel.add(rowPanel);
        panel.add(Box.createVerticalStrut(5));
    }
    
    /**
     * Creates a gradient button with a more visible, attractive style.
     * 
     * @param text The button text
     * @param startColor The gradient start color
     * @param endColor The gradient end color
     * @return The configured button
     */
    private JButton createGradientButton(String text, Color startColor, Color endColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient;
                if (getModel().isPressed()) {
                    gradient = new GradientPaint(
                        0, 0, 
                        darken(endColor, 0.1f),
                        0, getHeight(), 
                        darken(startColor, 0.1f)
                    );
                } else if (getModel().isRollover()) {
                    gradient = new GradientPaint(
                        0, 0, 
                        lighten(startColor, 0.1f),
                        0, getHeight(), 
                        lighten(endColor, 0.1f)
                    );
                } else {
                    gradient = new GradientPaint(
                        0, 0, 
                        startColor,
                        0, getHeight(), 
                        endColor
                    );
                }
                
                g2d.setPaint(gradient);
                
                // Draw rounded rectangle
                int arc = getHeight();
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                
                // Add glass effect
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight() / 2, arc, arc);
                
                // Add subtle border
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arc, arc);
                
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        
        // Add padding
        button.setMargin(new Insets(8, 15, 8, 15));
        
        // Add drop shadow
        button.setBorder(new Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(x + 2, y + 2, width - 4, height - 2, height, height);
                
                g2d.dispose();
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(0, 0, 4, 0);
            }
            
            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        });
        
        return button;
    }
    
    /**
     * Creates a navigation button with a more visible style.
     * 
     * @param text The button text
     * @return The configured button
     */
    private JButton createNavigationButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create background
                if (getModel().isPressed()) {
                    g2d.setColor(darken(accentColor, 0.2f));
                } else if (getModel().isRollover()) {
                    g2d.setColor(accentColor);
                } else {
                    g2d.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 180));
                }
                
                // Draw circle
                g2d.fillOval(0, 0, getWidth(), getHeight());
                
                // Add highlight
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2d.setColor(Color.WHITE);
                g2d.fillOval(2, 2, getWidth() - 4, getHeight() / 2 - 2);
                
                g2d.dispose();
                
                super.paintComponent(g);
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(30, 30);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        
        return button;
    }
    
    /**
     * Creates a view selection button with a more visible style.
     * 
     * @param text The button text
     * @param view The view identifier
     * @return The configured button
     */
    private JButton createViewButton(String text, String view) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create background
                if (currentView.equals(view)) {
                    // Gradient for selected view
                    GradientPaint gradient = new GradientPaint(
                        0, 0, 
                        accentColor, 
                        0, getHeight(), 
                        darken(accentColor, 0.1f)
                    );
                    g2d.setPaint(gradient);
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(70, 80, 120));
                } else {
                    g2d.setColor(new Color(40, 50, 95));
                }
                
                // Draw button shape
                if (view.equals("week")) {
                    // Left button
                    g2d.fillRoundRect(0, 0, getWidth() + 5, getHeight(), 10, 10);
                    g2d.fillRect(getWidth() - 5, 0, 10, getHeight());
                } else if (view.equals("year")) {
                    // Right button
                    g2d.fillRoundRect(- 5, 0, getWidth() + 5, getHeight(), 10, 10);
                    g2d.fillRect(0, 0, 10, getHeight());
                } else {
                    // Middle button
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                
                // Add highlight for selected view
                if (currentView.equals(view)) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                    g2d.setColor(Color.WHITE);
                    if (view.equals("week")) {
                        g2d.fillRoundRect(0, 0, getWidth() + 5, getHeight() / 2, 10, 10);
                        g2d.fillRect(getWidth() - 5, 0, 10, getHeight() / 2);
                    } else if (view.equals("year")) {
                        g2d.fillRoundRect(- 5, 0, getWidth() + 5, getHeight() / 2, 10, 10);
                        g2d.fillRect(0, 0, 10, getHeight() / 2);
                    } else {
                        g2d.fillRect(0, 0, getWidth(), getHeight() / 2);
                    }
                }
                
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(currentView.equals(view) ? Color.WHITE : textColor);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        
        // Add padding
        button.setMargin(new Insets(8, 15, 8, 15));
        
        // Add action listener
        button.addActionListener(e -> {
            currentView = view;
            updateViewButtons();
            refreshCalendar();
        });
        
        return button;
    }
    
    /**
     * Updates the appearance of view buttons based on the current selection.
     */
    private void updateViewButtons() {
        weekViewButton.repaint();
        monthViewButton.repaint();
        yearViewButton.repaint();
        
        weekViewButton.setForeground(currentView.equals("week") ? Color.WHITE : textColor);
        monthViewButton.setForeground(currentView.equals("month") ? Color.WHITE : textColor);
        yearViewButton.setForeground(currentView.equals("year") ? Color.WHITE : textColor);
    }
    
    /**
     * Creates a styled combo box with a clean, formal appearance.
     * 
     * @param items The combo box items
     * @return The configured combo box
     */
    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setBackground(secondaryColor);
        comboBox.setForeground(textColor);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Custom renderer
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                
                if (isSelected) {
                    setBackground(accentColor);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(secondaryColor);
                    setForeground(textColor);
                }
                
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                return this;
            }
        });
        
        return comboBox;
    }
    
    /**
     * Gets the list of vehicle options for filtering.
     * 
     * @return Array of vehicle options
     */
    private String[] getVehicleOptions() {
        List<Vehicle> vehicles = dataManager.getAllVehicles();
        String[] options = new String[vehicles.size() + 1];
        options[0] = "All Vehicles";
        
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);
            options[i + 1] = vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getLicensePlate() + ")";
        }
        
        return options;
    }
    
    /**
     * Gets the reservations for a specific date.
     * 
     * @param date The date to get reservations for
     * @return List of reservations
     */
    private List<Reservation> getReservationsForDate(LocalDate date) {
        List<Reservation> allReservations = dataManager.getAllReservations();
        List<Reservation> dateReservations = new ArrayList<>();
        
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        
        for (Reservation reservation : allReservations) {
            // Check if the reservation overlaps with the given date
            if (reservation.isOverlapping(startOfDay, endOfDay)) {
                // Apply filters if set
                if (selectedVehicleId != null && !reservation.getVehicleId().equals(selectedVehicleId)) {
                    continue;
                }
                
                if (selectedStatus != null && reservation.getStatus() != selectedStatus) {
                    continue;
                }
                
                dateReservations.add(reservation);
            }
        }
        
        return dateReservations;
    }
    
    /**
     * Creates a today icon for the Today button.
     * 
     * @return The icon
     */
    private Icon createTodayIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw calendar background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(x, y, 16, 16, 3, 3);
                
                // Draw calendar header
                g2d.setColor(accentColor);
                g2d.fillRoundRect(x, y, 16, 5, 3, 3);
                g2d.fillRect(x, y + 3, 16, 2);
                
                // Draw calendar grid lines
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawLine(x + 8, y + 6, x + 8, y + 15);
                g2d.drawLine(x + 1, y + 10, x + 15, y + 10);
                
                // Draw today's date
                g2d.setColor(accentColorAlt);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 8));
                String day = String.valueOf(LocalDate.now().getDayOfMonth());
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(day);
                g2d.drawString(day, x + 8 - textWidth / 2, y + 15);
                
                g2d.dispose();
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
    
    /**
     * Creates an add icon for the Add Reservation button.
     * 
     * @return The icon
     */
    private Icon createAddIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circle background
                g2d.setColor(Color.WHITE);
                g2d.fillOval(x, y, 16, 16);
                
                // Draw plus sign
                g2d.setColor(accentColorAlt);
                g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(x + 4, y + 8, x + 12, y + 8);
                g2d.drawLine(x + 8, y + 4, x + 8, y + 12);
                
                g2d.dispose();
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
    
    /**
     * Lightens a color by the specified amount.
     * 
     * @param color The color to lighten
     * @param amount The amount to lighten by
     * @return The lightened color
     */
    private Color lighten(Color color, float amount) {
        int r = Math.min(255, (int) (color.getRed() + 255 * amount));
        int g = Math.min(255, (int) (color.getGreen() + 255 * amount));
        int b = Math.min(255, (int) (color.getBlue() + 255 * amount));
        return new Color(r, g, b);
    }
    
    /**
     * Darkens a color by the specified amount.
     * 
     * @param color The color to darken
     * @param amount The amount to darken by
     * @return The darkened color
     */
    private Color darken(Color color, float amount) {
        int r = Math.max(0, (int) (color.getRed() * (1 - amount)));
        int g = Math.max(0, (int) (color.getGreen() * (1 - amount)));
        int b = Math.max(0, (int) (color.getBlue() * (1 - amount)));
        return new Color(r, g, b);
    }
    
    /**
     * Custom ScrollBar UI for a clean, minimal appearance.
     */
    private class ModernScrollBarUI extends BasicScrollBarUI {
        private Color thumbColor = new Color(255, 255, 255, 80);
        
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
            
            // Draw minimal thumb
            g2d.setColor(thumbColor);
            g2d.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
            
            g2d.dispose();
        }
        
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(0, 0, 0, 0));
            g2d.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2d.dispose();
        }
    }
}
