function sendSessionStatus(appName, sessionId, status, browserInfo) {
    var url = '//' + window.location.host + '/ide/rest/' + ws + '/session/'
        + appName + '/' + status + '?sessionId=' + sessionId
        + '&browserInfo=' + browserInfo;
    var websocket = new WebSocket('https:' == window.location.protocol ? 'wss:'
        : 'ws:' + url);
    websocket.send(appName);
}

function generate() {
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4()
        + s4() + s4();
}

function s4() {
    return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
}