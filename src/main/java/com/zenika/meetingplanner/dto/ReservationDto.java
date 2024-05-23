package com.zenika.meetingplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {
    private Long id;
    private int reservationNumber;
    private String date;
    private String startTime;
    private String endTime;
    private String type;
    private int attendees;
    private String reservedBy;
    private RoomDto room;

    
}
