package com.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chat.domain.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	public List<ChatRoom> findAll();

	@Query("SELECT MAX(c.roomId) FROM ChatRoom c")
	public Integer getLastRoomNum();
	
}