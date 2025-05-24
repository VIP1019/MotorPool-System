package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Vehicle implements Serializable {
  private static final long serialVersionUID = 1L;
  
  public enum VehicleType {
      SEDAN, SUV, VAN, BUS, TRUCK
  }
  
  public enum FuelType {
      GASOLINE, DIESEL, ELECTRIC, HYBRID
  }
  
  private String vehicleId;
  private String make;
  private String model;
  private int year;
  private String licensePlate;
  private VehicleType type;
  private int capacity;
  private FuelType fuelType;
  private double mileage;
  private boolean isAvailable;
  private String currentLocation;
  private String notes;
  private List<MaintenanceRecord> maintenanceHistory;
  
  public Vehicle(String vehicleId, String make, String model, int year, String licensePlate,
                VehicleType type, int capacity, FuelType fuelType) {
      this.vehicleId = vehicleId;
      this.make = make;
      this.model = model;
      this.year = year;
      this.licensePlate = licensePlate;
      this.type = type;
      this.capacity = capacity;
      this.fuelType = fuelType;
      this.mileage = 0;
      this.isAvailable = true;
      this.currentLocation = "Campus Garage";
      this.notes = "";
      this.maintenanceHistory = new ArrayList<>();
  }
  
  // Getters and setters
  public String getVehicleId() {
      return vehicleId;
  }
  
  public String getMake() {
      return make;
  }
  
  public void setMake(String make) {
      this.make = make;
  }
  
  public String getModel() {
      return model;
  }
  
  public void setModel(String model) {
      this.model = model;
  }
  
  public int getYear() {
      return year;
  }
  
  public void setYear(int year) {
      this.year = year;
  }
  
  public String getLicensePlate() {
      return licensePlate;
  }
  
  public void setLicensePlate(String licensePlate) {
      this.licensePlate = licensePlate;
  }
  
  public VehicleType getType() {
      return type;
  }
  
  public void setType(VehicleType type) {
      this.type = type;
  }
  
  public int getCapacity() {
      return capacity;
  }
  
  public void setCapacity(int capacity) {
      this.capacity = capacity;
  }
  
  public FuelType getFuelType() {
      return fuelType;
  }
  
  public void setFuelType(FuelType fuelType) {
      this.fuelType = fuelType;
  }
  
  public double getMileage() {
      return mileage;
  }
  
  public void setMileage(double mileage) {
      this.mileage = mileage;
  }
  
  public boolean isAvailable() {
      return isAvailable;
  }
  
  public void setAvailable(boolean available) {
      isAvailable = available;
  }
  
  public String getCurrentLocation() {
      return currentLocation;
  }
  
  public void setCurrentLocation(String currentLocation) {
      this.currentLocation = currentLocation;
  }
  
  public String getNotes() {
      return notes;
  }
  
  public void setNotes(String notes) {
      this.notes = notes;
  }
  
  public List<MaintenanceRecord> getMaintenanceHistory() {
      return maintenanceHistory;
  }
  
  public void addMaintenanceRecord(MaintenanceRecord record) {
      maintenanceHistory.add(record);
  }
  
  @Override
  public String toString() {
      return year + " " + make + " " + model + " (" + licensePlate + ")";
  }
  
  /**
   * Inner class for vehicle maintenance records
   */
  public static class MaintenanceRecord implements Serializable {
      private static final long serialVersionUID = 1L;
      
      private String date;
      private String description;
      private double cost;
      private String technician;
      
      public MaintenanceRecord(String date, String description, double cost, String technician) {
          this.date = date;
          this.description = description;
          this.cost = cost;
          this.technician = technician;
      }
      
      // Getters
      public String getDate() {
          return date;
      }
      
      public String getDescription() {
          return description;
      }
      
      public double getCost() {
          return cost;
      }
      
      public String getTechnician() {
          return technician;
      }
  }
}

