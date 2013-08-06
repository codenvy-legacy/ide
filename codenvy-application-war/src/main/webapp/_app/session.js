function sendSessionStatus(appName, sessionId, status) {
    var url = '//' + window.location.host + '/ide/rest/' + ws + '/session/' + appName + '/' + status + '?sessionId=' + sessionId;
    var websocket = new WebSocket('https:' == window.location.protocol ? 'wss:' : 'ws:' + url);
    websocket.send(appName);
}