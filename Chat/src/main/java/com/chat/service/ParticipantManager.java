package com.chat.service;

import java.util.Set;

public interface ParticipantManager {

	Set<String> getParticipants(Long roomId);

	void addParticipant(Long roomId, String username);

	void sendParticipantsUpdate(Long roomId, Set<String> participants);

	void removeParticipant(Long roomId, String username);

}