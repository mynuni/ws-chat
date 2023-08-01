package com.chat.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.chat.domain.ChatMessage;
import com.chat.domain.ChatRoom;
import com.chat.repository.ChatMessageRepository;
import com.chat.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;
	private final ChatMessageRepository chatMessageRepository;

	public List<ChatRoom> getChatRoomList() {
		return chatRoomRepository.findAll();
	}

	public void createChatRoom(ChatRoom chatRoom) {
		chatRoomRepository.save(chatRoom);
	}

	public int generateNewRoomNum() {
		return chatRoomRepository.getLastRoomNum() + 1;
	}

	public ChatRoom getChatRoom(Long roomId) {
		return chatRoomRepository.findById(roomId)
				.orElseThrow(() -> new NoSuchElementException("NO ROOM FOUND ID:" + roomId));
	}

	public void saveChatMessage(ChatMessage chatMessage, Long roomId) {
		ChatRoom chatRoom = chatRoomRepository.findById(roomId)
				.orElseThrow(() -> new NoSuchElementException("NO ROOM FOUND ID:" + roomId));
		chatMessage.setChatRoom(chatRoom);
		chatMessageRepository.save(chatMessage);
	}
	
	public void increaseVisitorCount(Long roomId) {
		ChatRoom chatRoom = chatRoomRepository.findById(roomId)
				.orElseThrow(() -> new NoSuchElementException("NO ROOM FOUND ID: " + roomId));
		chatRoom.setVisitorCount(chatRoom.getVisitorCount() + 1);
		updateChatRoom(chatRoom);
		log.info("참가인원++");
		log.info("ROOM ID:{}, COUNT:{}", chatRoom.getRoomId(), chatRoom.getVisitorCount());
	}

	public void updateChatRoom(ChatRoom chatRoom) {
		chatRoomRepository.save(chatRoom);
	}

	public void deleteChatRoom(Long roomId) {
		chatRoomRepository.deleteById(roomId);
	}

}
