package com.chat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

	private final ChatRoomService chatRoomService;

	// 채팅방 목록
	@GetMapping
	public String getChatRoomList(Model model) {
		List<ChatRoom> chatRoomList = chatRoomService.getChatRoomList();
		model.addAttribute("chatRoomList", chatRoomList);
		return "chatRoomList";
	}

	// 채팅방 생성
//	@PostMapping
//	public String createChatRoom(ChatRoom chatRoom) {
//		chatRoomService.createChatRoom(chatRoom);
//		return "redirect:/chat/room/" + chatRoom.getRoomId();
//	}
	
	@PostMapping
	@ResponseBody
	public ResponseEntity<Map<String, Integer>> createChatRoom(@RequestParam String chatRoomName) {
	    ChatRoom chatRoom = new ChatRoom();
	    chatRoom.setChatRoomName(chatRoomName);
	    chatRoomService.createChatRoom(chatRoom);

	    // 생성된 방 번호를 JSON 형태로 응답
	    Map<String, Integer> response = new HashMap<>();
	    response.put("roomId", chatRoom.getRoomId());
	    return ResponseEntity.ok(response);
	}

	// 채팅방 입장
	@GetMapping("/{roomId}")
	public String enterChatRoom(@PathVariable int roomId) {
		return "chatRoom";
	}
	
	@GetMapping("/new")
	public String getChatRoomForm() {
		int newRoomNum = chatRoomService.getNewRoomNum();
		return "redirect:/chat/" + newRoomNum;
	}

	@MessageMapping("/chat.addUser/{roomId}")
	@SendTo("/topic/{roomId}")
	public ChatMessage addUser(@Payload ChatMessage chatMessage, @DestinationVariable int roomId,
			SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		headerAccessor.getSessionAttributes().put("roomId", roomId);
		
		ChatRoom chatRoom = chatRoomService.getChatRoom(roomId);
        if (chatRoom != null) {
            chatRoom.setVisitorCount(chatRoom.getVisitorCount() + 1);
        }
        
		return chatMessage;
	}

	@MessageMapping("/chat.sendMessage/{roomId}")
	@SendTo("/topic/{roomId}")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage, @DestinationVariable int roomId) {
		return chatMessage;
	}

}