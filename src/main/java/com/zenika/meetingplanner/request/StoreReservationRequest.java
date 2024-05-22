package com.zenika.meetingplanner.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class StoreReservationRequest {
    private int attendees;
    private String type;
    private LocalDate date;
    private LocalTime startTime;
    private String reservedBy;
}
