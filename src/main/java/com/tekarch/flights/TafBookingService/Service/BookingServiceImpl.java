package com.tekarch.flights.TafBookingService.Service;

//import com.tekarch.flights.TafBookingService.Model.Booking;

import com.tekarch.flights.TafBookingService.Model.Booking;
import com.tekarch.flights.TafBookingService.Model.Flight;
import com.tekarch.flights.TafBookingService.Model.User;
import com.tekarch.flights.TafBookingService.Service.Interface.BookingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class BookingServiceImpl implements BookingService {

    Logger logger = LogManager.getLogger(BookingServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    private static final String DATASOURCE_URL = "http://localhost:8081/bookings";
    private static final String FLIGHT_URL = "http://localhost:8081/flights/";
    private static final String USER_DATASOURCE_URL ="http://localhost:8081/users/";

    @Override
    public Booking createBooking(Long userId, Long flightId) {
        // Check available seats
//        System.out.println(userId);
        logger.info("userId"+ userId);

        Flight flight = restTemplate.getForObject(FLIGHT_URL + flightId, Flight.class);
        User user= restTemplate.getForObject(USER_DATASOURCE_URL + userId, User.class);
        if (flight == null) {
            throw new IllegalArgumentException("Flight not available");
        }

        if (flight.getAvailableSeats() <= 0) {
            throw new IllegalArgumentException("No available seats");
        }

        // Create a booking if seats are available
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setStatus("Booked");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        logger.info("Booking - "+booking.getUser().getId());
        logger.info("Booking - "+ booking.getFlight().getId());


        // Reduce available seats after booking
        flight.setAvailableSeats(flight.getAvailableSeats() - 1);
        restTemplate.put(FLIGHT_URL + flightId, flight);  // Update the available seats in flight
        logger.info("Booking object"+booking.toString());
        Booking savedBooking = restTemplate.postForObject(DATASOURCE_URL, booking, Booking.class);

        logger.info("saved booking"+savedBooking);

        // Create the booking in the datastore service
        return savedBooking;

    }

    @Override
    public Booking getBookingById(Long bookingId) {
        // Get booking by ID
        String getBookingUrl= DATASOURCE_URL + "/" + bookingId;
        return restTemplate.getForObject(getBookingUrl, Booking.class);
    }

//    @Override
//    public List<Booking> getBookingsByUserId(Long userId) {
//        // Get bookings by user ID
//        String  getBookingbyUserId = DATASOURCE_URL + "/user/" + userId;
//        return restTemplate.getForObject(getBookingbyUserId, List.class);
//    }

    @Override
    public void cancelBooking(Long bookingId) {
        // Cancel booking and update status
        String deleteBookingUrl= DATASOURCE_URL + "/" + bookingId;
        Booking booking = restTemplate.getForObject(deleteBookingUrl, Booking.class);

        if (booking != null) {


            // Restore available seat count
            Flight flight = restTemplate.getForObject(FLIGHT_URL + booking.getFlight().getId(), Flight.class);
            if (flight != null) {
                flight.setAvailableSeats(flight.getAvailableSeats() + 1);
                restTemplate.put(FLIGHT_URL + booking.getFlight().getId(), flight);
            }

            // Update booking status in the datastore service
//            String deleteBookingUrl= DATASOURCE_URL + "/" + bookingId;
            logger.info("delete "+ deleteBookingUrl);
            restTemplate.delete(deleteBookingUrl);
        }
    }
}