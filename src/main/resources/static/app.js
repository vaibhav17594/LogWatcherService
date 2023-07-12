var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#logs").html("");
}

function connect() {
    var socket = new SockJS('/log-watcher-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
//        const sessionId = stompClient.ws._transport.url.split('/')[4];
        const destination = '/user/queue/logs';
        stompClient.subscribe(destination, function (log) {
                    var logData = JSON.parse(log.body);
                    if (Array.isArray(logData)) {
                        // Handle the case where log is a list of logs
                        logData.forEach(function (item) {
                            showLog(item.content);
                        });
                    } else {
                        // Handle the case where log is a single log
                        showLog(logData.content);
                    }
                });
        sendInitialLogsRequest();
        stompClient.subscribe('/topic/logs', function (log) {
            var logData = JSON.parse(log.body);
            if (Array.isArray(logData)) {
                // Handle the case where log is a list of logs
                logData.forEach(function (item) {
                    showLog(item.content);
                });
            } else {
                // Handle the case where log is a single log
                showLog(logData.content);
            }
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showLog(message) {
    $("#logs").append("<tr><td>" + message + "</td></tr>");
}

function sendInitialLogsRequest() {
    stompClient.send("/app/subscribe", {},
    JSON.stringify({
        'fileName': $("#fileName").val()
    }));
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() {
        connect();
    });
    $( "#disconnect" ).click(function() { disconnect(); });
});