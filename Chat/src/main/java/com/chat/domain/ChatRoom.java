package com.chat.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {
	private int roomId;
	private String chatRoomName;
	private String socketSession;
	private int visitorCount;

}