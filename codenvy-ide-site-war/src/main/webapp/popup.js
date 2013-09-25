/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */

function Popup(authUrl, redirectAfterLogin, popupWindowWidth, popupWindowHeight) {
    this.authUrl = authUrl;
    this.redirectAfterLogin = redirectAfterLogin;
    this.popupWindowWidth = popupWindowWidth;
    this.popupWindowHeight = popupWindowHeight;

    var get_window_position = function () {
        var x = window.screenX;
        var y = window.screenY;
        return [x, y];
    }

    var get_window_size = function () {
        var width = window.innerWidth;
        var height = window.innerHeight;
        return [width, height];
    }

    var popup_close_handler = function () {
        if (!popupWindow || popupWindow.closed) {
            //console.log("closed popup")
            popupWindow = null;
            if (popupCloseHandlerIntervalId) {
                window.clearInterval(popupCloseHandlerIntervalId);
                //console.log("stop interval " + popupCloseHandlerIntervalId);
            }
        }
        else {
            var href;
            try {
                href = popupWindow.location.href;
            }
            catch (error) {
            }

            if (href
                && (popupWindow.location.pathname == redirectAfterLogin
                || popupWindow.location.pathname == "/ide/dev-monit"
                || popupWindow.location.pathname.match("j_security_check$")
                )) {
                //console.log(href);
                popupWindow.close();
                popupWindow = null;
                if (popupCloseHandlerIntervalId) {
                    window.clearInterval(popupCloseHandlerIntervalId);
                    //console.log("stop interval " + popupCloseHandlerIntervalId);
                }
                window.location.replace(href);
            }
        }
    }

    this.open_window = function () {
        var mainWindowPosition = get_window_position();
        var mainWindowSize = get_window_size();
        var x = mainWindowPosition[0] + Math.max(0, Math.round((mainWindowSize[0] - this.popupWindowWidth) / 2));
        var y = mainWindowPosition[1] + Math.max(0, Math.round((mainWindowSize[1] - this.popupWindowHeight) / 2));
        popupWindow = window.open(this.authUrl,
            'popup',
            'width=' + this.popupWindowWidth + ',height=' + this.popupWindowHeight + ',left=' + x + ',top=' + y);
        popupCloseHandlerIntervalId = window.setInterval(popup_close_handler, 50);
    }
}
