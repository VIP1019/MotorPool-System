package model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* Represents a vehicle reservation
*/
public class Reservation implements Serializable {
  private static final long serialVersionUID = 1L;
  
  public enum ReservationStatus {
      PENDING, APPROVED, REJECTED, CANCELLED, COMPLETED
  }
  
  private String reservationId;
  private String userId;
  private String vehicleId;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String purpose;
  private String destination;
  private int estimatedPassengers;
  private ReservationStatus status;
  private String approvedBy;
  private LocalDateTime approvalDateTime;
  private String notes;
  private double initialMileage;
  private double finalMileage;
  
  public Reservation(String reservationId, String userId, String vehicleId, 
                    LocalDateTime startDateTime, LocalDateTime endDateTime,
                    String purpose, String destination, int estimatedPassengers) {
      this.reservationId = reservationId;
      this.userId = userId;
      this.vehicleId = vehicleId;
      this.startDateTime = startDateTime;
      this.endDateTime = endDateTime;
      this.purpose = purpose;
      this.destination = destination;
      this.estimatedPassengers = estimatedPassengers;
      this.status = ReservationStatus.PENDING;
      this.notes = "";
  }
  
  // Getters and setters
  public String getReservationId() {
      return reservationId;
  }
  
  public String getUserId() {
      return userId;
  }
  
  public String getVehicleId() {
      return vehicleId;
  }
  
  public void setVehicleId(String vehicleId) {
      this.vehicleId = vehicleId;
  }
  
  public LocalDateTime getStartDateTime() {
      return startDateTime;
  }
  
  public void setStartDateTime(LocalDateTime startDateTime) {
      this.startDateTime = startDateTime;
  }
  
  public LocalDateTime getEndDateTime() {
      return endDateTime;
  }
  
  public void setEndDateTime(LocalDateTime endDateTime) {
      this.endDateTime = endDateTime;
  }
  
  public String getPurpose() {
      return purpose;
  }
  
  public void setPurpose(String purpose) {
      this.purpose = purpose;
  }
  
  public String getDestination() {
      return destination;
  }
  
  public void setDestination(String destination) {
      this.destination = destination;
  }
  
  public int getEstimatedPassengers() {
      return estimatedPassengers;
  }
  
  public void setEstimatedPassengers(int estimatedPassengers) {
      this.estimatedPassengers = estimatedPassengers;
  }
  
  public ReservationStatus getStatus() {
      return status;
  }
  
  public void setStatus(ReservationStatus status) {
      this.status = status;
  }
  
  public String getApprovedBy() {
      return approvedBy;
  }
  
  public void setApprovedBy(String approvedBy) {
      this.approvedBy = approvedBy;
  }
  
  public LocalDateTime getApprovalDateTime() {
      return approvalDateTime;
  }
  
  public void setApprovalDateTime(LocalDateTime approvalDateTime) {
      this.approvalDateTime = approvalDateTime;
  }
  
  public String getNotes() {
      return notes;
  }
  
  public void setNotes(String notes) {
      this.notes = notes;
  }
  
  public double getInitialMileage() {
      return initialMileage;
  }
  
  public void setInitialMileage(double initialMileage) {
      this.initialMileage = initialMileage;
  }
  
  public double getFinalMileage() {
      return finalMileage;
  }
  
  public void setFinalMileage(double finalMileage) {
      this.finalMileage = finalMileage;
  }
  
  public boolean isActive() {
      return status == ReservationStatus.APPROVED || status == ReservationStatus.PENDING;
  }
  
  public boolean isOverlapping(LocalDateTime start, LocalDateTime end) {
      return (startDateTime.isBefore(end) && endDateTime.isAfter(start));
  }
  
  @Override
  public String toString() {
      return "Reservation #" + reservationId + " - " + status;
  }
}

