/* 
    CODENVY CONFIDENTIAL
    __________________

    [2012] - [2013] Codenvy, S.A.
    All Rights Reserved.

    NOTICE:  All information contained herein is, and remains
    the property of Codenvy S.A. and its suppliers,
    if any.  The intellectual and technical concepts contained
    herein are proprietary to Codenvy S.A.
    and its suppliers and may be covered by U.S. and Foreign Patents,
    patents in process, and are protected by trade secret or copyright law.
    Dissemination of this information or reproduction of this material
    is strictly forbidden unless prior written permission is obtained
    from Codenvy S.A..
*/

if (!window["codenvy-factories"]) {

    window["codenvy-factories"] = new Array();

    window.addEventListener("message", function(event) {
        try {
            // Get message.
            var message = event.data;

            // It must be the request for resize Factory button.
            if (message.indexOf("resize-factory-button:") != 0) {
                return;
            }

            // Parse message and resize Factory Button.
            var parts = message.split(':');

            // resize all, which ID is equal to required
            for (var i = 0; i < window["codenvy-factories"].length; i++) {
                var iframe = window["codenvy-factories"][i];

                if (iframe["factory"] && iframe["factory"] === parts[1] &&
                    iframe["uid"] && iframe["uid"] === parts[2]) {

                    iframe.style.width = "" + parts[3] + "px";
                    iframe.style.height = "" + parts[4] + "px";
                }
            }
        } catch (e) {
            console.log(e.message);
        }
    }, false);


    var uniqueId = null;

    function getUID(prefix) {
        if (!uniqueId) uniqueId = (new Date()).getTime();
        return (prefix || 'id') + (uniqueId++);
    };


    setTimeout(function() {

        function injectFrame(script) {

            var uid = getUID();

            // Fetch Factory button initial params

            var _factory = null;
            if (script.src.indexOf("/factory.js?") >= 0) {
                _factory = script.src.substring(script.src.indexOf('?') + 1);
            }

            var _style = script.getAttribute("style");

            var _url = script.getAttribute("url");

            var _logo = script.getAttribute("logo");

            // Build query string
            var frameQuery = "";

            if (_factory) {
                frameQuery += "&factory=" + _factory;
                frameQuery += "&uid=" + uid;
            }

            if (_style) {
                frameQuery += "&style=" + _style;
            }

            if (_url) {
                frameQuery += "&url=" + _url;
            }

            if (_logo) {
                frameQuery += "&logo=" + _logo;
            }

            // Inject Factory button frame
            if (frameQuery) {
                var frameURL = script.src.substring(0, script.src.lastIndexOf("/")) + "/factory.html?" + frameQuery.substring(1);

                var _frame = document.createElement("iframe");
                _frame.src = frameURL;

                if (_factory) {
                    _frame.factory = _factory;
                    _frame.uid = uid;
                    window["codenvy-factories"].push(_frame);
                }

                if (_style) {
                    switch(_style.toLowerCase()) {
                        case "white":
                        case "dark":
                            _frame.style.width = "77px";
                            _frame.style.height = "21px";
                            break;

                        case "horizontal,white":
                        case "white,horizontal":
                        case "horizontal,dark":
                        case "dark,horizontal":
                            _frame.style.width = "118px";
                            _frame.style.height = "21px";
                            break;

                        case "vertical,white":
                        case "white,vertical":
                        case "vertical,dark":
                        case "dark,vertical":
                            _frame.style.width = "77px";
                            _frame.style.height = "61px";
                            break;

                        case "advanced":
                        case "advanced with counter":
                        case "advanced,counter":
                        case "counter,advanced":
                            _frame.style.width = "112px";
                            _frame.style.height = "113px";
                            break;

                        default:
                            _frame.style.width = "0px";
                            _frame.style.height = "0px";
                    }
                } else {
                    _frame.style.width = "0px";
                    _frame.style.height = "0px";
                }

                // Style attributes
                _frame.style.background = "transparent";
                _frame.style.border = "0px none transparent";
                _frame.style.padding = "0px";
                _frame.style.overflow = "hidden";

                // Properties
                _frame.scrolling = "no";
                _frame.frameborder = "0";
                _frame.allowtransparency = "true"

                setTimeout(function() {
                    script.parentNode.replaceChild(_frame, script);
                }, 10);

            } else {
                console.log("You have an error in your script properties : " + script.outerHTML);
            }
        }

        var scriptPart = "/factory/resources/factory.js";

        var scripts = document.getElementsByTagName('script');
        for (var i = 0; i < scripts.length; i++) {
            var script = scripts.item(i);
            try {
                var src = script.getAttribute("src");
                if (src && src.indexOf(scriptPart) >= 0) {
                    injectFrame(script);
                }
            } catch (e) {
                console.log(e.message);
            }
        }

    }, 1000);

}
