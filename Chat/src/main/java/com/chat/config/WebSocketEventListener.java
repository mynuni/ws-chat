package com.chat.config;

import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.chat.domain.ChatMessage;
import com.chat.domain.ChatRoom;
import com.chat.service.ChatRoomService;
import com.chat.service.ParticipantManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketEventListener {

	private final SimpMessageSendingOperations messagingTemplate;
	private final ChatRoomService chatRoomService;
	private final ParticipantManager participantManager;

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		log.info("새로운 연결 SessionID = {}", headerAccessor.getSessionId());
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String username = (String) headerAccessor.getSessionAttributes().get("username");
		Long roomId = (Long) headerAccessor.getSessionAttributes().get("roomId");
		
		if (username != null && roomId != null) {
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setType(ChatMessage.MessageType.LEAVE);
			chatMessage.setSender(username);
			messagingTemplate.convertAndSend("/topic/" + roomId, chatMessage);
			log.info("연결 종료 SessionID = {}", headerAccessor.getSessionId());
			
	        participantManager.removeParticipant(roomId, username);
	        Set<String> participants = participantManager.getParticipants(roomId);
	        participantManager.sendParticipantsUpdate(roomId, participants);

			// 참여자 수 감소
			ChatRoom chatRoom = chatRoomService.getChatRoom(roomId);
			if (chatRoom != null) {
				chatRoom.setVisitorCount(chatRoom.getVisitorCount() - 1);
				chatRoomService.updateChatRoom(chatRoom);

				// 아무도 없을 경우 방 삭제
				if (chatRoom.getVisitorCount() == 0) {
					chatRoomService.deleteChatRoom(roomId);
				}
			}

		}
	}

}