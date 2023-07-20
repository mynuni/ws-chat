package com.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chat.domain.ChatRoom;
import com.chat.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;

	public List<ChatRoom> getChatRoomList() {
		return chatRoomRepository.getChatRoomList();
	}

	public void createChatRoom(ChatRoom chatRoom) {
		chatRoomRepository.createChatRoom(chatRoom);
	}

}
