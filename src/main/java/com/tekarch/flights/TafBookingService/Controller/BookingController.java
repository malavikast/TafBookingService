package com.tekarch.flights.TafBookingService.Controller;

import com.tekarch.flights.TafBookingService.Model.Booking;
import com.tekarch.flights.TafBookingService.Service.Interface.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Endpoint to create a new booking
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestParam("uid") Long uid, @RequestParam("fid") Long fid) {
        try {
            Booking booking = bookingService.createBooking(uid, fid);
            return new ResponseEntity<>(booking, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    // Endpoint to get booking details by booking ID
    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        if (booking != null) {
            return new ResponseEntity<>(booking, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }


    // Endpoint to get all bookings for a specific user
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Booking>> getBookingsByUserId(@PathVariable Long userId) {
//        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
//        if (bookings != null && !bookings.isEmpty()) {
//            return new ResponseEntity<>(bookings, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
//        }
//    }

    // Endpoint to cancel a booking (mark as "Cancelled")
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}