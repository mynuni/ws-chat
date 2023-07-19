package com.chat.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chat.domain.ChatMessage;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

	// 채팅방 입장
	@GetMapping("/{roomId}")
	public String enterChatRoom(@PathVariable int roomId) {
		return "chatRoom";
	}

	@MessageMapping("/chat.addUser/{roomId}")
	@SendTo("/topic/{roomId}")
	public ChatMessage addUser(@Payload ChatMessage chatMessage, @DestinationVariable int roomId,
			SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		headerAccessor.getSessionAttributes().put("roomId", roomId);
		return chatMessage;
	}

	@MessageMapping("/chat.sendMessage/{roomId}")
	@SendTo("/topic/{roomId}")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage, @DestinationVariable int roomId) {
		return chatMessage;
	}

}