package com.zenika.meetingplanner.controller.api.v2;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.zenika.meetingplanner.dto.ReservationDto;
import com.zenika.meetingplanner.helper.JsonReader;
import com.zenika.meetingplanner.helper.Mapper;
import com.zenika.meetingplanner.model.Reservation;
import com.zenika.meetingplanner.model.Room;
import com.zenika.meetingplanner.service.ReservationService;
import com.zenika.meetingplanner.service.RoomService;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;

@RestController
@Component("v2ReservationController")
@RequestMapping("/api/v2/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final RoomService roomService;

    public ReservationController(ReservationService reservationService, RoomService roomService) {
        this.reservationService = reservationService;
        this.roomService = roomService;
    }


    /**
     * get the most suitable room for each provided monday meeting
     * 
     * @return List<ReservationDto> if the reservations are stored successfully, error message otherwise
     */
    @GetMapping()
    public ResponseEntity<?> mondayMeetings() {
        try {
            List<ReservationDto> successfulReservations = new ArrayList<>();
            List<Reservation> reservations = loadMondayReservations();
            for (Reservation reservation : reservations) {
                Room bestFittingRoom = roomService.getBestFittingRoom(reservation.getAttendees(), reservation.getType(),
                        reservation.getDate(), reservation.getStartTime());
                if (bestFittingRoom != null) {
                    reservation.setRoom(bestFittingRoom);
                    Reservation savedReservation = reservationService.save(reservation);
                    bestFittingRoom.getReservations().add(savedReservation);
                    successfulReservations.add(Mapper.ReservationToReservationDto(savedReservation));
                }
            }
            return ResponseEntity.ok(successfulReservations);

        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Erreur lors de la lecture du fichier JSON");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    /**
     * Load the monday meetings from the json file
     * 
     * @return List<Reservation> the list of monday meetings
     * @throws IOException
     */
    private List<Reservation> loadMondayReservations() throws IOException {
        JsonNode jsonNode = JsonReader.read("meetings");
        List<Reservation> reservations = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        for (JsonNode node : jsonNode) {
            LocalTime startTime = LocalTime.parse(node.get("time").asText());
            Reservation reservation = Reservation.builder()
                    .reservationNumber(node.get("meeting").asInt())
                    .date(nextMonday)
                    .startTime(startTime)
                    .endTime(startTime.plusHours(1))
                    .type(node.get("type").asText())
                    .attendees(node.get("attendees").asInt())
                    .build();
            reservations.add(reservation);
        }
        return reservations;
    }

}
