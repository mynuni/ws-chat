package com.chat.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisParticipantManager implements ParticipantManager {

	private final RedisTemplate<String, Set<String>> redisTemplate;
	private final SimpMessageSendingOperations messagingTemplate;
	private final static String ROOM_PREFIX = "CHAT_ROOM_";

	@Override
	public Set<String> getParticipants(Long roomId) {
		RedisOperations<String, Set<String>> operations = redisTemplate.opsForSet().getOperations();
		Set<String> participants = new HashSet<>();
		
		for (Set<String> set : operations.opsForSet().members(ROOM_PREFIX + roomId)) {
			participants.addAll(set);
		}
		
		return participants;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addParticipant(Long roomId, String username) {
		redisTemplate.opsForSet().add(ROOM_PREFIX + roomId, Collections.singleton(username));
	}

	@Override
	public void sendParticipantsUpdate(Long roomId, Set<String> participants) {
		messagingTemplate.convertAndSend("/topic/participants/" + roomId, participants);
	}

	@Override
	public void removeParticipant(Long roomId, String username) {
		redisTemplate.opsForSet().remove(ROOM_PREFIX + roomId, username);
	}

}
