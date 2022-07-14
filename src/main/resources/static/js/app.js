var stompClient = null;

var to = null;
var from = null;
var topicUrl = null;

function con(to, from) {
    this.to=to;
    this.from=from;
    var arr=[to, from];
    arr.sort();
    topicUrl = arr[0]+'-'+arr[1];
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/'+ topicUrl, function (greeting) {
            showGreeting(JSON.parse(greeting.body));
        });
    });
};

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function sendName() {
    var url = document.location.pathname;
    const message = {
            from: from,
            to: to,
            text: $("#name").val(),
            date: new Date(),
     };
    stompClient.send("/app" + url, {}, JSON.stringify(message));
}

function showGreeting(message) {
    if(message.userFilename!=null){
        $("#greetings").append("<tr><td><img width='50px' height='50px' style='object-fit: cover;'></td><td>" + message.from + "</td><td width = '80%'>" + message.text + "</td></tr>");
        let img = document.querySelector("#greetings > tr:last-child > td:nth-child(1) > img");
        img.src='/img/' + message.userFilename;
    }
    else{
         $("#greetings").append("<tr><td><img id='img' src='/img/default.jpg' width='50px' height='50px' style='object-fit: cover;'></td><td>" + message.from + "</td><td width = '80%'>" + message.text + "</td></tr>");
    }
    document.getElementById('name').value = '';
    document.scrollingElement.scrollTop = document.scrollingElement.scrollHeight
    if(message.from !== from){
         let audio = new Audio('/static/audio/new_msg_received.mp3');
         audio.play();
    }
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});