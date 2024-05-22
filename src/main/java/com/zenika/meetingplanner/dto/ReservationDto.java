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
    private String date;
    private String startTime;
    private String endTime;
    private String type;
    private String reservedBy;
    private RoomDto room;

    
}
