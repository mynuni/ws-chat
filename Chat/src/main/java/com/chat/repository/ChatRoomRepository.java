package com.chat.repository;

import java.util.List;

import com.chat.domain.ChatRoom;

public interface ChatRoomRepository {

	public List<ChatRoom> getChatRoomList();

	public void createChatRoom(ChatRoom chatRoom);
	
	public int getLastRoomNum();
	
	public ChatRoom getChatRoom(int roomId);
	
	public void deleteChatRoom(int roomId);

}
