package com.zenika.meetingplanner.service;

import com.zenika.meetingplanner.model.Reservation;
import com.zenika.meetingplanner.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    /**
     * Get all reservations
     * @return List of Reservation
     */
    public List<Reservation> findAll(){
        return reservationRepository.findAll();
    }

    /**
     * Save a reservation
     * @param reservation The reservation to save
     * @return The saved reservation
     */
    public Reservation save(Reservation reservation){
        return reservationRepository.save(reservation);
    }
}
