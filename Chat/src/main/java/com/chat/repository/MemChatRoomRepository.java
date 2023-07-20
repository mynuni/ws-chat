package com.chat.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.chat.domain.ChatRoom;

@Repository
public class MemChatRoomRepository {

	List<ChatRoom> chatRoomList = new ArrayList<>();
	private static int nextRoomId;

	public List<ChatRoom> getChatRoomList() {
		return chatRoomList;
	}

	public void createChatRoom(ChatRoom chatRoom) {
		chatRoom.setRoomId(++nextRoomId);
		chatRoomList.add(chatRoom);
	}

}
