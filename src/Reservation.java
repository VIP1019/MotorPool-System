import java.time.LocalDateTime;

/**
* This is a placeholder class to help with imports.
* The actual Reservation class is in the model package.
*/
public class Reservation extends model.Reservation {
   public Reservation(String reservationId, String userId, String vehicleId, 
                     LocalDateTime startDateTime, LocalDateTime endDateTime,
                     String purpose, String destination, int estimatedPassengers) {
       super(reservationId, userId, vehicleId, startDateTime, endDateTime, purpose, destination, estimatedPassengers);
   }
}

