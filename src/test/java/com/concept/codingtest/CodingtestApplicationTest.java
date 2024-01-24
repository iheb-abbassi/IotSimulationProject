package com.concept.codingtest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ScooterAssignmentApplicationTests {

	@Test
	void testUpdateUserScooter() throws IOException {
		String jsonData = loadJsonData("/data.json");
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(jsonData);

		ScooterAssignmentApplication application = new ScooterAssignmentApplication();
		application.updateUserScooter(rootNode.get("users"), "user1", "scooter123");

		assertEquals("scooter123", rootNode.get("users").get(0).get("current_scooter").asText());
	}

	@Test
	void testAssignScooterToUser() throws IOException {
		String jsonData = loadJsonData("/data.json");
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(jsonData);

		ScooterAssignmentApplication application = new ScooterAssignmentApplication();
		application.assignScooterToUser(rootNode.get("users"), "scooter456");

		assertEquals("scooter456", rootNode.get("users").get(0).get("current_scooter").asText());
	}


	private String loadJsonData(String filePath) throws IOException {
		try (InputStream inputStream = getClass().getResourceAsStream(filePath)) {
			if (inputStream == null) {
				throw new IOException("Unable to load JSON data from file: " + filePath);
			}
			return new String(inputStream.readAllBytes());
		}
	}
}