import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import model.Reservation;
import model.User;
import model.Vehicle;

/**
 * Singleton class for managing application data
 */
public class DataManager {
    private static DataManager instance;
    
    private Map<String, User> users;
    private Map<String, Vehicle> vehicles;
    private Map<String, Reservation> reservations;
    
    private User currentUser;
    private boolean dataChanged;
    
    // Thread safety
    private final ReadWriteLock usersLock = new ReentrantReadWriteLock();
    private final ReadWriteLock vehiclesLock = new ReentrantReadWriteLock();
    private final ReadWriteLock reservationsLock = new ReentrantReadWriteLock();
    
    private DataManager() {
        users = new HashMap<>();
        vehicles = new HashMap<>();
        reservations = new HashMap<>();
        dataChanged = false;
        
        // Create default admin if no users exist
        createDefaultAdminIfNeeded();
    }
    
    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    private void createDefaultAdminIfNeeded() {
        File userDir = new File("data/users");
        if (userDir.exists() && userDir.list().length == 0) {
            User admin = new User("admin1", "admin", "admin123", "System Administrator",
                    "admin@university.edu", "555-123-4567", User.UserRole.ADMIN);
            users.put(admin.getUserId(), admin);
            saveUser(admin);
        }
    }
    
    // User methods
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public User getUserById(String userId) {
        usersLock.readLock().lock();
        try {
            return users.get(userId);
        } finally {
            usersLock.readLock().unlock();
        }
    }
    
    public User getUserByUsername(String username) {
        usersLock.readLock().lock();
        try {
            for (User user : users.values()) {
                if (user.getUsername().equals(username)) {
                    return user;
                }
            }
            return null;
        } finally {
            usersLock.readLock().unlock();
        }
    }
    
    public List<User> getAllUsers() {
        usersLock.readLock().lock();
        try {
            return new ArrayList<>(users.values());
        } finally {
            usersLock.readLock().unlock();
        }
    }
    
    public void addUser(User user) {
        usersLock.writeLock().lock();
        try {
            users.put(user.getUserId(), user);
            saveUser(user);
            dataChanged = true;
        } finally {
            usersLock.writeLock().unlock();
        }
    }
    
    public void updateUser(User user) {
        usersLock.writeLock().lock();
        try {
            users.put(user.getUserId(), user);
            saveUser(user);
            dataChanged = true;
        } finally {
            usersLock.writeLock().unlock();
        }
    }
    
    public void deleteUser(String userId) {
        usersLock.writeLock().lock();
        try {
            users.remove(userId);
            File userFile = new File("data/users/" + userId + ".dat");
            if (userFile.exists()) {
                userFile.delete();
            }
            dataChanged = true;
        } finally {
            usersLock.writeLock().unlock();
        }
    }
    
    // Vehicle methods
    public Vehicle getVehicleById(String vehicleId) {
        vehiclesLock.readLock().lock();
        try {
            return vehicles.get(vehicleId);
        } finally {
            vehiclesLock.readLock().unlock();
        }
    }
    
    public List<Vehicle> getAllVehicles() {
        vehiclesLock.readLock().lock();
        try {
            return new ArrayList<>(vehicles.values());
        } finally {
            vehiclesLock.readLock().unlock();
        }
    }
    
    public List<Vehicle> getAvailableVehicles(LocalDateTime start, LocalDateTime end) {
        vehiclesLock.readLock().lock();
        reservationsLock.readLock().lock();
        try {
            List<Vehicle> availableVehicles = new ArrayList<>();
            
            for (Vehicle vehicle : vehicles.values()) {
                // First check if the vehicle is marked as available in general
                if (vehicle.isAvailable()) {
                    boolean isReserved = false;
                    
                    // Then check if there are any active reservations that overlap with the requested time period
                    for (Reservation reservation : reservations.values()) {
                        if (reservation.getVehicleId().equals(vehicle.getVehicleId()) &&
                            reservation.isActive() &&
                            reservation.isOverlapping(start, end)) {
                            isReserved = true;
                            break;
                        }
                    }
                    
                    if (!isReserved) {
                        availableVehicles.add(vehicle);
                    }
                }
            }
            
            return availableVehicles;
        } finally {
            reservationsLock.readLock().unlock();
            vehiclesLock.readLock().unlock();
        }
    }
    
    public void addVehicle(Vehicle vehicle) {
        vehiclesLock.writeLock().lock();
        try {
            vehicles.put(vehicle.getVehicleId(), vehicle);
            saveVehicle(vehicle);
            dataChanged = true;
        } finally {
            vehiclesLock.writeLock().unlock();
        }
    }
    
    public void updateVehicle(Vehicle vehicle) {
        vehiclesLock.writeLock().lock();
        try {
            vehicles.put(vehicle.getVehicleId(), vehicle);
            saveVehicle(vehicle);
            dataChanged = true;
        } finally {
            vehiclesLock.writeLock().unlock();
        }
    }
    
    public void deleteVehicle(String vehicleId) {
        vehiclesLock.writeLock().lock();
        try {
            vehicles.remove(vehicleId);
            File vehicleFile = new File("data/vehicles/" + vehicleId + ".dat");
            if (vehicleFile.exists()) {
                vehicleFile.delete();
            }
            dataChanged = true;
        } finally {
            vehiclesLock.writeLock().unlock();
        }
    }
    
    // Reservation methods
    public Reservation getReservationById(String reservationId) {
        reservationsLock.readLock().lock();
        try {
            return reservations.get(reservationId);
        } finally {
            reservationsLock.readLock().unlock();
        }
    }
    
    public List<Reservation> getAllReservations() {
        reservationsLock.readLock().lock();
        try {
            return new ArrayList<>(reservations.values());
        } finally {
            reservationsLock.readLock().unlock();
        }
    }
    
    public List<Reservation> getUserReservations(String userId) {
        reservationsLock.readLock().lock();
        try {
            List<Reservation> userReservations = new ArrayList<>();
            for (Reservation reservation : reservations.values()) {
                if (reservation.getUserId().equals(userId)) {
                    userReservations.add(reservation);
                }
            }
            return userReservations;
        } finally {
            reservationsLock.readLock().unlock();
        }
    }
    
    public List<Reservation> getVehicleReservations(String vehicleId) {
        reservationsLock.readLock().lock();
        try {
            List<Reservation> vehicleReservations = new ArrayList<>();
            for (Reservation reservation : reservations.values()) {
                if (reservation.getVehicleId().equals(vehicleId)) {
                    vehicleReservations.add(reservation);
                }
            }
            return vehicleReservations;
        } finally {
            reservationsLock.readLock().unlock();
        }
    }
    
    public void addReservation(Reservation reservation) {
        reservationsLock.writeLock().lock();
        try {
            reservations.put(reservation.getReservationId(), reservation);
            saveReservation(reservation);
            dataChanged = true;
        } finally {
            reservationsLock.writeLock().unlock();
        }
    }
    
    public void updateReservation(Reservation reservation) {
        reservationsLock.writeLock().lock();
        try {
            reservations.put(reservation.getReservationId(), reservation);
            saveReservation(reservation);
            dataChanged = true;
        } finally {
            reservationsLock.writeLock().unlock();
        }
    }
    
    public void deleteReservation(String reservationId) {
        reservationsLock.writeLock().lock();
        try {
            reservations.remove(reservationId);
            File reservationFile = new File("data/reservations/" + reservationId + ".dat");
            if (reservationFile.exists()) {
                reservationFile.delete();
            }
            dataChanged = true;
        } finally {
            reservationsLock.writeLock().unlock();
        }
    }
    
    // Data loading and saving methods
    public void loadAllData() {
        loadUsers();
        loadVehicles();
        loadReservations();
        dataChanged = false;
    }
    
    public void saveAllData() {
        if (dataChanged) {
            saveUsers();
            saveVehicles();
            saveReservations();
            dataChanged = false;
        }
    }
    
    private void loadUsers() {
        usersLock.writeLock().lock();
        try {
            users.clear();
            File userDir = new File("data/users");
            if (userDir.exists()) {
                File[] userFiles = userDir.listFiles((dir, name) -> name.endsWith(".dat"));
                if (userFiles != null) {
                    for (File file : userFiles) {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                            User user = (User) ois.readObject();
                            users.put(user.getUserId(), user);
                        } catch (Exception e) {
                            System.err.println("Error loading user from " + file.getName() + ": " + e.getMessage());
                        }
                    }
                }
            }
        } finally {
            usersLock.writeLock().unlock();
        }
    }
    
    private void loadVehicles() {
        vehiclesLock.writeLock().lock();
        try {
            vehicles.clear();
            File vehicleDir = new File("data/vehicles");
            if (vehicleDir.exists()) {
                File[] vehicleFiles = vehicleDir.listFiles((dir, name) -> name.endsWith(".dat"));
                if (vehicleFiles != null) {
                    for (File file : vehicleFiles) {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                            Vehicle vehicle = (Vehicle) ois.readObject();
                            vehicles.put(vehicle.getVehicleId(), vehicle);
                        } catch (Exception e) {
                            System.err.println("Error loading vehicle from " + file.getName() + ": " + e.getMessage());
                        }
                    }
                }
            }
        } finally {
            vehiclesLock.writeLock().unlock();
        }
    }
    
    private void loadReservations() {
        reservationsLock.writeLock().lock();
        try {
            reservations.clear();
            File reservationDir = new File("data/reservations");
            if (reservationDir.exists()) {
                File[] reservationFiles = reservationDir.listFiles((dir, name) -> name.endsWith(".dat"));
                if (reservationFiles != null) {
                    for (File file : reservationFiles) {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                            Reservation reservation = (Reservation) ois.readObject();
                            reservations.put(reservation.getReservationId(), reservation);
                        } catch (Exception e) {
                            System.err.println("Error loading reservation from " + file.getName() + ": " + e.getMessage());
                        }
                    }
                }
            }
        } finally {
            reservationsLock.writeLock().unlock();
        }
    }
    
    private void saveUsers() {
        usersLock.readLock().lock();
        try {
            for (User user : users.values()) {
                saveUser(user);
            }
        } finally {
            usersLock.readLock().unlock();
        }
    }
    
    private void saveVehicles() {
        vehiclesLock.readLock().lock();
        try {
            for (Vehicle vehicle : vehicles.values()) {
                saveVehicle(vehicle);
            }
        } finally {
            vehiclesLock.readLock().unlock();
        }
    }
    
    private void saveReservations() {
        reservationsLock.readLock().lock();
        try {
            for (Reservation reservation : reservations.values()) {
                saveReservation(reservation);
            }
        } finally {
            reservationsLock.readLock().unlock();
        }
    }
    
    private void saveUser(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("data/users/" + user.getUserId() + ".dat"))) {
            oos.writeObject(user);
        } catch (IOException e) {
            System.err.println("Error saving user " + user.getUserId() + ": " + e.getMessage());
        }
    }
    
    private void saveVehicle(Vehicle vehicle) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("data/vehicles/" + vehicle.getVehicleId() + ".dat"))) {
            oos.writeObject(vehicle);
        } catch (IOException e) {
            System.err.println("Error saving vehicle " + vehicle.getVehicleId() + ": " + e.getMessage());
        }
    }
    
    private void saveReservation(Reservation reservation) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("data/reservations/" + reservation.getReservationId() + ".dat"))) {
            oos.writeObject(reservation);
        } catch (IOException e) {
            System.err.println("Error saving reservation " + reservation.getReservationId() + ": " + e.getMessage());
        }
    }
    
    public boolean isDataChanged() {
        return dataChanged;
    }
    
    public void setDataChanged(boolean dataChanged) {
        this.dataChanged = dataChanged;
    }
}
