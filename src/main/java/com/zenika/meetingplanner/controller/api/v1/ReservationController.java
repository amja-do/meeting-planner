package com.zenika.meetingplanner.controller.api.v1;

import com.zenika.meetingplanner.dto.ReservationDto;
import com.zenika.meetingplanner.helper.Mapper;
import com.zenika.meetingplanner.model.Reservation;
import com.zenika.meetingplanner.model.Room;
import com.zenika.meetingplanner.request.StoreReservationRequest;
import com.zenika.meetingplanner.service.ReservationService;
import com.zenika.meetingplanner.service.RoomService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final RoomService roomService;

    public ReservationController(ReservationService reservationService, RoomService roomService) {
        this.reservationService = reservationService;
        this.roomService = roomService;
    }

    /**
     * Get all reservations
     * @return List of ReservationDto if the reservations are found, error message otherwise
     */
    @GetMapping
    public ResponseEntity<?> index() {
        try {
            List<Reservation> reservations = reservationService.findAll();
            List<ReservationDto> reservationDtos = reservations.stream().map(Mapper::ReservationToReservationDto)
                    .toList();
            return ResponseEntity.ok(reservationDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Store a new reservation
     * @param request The request containing the reservation data
     * @return ReservationDto if the reservation is stored successfully, error message otherwise
     */
    @PostMapping
    public ResponseEntity<?> store(@RequestBody StoreReservationRequest request) {
        try {
            if (request == null)
                return ResponseEntity.badRequest().body("Requête invalide");

            if (!List.of("VC", "SPEC", "RC", "RS").contains(request.getType().toUpperCase()))
                return ResponseEntity.badRequest().body("Type de réunion invalide");

            if (!isDateAndTimeAfterNow(request.getDate(), request.getStartTime()))
                return ResponseEntity.badRequest()
                        .body("La date et l'heure de début doivent être ultérieures à la date et l'heure actuelles");

            if (isWeekend(request.getDate()))
                return ResponseEntity.badRequest().body("Impossible de réserver le week-end");

            if (!isTimeInFullHourStyle(request.getStartTime()))
                return ResponseEntity.badRequest().body("L'heure de début doit être au format d'heure pleine");

            if (!isTimeBetween(8, 20, request.getStartTime()))
                return ResponseEntity.badRequest().body("Les réservations ne sont autorisées qu'entre 8h00 et 20h00");

            Room bestFittingRoom = roomService.getBestFittingRoom(request.getAttendees(), request.getType(),
                    request.getDate(), request.getStartTime());
            if (bestFittingRoom == null)
                return ResponseEntity.status(404).body("Aucune salle disponible pour cette réservation");

            Reservation reservation = Reservation.builder()
                    .date(request.getDate())
                    .startTime(request.getStartTime())
                    .endTime(request.getStartTime().plusHours(1))
                    .type(request.getType())
                    .reservedBy(request.getReservedBy())
                    .room(bestFittingRoom)
                    .build();

            Reservation savedReservation = reservationService.save(reservation);
            ReservationDto savedReservationDto = Mapper.ReservationToReservationDto(savedReservation);

            return ResponseEntity.status(201).body(savedReservationDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    /**
     * Check if the given date is a weekend
     * @param date The date to check
     * @return true if the given date is a weekend, false otherwise
     */
    private boolean isWeekend(LocalDate date) {
        if (date == null)
            return false;
        return date.getDayOfWeek().getValue() >= 6;
    }

    /**
     * Check if the given time is between the given start and end
     * @param start The start time
     * @param end The end time
     * @param target The time to check
     * @return true if the given time is between the given start and end, false otherwise
     */
    private boolean isTimeBetween(int start, int end, LocalTime target) {
        if (target == null)
            return false;
        return target.getHour() >= start && target.getHour() <= end;
    }

    /**
     * Check if the given time is in full hour style
     * @param time The time to check
     * @return true if the given time is in full hour style, false otherwise
     */
    private boolean isTimeInFullHourStyle(LocalTime time) {
        return time.getMinute() == 0;
    }

    /**
     *  Check if the given date and time are after the current date and time
     * 
     *  @param date The date to check
     *  @param time The time to check
     *  @return true if the given date and time are after the current date and time, false otherwise
     */
    private boolean isDateAndTimeAfterNow(LocalDate date, LocalTime time) {
        return date.isAfter(LocalDate.now()) || (date.isEqual(LocalDate.now()) && time.isAfter(LocalTime.now()));
    }

}
