<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Chatbot - Job-Specific Resume Suggestions</title>
    <script>
        let socket = new WebSocket("ws://localhost:8082/chatbot");

        socket.onopen = function() {
            console.log("Connected to WebSocket server");
        };

        socket.onmessage = function(event) {
            let botResponse = event.data;
            let chatArea = document.getElementById("chat");
            chatArea.innerHTML += "<b>Bot:</b> " + botResponse + "<br>";
        };

        socket.onerror = function(error) {
            console.error("WebSocket Error:", error);
        };

        function sendMessage() {
            let userMessage = document.getElementById("userInput").value;
            document.getElementById("chat").innerHTML += "<b>You:</b> " + userMessage + "<br>";
            socket.send(userMessage);
            document.getElementById("userInput").value = "";
        }
    </script>
</head>
<body>
    <h2>Resume Chatbot</h2>
    <div id="chat" style="border:1px solid black; padding:10px; height:300px; overflow:auto;"></div>
    <input type="text" id="userInput" placeholder="Type your job description here..." />
    <button onclick="sendMessage()">Send</button>
</body>
</html>
