package com.chat.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.chat.domain.ChatRoom;

@Repository
public class MemChatRoomRepository implements ChatRoomRepository {

	private static List<ChatRoom> chatRoomList = new ArrayList<>();
	private static int nextRoomId;

	public List<ChatRoom> getChatRoomList() {
		return chatRoomList;
	}

	public void createChatRoom(ChatRoom chatRoom) {
		chatRoom.setRoomId(++nextRoomId);
		chatRoomList.add(chatRoom);
	}

	public int getLastRoomNum() {
		int size = chatRoomList.size();
		return size;
	}
	
	public ChatRoom getChatRoom(int roomId) {
		for (ChatRoom chatRoom : chatRoomList) {
			if (chatRoom.getRoomId() == roomId) {
				return chatRoom;
			}
		}
		return null;
	}
	
	public void deleteChatRoom(int roomId) {
        chatRoomList.removeIf(chatRoom -> chatRoom.getRoomId() == roomId);
    }

}
