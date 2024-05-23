package com.zenika.meetingplanner.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_number")
    private int reservationNumber;

    private LocalDate date;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private String type;

    private int attendees;
    
    @Column(name = "reservedBy")
    private String reservedBy;
    
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "room_name")
    private Room room;
}
