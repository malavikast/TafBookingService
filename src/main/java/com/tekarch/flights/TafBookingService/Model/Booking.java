package com.tekarch.flights.TafBookingService.Model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Booking {
    private Long id;
    private User user;
    private Flight flight;
    private String status; // Booked, Cancelled
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "MyRequest{" +
                "userId='" + user.getId() + '\'' +
                ", flightId='" + flight.getId() + '\'' +
                '}';
    }

    // Getters and setters

}

