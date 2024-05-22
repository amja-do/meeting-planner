package com.zenika.meetingplanner.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDto {
    private String name;
    private int maxCapacity;
    private Set<EquipmentDto> equipments;
    
}
