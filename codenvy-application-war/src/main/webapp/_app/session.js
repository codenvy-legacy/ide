function sendSessionStatus(appName, status) {
    var url = '//' + window.location.host + '/ide/rest/' + ws + '/session/' + appName + '/' + status;
    var websocket = new WebSocket('https:' == window.location.protocol ? 'wss:' : 'ws:' + url);
    websocket.send(appName);
}