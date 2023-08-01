package com.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chat.domain.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}
