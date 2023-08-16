package com.chat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chat.domain.ChatMessage;
import com.chat.domain.ChatRoom;
import com.chat.service.ChatRoomService;
import com.chat.service.ParticipantManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

	private final ChatRoomService chatRoomService;
	private final ParticipantManager participantManager;
	private final SimpMessagingTemplate messagingTemplate;

	@GetMapping
	public String getChatRoomList(Model model) {
		List<ChatRoom> chatRoomList = chatRoomService.getChatRoomList();
		model.addAttribute("chatRoomList", chatRoomList);
		return "chatRoomList";
	}
	
	@PostMapping
	@ResponseBody
	public ResponseEntity<Map<String, Long>> createChatRoom(@RequestParam String chatRoomName) {
	    ChatRoom chatRoom = new ChatRoom();
	    chatRoom.setChatRoomName(chatRoomName);
	    chatRoomService.createChatRoom(chatRoom);

	    Map<String, Long> response = new HashMap<>();
	    response.put("roomId", chatRoom.getRoomId());
	    return ResponseEntity.ok(response);
	}

	@GetMapping("/{roomId}")
	public String enterChatRoom(@PathVariable Long roomId, Model model) {
		ChatRoom chatRoom = chatRoomService.getChatRoom(roomId);
		model.addAttribute("chatRoom", chatRoom);
		return "chatRoom";
	}
	
	@GetMapping("/new")
	public String getChatRoomForm() {
		int newRoomNum = chatRoomService.generateNewRoomNum();
		return "redirect:/chat/" + newRoomNum;
	}

	@MessageMapping("/chat.addUser/{roomId}")
	@SendTo("/topic/{roomId}")
	public ChatMessage addUser(@Payload ChatMessage chatMessage, @DestinationVariable Long roomId, 
			SimpMessageHeaderAccessor headerAccessor) {
		
		headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		headerAccessor.getSessionAttributes().put("roomId", roomId);
		headerAccessor.setDestination("/topic/" + roomId);
		
		participantManager.addParticipant(roomId, chatMessage.getSender());
		Set<String> participants = participantManager.getParticipants(roomId);
		messagingTemplate.convertAndSend("/topic/participants/" + roomId, participants);
		chatRoomService.increaseVisitorCount(roomId);
		return chatMessage;
	}

	@MessageMapping("/chat.sendMessage/{roomId}")
	@SendTo("/topic/{roomId}")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage, @DestinationVariable Long roomId) {
		ChatRoom chatRoom = chatRoomService.getChatRoom(roomId);
		
        if (chatRoom != null) {
            chatRoomService.saveChatMessage(chatMessage, roomId);
        }
        
		return chatMessage;
	}
	
}
