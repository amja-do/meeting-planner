package com.zenika.meetingplanner.service;

import com.zenika.meetingplanner.model.Equipment;
import com.zenika.meetingplanner.model.Reservation;
import com.zenika.meetingplanner.model.Room;
import com.zenika.meetingplanner.repository.RoomRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;


    @ParameterizedTest
    @MethodSource("provideArgumentsForHasRequiredEquipment")
    public void testHasRequiredEquipment(Room room, String type, boolean expected){
        List<String> requiredEquipments = roomService.getRequiredEquipment(type);
        boolean result = roomService.hasRequiredEquipment(room.getEquipments(),requiredEquipments);
        Assertions.assertEquals(expected, result);
    }

    private static Stream<Arguments> provideArgumentsForHasRequiredEquipment() {

        Equipment ecran = Equipment.builder().id(1L).name("ecran").build();
        Equipment webcam = Equipment.builder().id(2L).name("webcam").build();
        Equipment tableau = Equipment.builder().id(3L).name("tableau").build();
        Equipment pieuvre = Equipment.builder().id(4L).name("pieuvre").build();
        
        Set<Equipment> equipments1 = new HashSet<>(Arrays.asList(ecran, webcam, tableau));
        Set<Equipment> equipments2 = new HashSet<>();
        Set<Equipment> equipments3 = new HashSet<>(List.of(tableau));
        Set<Equipment> equipments4 = new HashSet<>(Arrays.asList(ecran, webcam, pieuvre));
        
        Room room1, room2, room3, room4;
        room1 = Room.builder().equipments(equipments1).build();
        room2 = Room.builder().equipments(equipments2).build();
        room3 = Room.builder().equipments(equipments3).build();
        room4 = Room.builder().equipments(equipments4).build();
        return Stream.of(
                Arguments.of(room1, "VC", false),
                Arguments.of(room1, "SPEC", true),
                Arguments.of(room3, "SPEC", true),
                Arguments.of(room2, "SPEC", false),
                Arguments.of(room2, "RS", true),
                Arguments.of(room2, "VC", false),
                Arguments.of(room2, "RC", false),
                Arguments.of(room4, "VC", true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForCheckRoomCapacity")
    public void testCheckRoomCapacity(Room room, int attendees, boolean expected){
        boolean result = roomService.checkRoomCapacity(room.getMaxCapacity(), attendees);
        Assertions.assertEquals(expected, result);
    }

    private static Stream<Arguments> provideArgumentsForCheckRoomCapacity(){
        Room room = Room.builder().maxCapacity(10).build();
        return Stream.of(
                Arguments.of(room, 6, true),
                Arguments.of(room, 7, true),
                Arguments.of(room, 8, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForIsRoomAvailable")
    public void testIsRoomAvailable(Room room, LocalDate date, LocalTime startTime, boolean expected){
        boolean result = roomService.isRoomAvailable(room, date, startTime);
        Assertions.assertEquals(expected, result);
    }

    private static Stream<Arguments> provideArgumentsForIsRoomAvailable(){
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation1 = Reservation
                .builder()
                .date(LocalDate.of(2024, 5, 27))
                .startTime(LocalTime.of(8,0))
                .build();
        reservations.add(reservation1);
        Reservation reservation2 = Reservation
                .builder()
                .date(LocalDate.of(2024, 5, 27))
                .startTime(LocalTime.of(11,0))
                .build();
        reservations.add(reservation2);
        Room room = Room.builder().reservations(reservations).build();
        return Stream.of(
                Arguments.of(room,
                             LocalDate.of(2024, 5, 27),
                             LocalTime.of(8,0),
                             false
                ),
                Arguments.of(room,
                        LocalDate.of(2024, 5, 27),
                        LocalTime.of(9,0),
                        false
                ),
                Arguments.of(room,
                        LocalDate.of(2024, 5, 27),
                        LocalTime.of(10,0),
                        false
                ),
                Arguments.of(room,
                        LocalDate.of(2024, 5, 27),
                        LocalTime.of(11,0),
                        false
                ),
                Arguments.of(room,
                        LocalDate.of(2024, 5, 27),
                        LocalTime.of(12,0),
                        false
                ),
                Arguments.of(room,
                        LocalDate.of(2024, 5, 27),
                        LocalTime.of(13,0),
                        true
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForGetAvailableRooms")
    public void testGetAvailableRooms(List<Room> rooms, int attendees, String type, LocalDate date, LocalTime startTime, int expected){
        when(roomRepository.findAll()).thenReturn(rooms);
        List<Room> result = roomService.getAvailableRooms(attendees, type, date, startTime);
        Assertions.assertEquals(expected, result.size());

    }

    private static Stream<Arguments> provideArgumentsForGetAvailableRooms(){

        Equipment ecran = Equipment.builder().id(1L).name("ecran").build();
        Equipment webcam = Equipment.builder().id(2L).name("webcam").build();
        Equipment tableau = Equipment.builder().id(3L).name("tableau").build();
        Equipment pieuvre = Equipment.builder().id(4L).name("pieuvre").build();
        
        Set<Equipment> equipments1 = new HashSet<>(Arrays.asList(ecran, webcam, tableau));
        Set<Equipment> equipments2 = new HashSet<>();
        Set<Equipment> equipments3 = new HashSet<>(List.of(tableau));
        Set<Equipment> equipments4 = new HashSet<>(Arrays.asList(ecran, webcam, pieuvre, tableau));
        
        Room room1, room2, room3, room4;
        List<Room> rooms = new ArrayList<>();
        room1 = Room.builder().maxCapacity(10).equipments(equipments1).build();
        room2 = Room.builder().maxCapacity(7).equipments(equipments2).build();
        room3 = Room.builder().maxCapacity(15).equipments(equipments3).build();
        room4 = Room.builder().maxCapacity(20).equipments(equipments4).build();
        room4.setReservations(new ArrayList<>(Arrays.asList(
            Reservation.builder().date(LocalDate.of(2024, 5, 27)).startTime(LocalTime.of(8,0)).build(),
            Reservation.builder().date(LocalDate.of(2024, 5, 27)).startTime(LocalTime.of(11,0)).build()
            )));
        rooms.add(room1);
        rooms.add(room2);
        rooms.add(room3);
        rooms.add(room4);
        return Stream.of(
                Arguments.of(rooms, 7, "VC", LocalDate.of(2024, 5, 27), LocalTime.of(8,0), 0),
                Arguments.of(rooms, 12, "SPEC", LocalDate.of(2024, 5, 27), LocalTime.of(8,0), 0),
                Arguments.of(rooms, 8, "RS", LocalDate.of(2024, 5, 27), LocalTime.of(8,0), 1),
                Arguments.of(rooms, 9, "RC", LocalDate.of(2024, 5, 27), LocalTime.of(10,0), 0)
        );
    }

    

}