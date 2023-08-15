<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="https://kit.fontawesome.com/5314583eb7.js" crossorigin="anonymous"></script>
<link href="/resources/chatRoom.css" rel="stylesheet" type="text/css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<title>채팅방</title>
</head>
<body>
	<div id="modal" class="modal">
		<div class="modal-content">
			<div class="button-container">
				<input type="text" id="nickname-input" placeholder="닉네임을 입력해주세요.">
				<button type="submit" id="modal-confirm-btn">확인</button>
				<button type="button" id="modal-cancel-btn">취소</button>
			</div>
		</div>
	</div>
	<div id="chat-page" class="hidden">
		<div class="chat-container">
			<div class="chat-header">
				<i id="go-back-btn" class="fa-solid fa-arrow-left"></i>
				<h3>${chatRoom.chatRoomName}</h3>
				<i id="participant-list-btn" class="fa-solid fa-bars"></i>
				<div id="participant-list">
					<ul id="participants">
						<!-- 참가자 명단 영역 -->
					</ul>
				</div>
			</div>
			<ul id="messageArea">
				<!-- 메세지 영역 -->
			</ul>
			<form id="messageForm" name="messageForm">
				<div class="form-group">
					<div class="input-group clearfix">
						<input type="text" id="message" autocomplete="off" class="form-control" />
						<button type="submit" id="message-submit-btn">전송</button>
					</div>
				</div>
			</form>
		</div>
	</div>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.4/sockjs.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
	<script>
		$(function() {
			$("#go-back-btn").click(function () {
			      history.back();
			});
			
			let usernamePage = $('#username-page');
			let chatPage = $('#chat-page');
			let usernameForm = $('#usernameForm');
			let messageForm = $('#messageForm');
			let messageInput = $('#message');
			let messageArea = $('#messageArea');
			let connectingElement = $('.connecting');
			let stompClient = null;
			let username = null;
			let roomId = null;

			function connect(event) {
				username = $("#nickname-input").val().trim();
				if (username !== null && username !== '') {
					const socket = createSocket();
					stompClient = Stomp.over(socket);
					stompClient.connect({}, onConnected, onError);
				}
				event.preventDefault();
			}

			// 소켓 객체 생성
			function createSocket() {
				const socket = new SockJS('/ws');
				return socket;
			}

			function onConnected() {
				roomId = window.location.pathname.split('/').pop();
				console.log("룸 ID:" + roomId);
				stompClient.subscribe('/topic/' + roomId, onMessageReceived);
				
			    stompClient.subscribe('/topic/participants/' + roomId, function(payload) {
			        let participants = JSON.parse(payload.body);
			        updateParticipantList(participants);
			    });
				
				stompClient.send("/app/chat.addUser/" + roomId, {}, JSON
						.stringify({
							sender : username,
							type : 'JOIN'
						}));
				connectingElement.addClass('hidden');
			}

			function onError(error) {
				connectingElement.text('연결 실패');
			}

			// 메세지 전송
			function sendMessage(event) {
				const messageContent = messageInput.val().trim();

				if (messageContent && stompClient) {
					const chatMessage = {
						sender : username,
						content : messageInput.val(),
						type : 'CHAT'
					};
					stompClient.send("/app/chat.sendMessage/" + roomId, {},
							JSON.stringify(chatMessage));
					messageInput.val('');
				}
				event.preventDefault();
			}

			// 닉네임별 랜덤 색상을 위한 변수
			let participantColors = {};

			function onMessageReceived(payload) {
			    var message = JSON.parse(payload.body);

			    var messageElement = $('<li></li>');
			    var textElement = $('<p></p>');

			    if (message.type === 'JOIN') {
			        messageElement.addClass('event-message');
			        textElement.text(message.sender + '님이 입장하셨습니다.');
			        joinAlert();
			    } else if (message.type === 'LEAVE') {
			        messageElement.addClass('event-message');
			        textElement.text(message.sender + '님이 퇴장하셨습니다.');
			    } else {
			        messageElement.addClass('chat-message');
			        
			        var usernameElement = $('<span></span>').text(message.sender);
			        var circleIcon = $('<i class="fa-solid fa-circle-user"></i>');
			        
			        usernameElement.prepend(circleIcon);
			        
			        // 새로운 닉네임에게 랜덤 색상 부여
			        if (!participantColors.hasOwnProperty(message.sender)) {
			        	participantColors[message.sender] = getRandomColor();
			        }
			        
			        circleIcon.css('color', participantColors[message.sender]);
			        
			        messageElement.append(usernameElement);

			        if (message.sender === username) {
			            messageElement.addClass('my-message');
			        } else {
			            messageElement.addClass('other-message');
			        }

			        textElement.text(message.content);
			    }

			    messageElement.append(textElement);

			    messageArea.append(messageElement);
			    messageArea.scrollTop(messageArea[0].scrollHeight);
			}

			// 랜덤 색상 계산
			function getRandomColor() {
			    let colors = ['red', 'orange', 'green', 'blue', 'purple'];
			    let randomIndex = Math.floor(Math.random() * colors.length);
			    return colors[randomIndex];
			}

			function closeModal() {
				$("#modal").hide();
			}

			$("#modal-confirm-btn").click(function(event) {
				event.preventDefault();
				closeModal();
				connect();
			});
			messageForm.on('submit', sendMessage);

			$("#modal").css("display", "block");

			$("#modal-cancel-btn").click(function() {
				history.back();
			});
			
			function updateParticipantList(participants) {
			    let participantsElement = $('#participants');
			    participantsElement.empty();

			    participants.forEach(function(participant) {
			        participantsElement.append('<li><i id="user-list-dot" class="fa-solid fa-minus"></i>' + participant + '</li>');
			    });
			}
			
			$("#participant-list-btn").click(function() {
	            $("#participant-list").toggle();
	        });
			
		});
		
	</script>
	<script src="${pageContext.request.contextPath}/resources/sound/alert.js"></script>
</body>
</html>