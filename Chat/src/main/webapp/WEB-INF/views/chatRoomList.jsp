<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>임시 채팅방 목록</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
</head>
<body>
	<c:forEach var="chatRoom" items="${chatRoomList}">
		<a href="/chat/${chatRoom.roomId}">제목: ${chatRoom.chatRoomName}</a>
	</c:forEach>
</body>
</html>