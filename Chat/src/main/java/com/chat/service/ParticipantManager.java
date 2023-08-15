package com.chat.service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ParticipantManager {

	private final ConcurrentHashMap<Long, Set<String>> participants = new ConcurrentHashMap<>();
	private final SimpMessageSendingOperations messagingTemplate;

	public Set<String> getParticipants(Long roomId) {
		return participants.getOrDefault(roomId, new HashSet<>());
	}

	public void addParticipant(Long roomId, String username) {
		participants.computeIfAbsent(roomId, key -> new HashSet<>()).add(username);
	}

	public void sendParticipantsUpdate(Long roomId, Set<String> participants) {
		messagingTemplate.convertAndSend("/topic/participants/" + roomId, participants);
	}

	public void removeParticipant(Long roomId, String username) {
		participants.computeIfPresent(roomId, (key, value) -> {
			value.remove(username);

			if (value.isEmpty()) {
				participants.remove(roomId);
				return null;
			} else {
				return value;
			}
		});
	}

}