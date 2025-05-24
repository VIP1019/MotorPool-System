package model;

import java.io.Serializable;

/**
* Represents a user in the system
*/
public class User implements Serializable {
 private static final long serialVersionUID = 1L;
 
 public enum UserRole {
     ADMIN, USER, STUDENT, FACULTY, STAFF; // Added back STUDENT, FACULTY, STAFF for backward compatibility
     
     // Convert legacy roles to current roles
     public UserRole toCurrentRole() {
         switch(this) {
             case ADMIN:
                 return ADMIN;
             case STUDENT:
             case FACULTY:
             case STAFF:
             case USER:
             default:
                 return USER;
         }
     }
 }
 
 private String userId;
 private String username;
 private String password;
 private String fullName;
 private String email;
 private String phoneNumber;
 private UserRole role;
 private boolean isActive;
 
 public User(String userId, String username, String password, String fullName, 
             String email, String phoneNumber, UserRole role) {
     this.userId = userId;
     this.username = username;
     this.password = password;
     this.fullName = fullName;
     this.email = email;
     this.phoneNumber = phoneNumber;
     this.role = role;
     this.isActive = true;
 }
 
 // Getters and setters
 public String getUserId() {
     return userId;
 }
 
 public String getUsername() {
     return username;
 }
 
 public void setUsername(String username) {
     this.username = username;
 }
 
 public String getPassword() {
     return password;
 }
 
 public void setPassword(String password) {
     this.password = password;
 }
 
 public String getFullName() {
     return fullName;
 }
 
 public void setFullName(String fullName) {
     this.fullName = fullName;
 }
 
 public String getEmail() {
     return email;
 }
 
 public void setEmail(String email) {
     this.email = email;
 }
 
 public String getPhoneNumber() {
     return phoneNumber;
 }
 
 public void setPhoneNumber(String phoneNumber) {
     this.phoneNumber = phoneNumber;
 }
 
 public UserRole getRole() {
     return role;
 }
 
 public void setRole(UserRole role) {
     this.role = role;
 }
 
 public boolean isActive() {
     return isActive;
 }
 
 public void setActive(boolean active) {
     isActive = active;
 }
 
 public boolean isAdmin() {
     return role == UserRole.ADMIN;
 }
 
 @Override
 public String toString() {
     return fullName + " (" + role + ")";
 }
}

