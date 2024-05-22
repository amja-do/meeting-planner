package com.zenika.meetingplanner.repository;

import com.zenika.meetingplanner.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
}
