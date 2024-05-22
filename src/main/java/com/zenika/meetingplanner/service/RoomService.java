package com.zenika.meetingplanner.service;

import com.zenika.meetingplanner.model.Equipment;
import com.zenika.meetingplanner.model.Room;
import com.zenika.meetingplanner.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }


    /**
     * Get the best fitting room for a meeting
     * @param attendees The number of attendees
     * @param type The type of the meeting
     * @param date The date of the meeting
     * @param startTime The start time of the meeting
     * @return The best fitting room
     */
    public Room getBestFittingRoom(int attendees, String type, LocalDate date, LocalTime startTime) {
        List<Room> availableRooms = getAvailableRooms(attendees, type, date, startTime);
        return availableRooms.stream().min(Comparator.comparingInt(r -> r.getEquipments().size())).orElse(null);
    }

    /**
     * Get all available rooms for a meeting
     * @param attendees The number of attendees
     * @param type The type of the meeting
     * @param date The date of the meeting
     * @param startTime The start time of the meeting
     * @return List of available rooms
     */
    public List<Room> getAvailableRooms(int attendees, String type, LocalDate date, LocalTime startTime) {
        List<Room> rooms = roomRepository.findAll();

        rooms = rooms.stream()
                .filter(room -> checkRoomCapacity(room.getMaxCapacity(), attendees))
                .filter(room -> hasRequiredEquipment(room.getEquipments(), getRequiredEquipment(type)))
                .filter(room -> isRoomAvailable(room, date, startTime))
                .toList();

        for (Room room : rooms) {
            if (room.getReservations() != null && room.getReservations().stream()
                    .anyMatch(reservation -> reservation.getDate().equals(date)
                            && reservation.getStartTime().equals(startTime))) {
                rooms.remove(room);

            }
        }

        return rooms;
    }

    /**
     * Check if the room capacity is enough for the meeting
     * @param roomCapacity The capacity of the room
     * @param attendees The number of attendees
     * @return True if the room capacity is enough, false otherwise
     */
    public boolean checkRoomCapacity(int roomCapacity, int attendees){
        return roomCapacity * 0.7 >= attendees;
    }


    /**
     * Get the required equipments based on a meeting type
     * @param type The type of the meeting
     * @return List of required equipments
     */
    public List<String> getRequiredEquipment(String type) {
        switch (type) {
            case "VC":
                return List.of("ecran", "pieuvre", "webcam");
            case "SPEC":
                return List.of("tableau");
            case "RS":
                return List.of();
            case "RC":
                return List.of("tableau", "ecran", "pieuvre");
            default:
                return List.of();
        }
    }


    /**
     * Check if the room has the required equipments
     * @param roomEquipments The equipments of the room
     * @param requiredEquipments The required equipments
     * @return True if the room has the required equipments, false otherwise
     */
    public boolean hasRequiredEquipment(Set<Equipment> roomEquipments, List<String> requiredEquipments) {
        Set<String> roomEquipmentNames = roomEquipments.stream()
                .map(Equipment::getName)
                .collect(Collectors.toSet());

        return roomEquipmentNames.containsAll(requiredEquipments);
    }


    /**
     * Check if the room is available for a meeting
     * @param room The room to check
     * @param date The date of the meeting
     * @param startTime The start time of the meeting
     * @return True if the room is available, false otherwise
     */
    public boolean isRoomAvailable(Room room, LocalDate date, LocalTime startTime) {
        LocalTime endTime = startTime.plusHours(1);

        if(room.getReservations() == null) {
            return true;
        }
        return room.getReservations().stream()
                .noneMatch(reservation -> {
                    LocalDate reservationDate = reservation.getDate();
                    LocalTime reservationStartTime = reservation.getStartTime();
                    LocalTime roomFreeAfter = reservationStartTime.plusHours(2);
                    LocalTime roomFreeBefore = reservationStartTime.minusHours(1);
                    
                    return reservationDate.equals(date) &&
                        (
                            (startTime.isBefore(roomFreeAfter) && endTime.isAfter(reservationStartTime)) || 
                            (startTime.isBefore(reservationStartTime) && endTime.isAfter(roomFreeBefore))
                        );
                    });
    }
    
}
