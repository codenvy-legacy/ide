var websocket = new WebSocket((('https:' == window.location.protocol) ? 'wss:' : 'ws:') + '//' + window.location.host + '/ide/websocket/' + ws);
var messages = [];

websocket.onopen = function () {
    for (var message in messages)
        sendMessage(message);
    messages = [];
};

websocket.onclose = function () {
    websocket.open();
}

websocket.onerror = function (err) {
    if (window.console) {
        window.console.error(err);
    }
}

function sendSessionStatus(appName, sessionId, status) {
    var nVer = navigator.appVersion;
    var nAgt = navigator.userAgent;
    var browserName = navigator.appName;
    var fullVersion = '' + parseFloat(navigator.appVersion);
    var nameOffset, verOffset, ix;

    if ((verOffset = nAgt.indexOf("MSIE")) != -1) {
        browserName = "Microsoft Internet Explorer";
        fullVersion = nAgt.substring(verOffset + 5);
    }
    else if ((verOffset = nAgt.indexOf("Chrome")) != -1) {
        browserName = "Chrome";
        fullVersion = nAgt.substring(verOffset + 7);
    }
    else if ((verOffset = nAgt.indexOf("Safari")) != -1) {
        browserName = "Safari";
        fullVersion = nAgt.substring(verOffset + 7);
        if ((verOffset = nAgt.indexOf("Version")) != -1)
            fullVersion = nAgt.substring(verOffset + 8);
    }
    else if ((verOffset = nAgt.indexOf("Firefox")) != -1) {
        browserName = "Firefox";
        fullVersion = nAgt.substring(verOffset + 8);
    }
    else if ((nameOffset = nAgt.lastIndexOf(' ') + 1) <
        (verOffset = nAgt.lastIndexOf('/'))) {
        browserName = nAgt.substring(nameOffset, verOffset);
        fullVersion = nAgt.substring(verOffset + 1);
        if (browserName.toLowerCase() == browserName.toUpperCase()) {
            browserName = navigator.appName;
        }
    }
    if ((ix = fullVersion.indexOf(";")) != -1)
        fullVersion = fullVersion.substring(0, ix);
    if ((ix = fullVersion.indexOf(" ")) != -1)
        fullVersion = fullVersion.substring(0, ix);

    var browserInfo = browserName + '/' + fullVersion;
    var url = ws + '/session/' + appName + '/' + status;
    var mes = {body: JSON.stringify({sessionId: sessionId, browserInfo: browserInfo}), method: "POST", path: url, uuid: generate(), "headers": [
        {name: "content-type", value: "application/json"}
    ]};

    if (websocket.readyState == WebSocket.OPEN) {
        sendMessage(mes);
    } else {
        messages[messages.length] = mes;
    }
}

function sendMessage(message) {
    try {
        websocket.send(JSON.stringify(message));
    }
    catch (err) {
        if (window.console) {
            window.console.error(err);
        }
    }
}

function generate() {
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4()
        + s4() + s4();
}

function s4() {
    return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
}