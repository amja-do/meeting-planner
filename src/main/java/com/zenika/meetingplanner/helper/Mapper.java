package com.zenika.meetingplanner.helper;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import com.zenika.meetingplanner.dto.EquipmentDto;
import com.zenika.meetingplanner.dto.ReservationDto;
import com.zenika.meetingplanner.dto.RoomDto;
import com.zenika.meetingplanner.model.Equipment;
import com.zenika.meetingplanner.model.Reservation;
import com.zenika.meetingplanner.model.Room;

public class Mapper {

    /**
     * Convert Equipment to EquipmentDto
     * @param equipment The equipment to convert
     * @return The EquipmentDto
     */
    public static EquipmentDto EquipmentToEquipmentDto(Equipment equipment) {
        return EquipmentDto.builder()
                .id(equipment.getId())
                .name(equipment.getName())
                .build();
    }

    /**
     * Convert EquipmentDto to Equipment
     * @param equipmentDto The EquipmentDto to convert
     * @return The Equipment
     */
    public static Equipment EquipmentDtoToEquipment(EquipmentDto equipmentDto) {
        return Equipment.builder()
                .id(equipmentDto.getId())
                .name(equipmentDto.getName())
                .build();
    }

    /**
     * Convert Room to RoomDto
     * @param room The Room to convert
     * @return The RoomDto
     */
    public static RoomDto RoomToRoomDto(com.zenika.meetingplanner.model.Room room) {
        return RoomDto.builder()
                .name(room.getName())
                .maxCapacity(room.getMaxCapacity())
                .equipments(room.getEquipments().stream().map(Mapper::EquipmentToEquipmentDto).collect(Collectors.toSet()))
                .build();
    }

    /**
     * Convert RoomDto to Room
     * @param roomDto The RoomDto to convert
     * @return The Room
     */
    public static Room RoomDtoToRoom(RoomDto roomDto) {
        return Room.builder()
                .name(roomDto.getName())
                .maxCapacity(roomDto.getMaxCapacity())
                .equipments(roomDto.getEquipments().stream().map(Mapper::EquipmentDtoToEquipment).collect(Collectors.toSet()))
                .build();
    }


    /**
     * Convert Reservation to ReservationDto
     * @param reservation The Reservation to convert
     * @return The ReservationDto
     */
    public static ReservationDto ReservationToReservationDto(com.zenika.meetingplanner.model.Reservation reservation) {
        return ReservationDto.builder()
                .id(reservation.getId())
                .date(reservation.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .startTime(reservation.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .endTime(reservation.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .type(reservation.getType())
                .reservedBy(reservation.getReservedBy())
                .room(RoomToRoomDto(reservation.getRoom()))
                .build();
    }

    /**
     * Convert ReservationDto to Reservation
     * @param reservationDto The ReservationDto to convert
     * @return The Reservation
     */
    public static Reservation ReservationDtoToReservation(ReservationDto reservationDto) {
        return Reservation.builder()
                .id(reservationDto.getId())
                .date(java.time.LocalDate.parse(reservationDto.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .startTime(java.time.LocalTime.parse(reservationDto.getStartTime(), DateTimeFormatter.ofPattern("HH:mm")))
                .endTime(java.time.LocalTime.parse(reservationDto.getEndTime(), DateTimeFormatter.ofPattern("HH:mm")))
                .type(reservationDto.getType())
                .reservedBy(reservationDto.getReservedBy())
                .room(RoomDtoToRoom(reservationDto.getRoom()))
                .build();
    }


}
