package com.zenika.meetingplanner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.List;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    private String name;

    @Column(name = "max_capacity")
    private int maxCapacity;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinTable(name = "room_equipments_mapping", joinColumns = @JoinColumn(name = "room_name"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id"))
    private Set<Equipment> equipments;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Reservation> reservations;

}
