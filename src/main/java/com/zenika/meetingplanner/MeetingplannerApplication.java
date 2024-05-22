package com.zenika.meetingplanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.meetingplanner.model.Equipment;
import com.zenika.meetingplanner.model.Room;
import com.zenika.meetingplanner.repository.EquipmentRepository;
import com.zenika.meetingplanner.repository.RoomRepository;

import jakarta.persistence.EntityManager;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class MeetingplannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeetingplannerApplication.class, args);
	}


	/**
	 * Load the data from the json file and save it to the database
	 * @param equipmentRepository The equipment repository
	 * @param roomRepository The room repository
	 * @param entityManager The entity manager
	 * @return CommandLineRunner
	 */
	@Bean
	CommandLineRunner commandLineRunner(EquipmentRepository equipmentRepository, RoomRepository roomRepository, EntityManager entityManager){
		return args -> {
			Set<Equipment> availableEquipments = new HashSet<Equipment>();
			for(String item : new String[]{"ecran", "webcam", "tableau", "pieuvre"}){
				Equipment savedEquipment = equipmentRepository.save(Equipment.builder().name(item).build());
				availableEquipments.add(savedEquipment);
			}

			for(JsonNode jsonNode : readJsonFile()){
				List<String> extractedEquipments = extractEquipments(jsonNode.get("equipments"));
				Set<Equipment> matchingEquipments = getMatchingEquipments(availableEquipments, extractedEquipments);
				Room room = Room.builder()
						.name(jsonNode.get("name").asText())
						.maxCapacity(jsonNode.get("capacity").asInt())
						.equipments(matchingEquipments)
						.build();
				roomRepository.save(room);
			}

		};
	}


	/**
	 * Read the json file (rooms data) and return a list of type JsonNode
	 * @return List of JsonNode
	 * @throws IOException
	 */
	public JsonNode readJsonFile() throws IOException {
		ClassPathResource resource = new ClassPathResource("data.json");

		ObjectMapper objectMapper = new ObjectMapper();

		return objectMapper.readTree(resource.getInputStream());
	}


	/**
	 * Extract the equipments from a JsonNode
	 * @param equipments The JsonNode containing the equipments
	 * @return List of equipments
	 */
	public List<String> extractEquipments(JsonNode equipments){
		if(equipments == null) return null;
		List<String> extractedEquipments = new ArrayList<String>();
		for(JsonNode item : equipments){
			extractedEquipments.add(item.asText().toLowerCase());
		}
		return extractedEquipments;
	}


	/**
	 * Get the matching equipments from a list of equipments
	 * @param availableEquipments The available equipments
	 * @param equipments The list of equipments names
	 * @return Set of matching equipments
	 */
	public Set<Equipment> getMatchingEquipments(Set<Equipment> availableEquipments, List<String> equipments){
		if(equipments == null) return null;
		Set<Equipment> matchingEquipments = new HashSet<Equipment>();
		for(String item : equipments){
			for(Equipment equipment : availableEquipments){
				if(equipment.getName().equals(item)){
					matchingEquipments.add(equipment);
				}
			}
		}
		System.out.println(matchingEquipments.size());
		return matchingEquipments;
	}



}
