package com.zenika.meetingplanner.helper;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonReader {
    
    /**
	 * Read the json file (rooms data) and return a list of type JsonNode
	 * @return List of JsonNode
	 * @throws IOException
	 */
	public static JsonNode read(String name) throws IOException {
		ClassPathResource resource = new ClassPathResource(name + ".json");

		ObjectMapper objectMapper = new ObjectMapper();

		return objectMapper.readTree(resource.getInputStream());
	}
}
