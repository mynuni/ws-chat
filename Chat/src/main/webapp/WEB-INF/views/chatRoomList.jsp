<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="https://kit.fontawesome.com/5314583eb7.js" crossorigin="anonymous"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<link href="/resources/chatRoomList.css" rel="stylesheet" type="text/css">
<title>채팅방 목록</title>
</head>
<body>
	<div id="list-container">
		<div id="list-title-wrap">
			<div id="list-title">채팅방 목록</div>
		</div>
		<div id="create-btn-wrap">
			<button id="create-chat-room-btn" type="submit">채팅방 만들기</button>
		</div>
		<div id="room-list-container">
			<ul>
				<c:forEach var="chatRoom" items="${chatRoomList}">
					<li>
						<div class="room-number-wrap">
							<div class="room-number">${chatRoom.visitorCount}명</div>
						</div>
						<div class="room-name-wrap">
							<div class="room-name">${chatRoom.chatRoomName}</div>
						</div>
						<div class="enter-icon-wrap">
							<a href="/chat/${chatRoom.roomId}"><i class="fa-solid fa-arrow-right-to-bracket"></i></a>
						</div>
					</li>
				</c:forEach>
			</ul>
		</div>
	</div>
	<div id="create-chat-room-modal" class="modal">
		<div class="modal-content">
			<input type="text" id="modal-chat-room-name" placeholder="채팅방 이름을 입력해주세요.">
			<button id="modal-create-chat-room-btn">생성</button>
			<button class="close" id="close-modal">취소</button>
		</div>
	</div>
	<script>
	$(document).ready(function() {
		// 모달창 열기
		$("#create-chat-room-btn").click(function() {
			$("#create-chat-room-modal").css("display", "block");
		});

		// 모달창 닫기
		$("#close-modal").click(function() {
			$("#create-chat-room-modal").css("display", "none");
		});

		// 방제 입력 후 POST 전송
		$("#modal-create-chat-room-btn").click(function() {
			const chatRoomName = $("#modal-chat-room-name").val();

			$.ajax({
				type : "POST",
				url : "/chat",
				data : {
					chatRoomName : chatRoomName
				},
				dataType : "json",
				success : function(response) {
					const newRoomNum = response.roomId;
					window.location.href = "/chat/" + newRoomNum;
				},
				error : function() {
					console.error("채팅방 생성 실패");
				}
			});

			$("#create-chat-room-modal").css("display", "none");
		});
	});
	</script>
</body>
</html>