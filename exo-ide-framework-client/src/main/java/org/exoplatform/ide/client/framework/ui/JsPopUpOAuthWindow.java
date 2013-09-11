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
package org.exoplatform.ide.client.framework.ui;

import com.google.gwt.user.client.Window;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: JsPopUpWindow.java Sep 4, 2012
 */
public class JsPopUpOAuthWindow {

    public interface JsPopUpOAuthWindowCallback {
        public void oAuthFinished(int authenticationStatus);
    }

    private String authUrl;

    private String errorPageUrl;

    // 0 means that auth not performed, 1 means that auth failed, 2 means that auth successful
    private int authenticationStatus = 0;

    private int popupWindowWidth;

    private int popupWindowHeight;

    private int clientWidth;

    private int clientHeight;

    private JsPopUpOAuthWindowCallback callback;

    public JsPopUpOAuthWindow(String authUrl,
                              String errorPageUrl,
                              int popupWindowWidth,
                              int popupWindowHeight,
                              int clientWidth,
                              int clientHeight,
                              JsPopUpOAuthWindowCallback callback) {
        this.authUrl = authUrl;
        this.errorPageUrl = errorPageUrl;
        this.popupWindowWidth = popupWindowWidth;
        this.popupWindowHeight = popupWindowHeight;
        this.clientWidth = clientWidth;
        this.clientHeight = clientHeight;
        this.callback = callback;
    }

    public int getAuthenticationStatus() {
        return authenticationStatus;
    }

    public void setAuthenticationStatus(int authenticationStatus) {
        this.authenticationStatus = authenticationStatus;
        if (this.callback != null) {
            this.callback.oAuthFinished(this.authenticationStatus);
        }
    }

    public JsPopUpOAuthWindow(String authUrl,
                              String errorPageUrl,
                              int popupWindowWidth,
                              int popupWindowHeight,
                              JsPopUpOAuthWindowCallback callback) {
        this(authUrl, errorPageUrl, popupWindowWidth, popupWindowHeight, Window.getClientWidth(), Window.getClientHeight(), callback);
    }

    public void loginWithOAuth() {
        this.loginWithOAuth(authUrl, errorPageUrl, popupWindowWidth, popupWindowHeight, clientWidth, clientHeight);
    }

    // @formatter:off
    private native void loginWithOAuth(String authUrl, String errorPageUrl, int popupWindowWidth,
                                       int popupWindowHeight, int clientWidth, int clientHeight) /*-{

        var instance = this;

        function Popup(authUrl, errorPageUrl, popupWindowWidth, popupWindowHeight) {
            this.authUrl = authUrl;
            this.errorPageUrl = errorPageUrl;
            this.popupWindowWidth = popupWindowWidth;
            this.popupWindowHeight = popupWindowHeight;

            var popup_close_handler = function () {
                if (!popupWindow || popupWindow.closed) {
                    instance.@org.exoplatform.ide.client.framework.ui.JsPopUpOAuthWindow::setAuthenticationStatus(I)(1);
                    console.log("closed popup")
                    popupWindow = null;
                    if (popupCloseHandlerIntervalId) {
                        window.clearInterval(popupCloseHandlerIntervalId);
                    }
                } else {
                    var href;
                    try {
                        href = popupWindow.location.href;
                    } catch (error) {
                    }

                    if (href) {
                        console.log(href);
                        var path = popupWindow.location.pathname;
                        if (path == "/ide/" + $wnd.ws) {
                            instance.@org.exoplatform.ide.client.framework.ui.JsPopUpOAuthWindow::setAuthenticationStatus(I)(2);
                            popupWindow.close();
                            popupWindow = null;
                            if (popupCloseHandlerIntervalId) {
                                window.clearInterval(popupCloseHandlerIntervalId);
                                console.log("stop interval " + popupCloseHandlerIntervalId);
                            }
                        } else if (path.match("j_security_check$")) {
                            instance.@org.exoplatform.ide.client.framework.ui.JsPopUpOAuthWindow::setAuthenticationStatus(I)(1);
                            console.log("login failed");
                            if (!errorFlag) {
                                errorFlag = true;
                                popupWindow.location.replace(errorPageUrl);
                            }

                        }
                    }
                }
            }

            this.open_window = function () {
                var x = Math.max(0, Math.round(clientWidth / 2)
                    - Math.round(this.popupWindowWidth / 2));
                var y = Math.max(0, Math.round(clientHeight / 2)
                    - Math.round(this.popupWindowHeight / 2));
                popupWindow = window.open(this.authUrl, 'popup', 'width='
                    + this.popupWindowWidth + ',height='
                    + this.popupWindowHeight + ',left=' + x + ',top=' + y);
                popupCloseHandlerIntervalId = window.setInterval(
                    popup_close_handler, 80);
                errorFlag = false;
            }
        }

        var popup = new Popup(authUrl, errorPageUrl, popupWindowWidth,
            popupWindowHeight);
        popup.open_window();
    }-*/;
    // @formatter:on
}
