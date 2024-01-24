package com.concept.codingtest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@SpringBootApplication
public class ScooterAssignmentApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(ScooterAssignmentApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ScooterAssignmentApplication.class, args);
	}

	@Override
	public void run(String[] args) {
		// Load JSON data from a file
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			InputStream inputStream = getClass().getResourceAsStream("/data.json");
			JsonNode rootNode = objectMapper.readTree(inputStream);

			processScooters(rootNode);

			logger.info("Final Version of Data:\n{}", objectMapper.writeValueAsString(rootNode));

		} catch (Exception e) {
			logger.error("An error occurred during application execution", e);
		}
	}


	private void processScooters(JsonNode rootNode) {
		JsonNode scootersNode = rootNode.get("scooters");
		JsonNode usersNode = rootNode.get("users");

		List<String> unassignedScooterIds = new ArrayList<>();

		Iterator<JsonNode> scooterIterator = scootersNode.iterator();
		while (scooterIterator.hasNext()) {
			JsonNode scooter = scooterIterator.next();

			boolean assigned = scooter.get("assigned").asBoolean();
			boolean decommissioned = scooter.get("decommissioned").asBoolean();
			String assignedUser = scooter.get("assigned_user").asText();

			// Delete decommissioned scooters
			if (decommissioned) {
				scooterIterator.remove();
				logger.info("Decommissioned scooter removed: {}", scooter.get("id").asText());
			}

			// Assign unassigned scooters to users without scooters
			if (!assigned && !decommissioned) {
				unassignedScooterIds.add(scooter.get("id").asText());
				logger.info("Unassigned scooter found: {}", scooter.get("id").asText());
			}

			// If a scooter is assigned, update the user's current_scooter field
			if (assigned && !assignedUser.isEmpty()) {
				updateUserScooter(usersNode, assignedUser, scooter.get("id").asText());
				logger.info("User {} assigned scooter: {}", assignedUser, scooter.get("id").asText());
			}
		}

		// Assign unassigned scooters to users without scooters
		for (String scooterId : unassignedScooterIds) {
			assignScooterToUser(usersNode, scooterId);
			logger.info("Scooter {} assigned to a user.", scooterId);
		}
	}

	void updateUserScooter(JsonNode usersNode, String userId, String scooterId) {
		for (JsonNode user : usersNode) {
			if (userId.equals(user.get("id").asText())) {
				((com.fasterxml.jackson.databind.node.ObjectNode) user).put("current_scooter", scooterId);
				break;
			}
		}
	}

	void assignScooterToUser(JsonNode usersNode, String scooterId) {
		for (JsonNode user : usersNode) {
			if (user.get("current_scooter").asText().isEmpty()) {
				((com.fasterxml.jackson.databind.node.ObjectNode) user).put("current_scooter", scooterId);
				break;
			}
		}
	}
}