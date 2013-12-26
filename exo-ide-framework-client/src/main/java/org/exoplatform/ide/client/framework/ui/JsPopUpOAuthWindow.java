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

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.util.Utils;

import java.util.ArrayList;
import java.util.List;

/** Pop-up authorization window to allow user make authorization via OAuth on third-party service. */
public class JsPopUpOAuthWindow {

    public interface Callback {
        public void oAuthFinished(int authenticationStatus);
    }

    private String   oauthProvider;
    private Callback callback;
    private String   authorizationMode;

    private List<String> scopes            = new ArrayList<String>();
    private int          popupWindowWidth  = 980;
    private int          popupWindowHeight = 500;


    public JsPopUpOAuthWindow() {
    }

    public JsPopUpOAuthWindow withOauthProvider(String oauthProvider) {
        this.oauthProvider = oauthProvider;
        return this;
    }

    public JsPopUpOAuthWindow withScopes(List<String> scopes) {
        this.scopes = scopes;
        return this;
    }

    public JsPopUpOAuthWindow withScope(String scope) {
        scopes.add(scope);
        return this;
    }

    public JsPopUpOAuthWindow withWindowWidth(int width) {
        this.popupWindowWidth = width;
        return this;
    }

    public JsPopUpOAuthWindow withWindowHeight(int height) {
        this.popupWindowHeight = height;
        return this;
    }

    public JsPopUpOAuthWindow withCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public JsPopUpOAuthWindow withAuthMode(String authorizationMode) {
        this.authorizationMode = authorizationMode;
        return this;
    }

    public void login() {
        StringBuilder urlBuilder = new StringBuilder(Utils.getAuthorizationContext());
        urlBuilder.append("/oauth/authenticate").append('?');
        urlBuilder.append("oauth_provider=").append(oauthProvider);
        urlBuilder.append("&userId=").append(IDE.user.getUserId());
        urlBuilder.append("&redirect_after_login=").append("/ide/").append(Utils.getWorkspaceName());

        if (scopes != null) {
            for (String scope : scopes) {
                urlBuilder.append("&scope=").append(scope);
            }
        }

        if (authorizationMode != null) {
            urlBuilder.append("&mode=").append(authorizationMode);
        }

        loginWithOAuth(urlBuilder.toString(),
                       Utils.getAuthorizationErrorPageURL(),
                       popupWindowWidth,
                       popupWindowHeight,
                       Window.getClientWidth(),
                       Window.getClientHeight());
    }

    private void setAuthenticationStatus(int authenticationStatus) {
        if (this.callback != null) {
            this.callback.oAuthFinished(authenticationStatus);
        }
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
                        } else if (path.match("j_security_check$") || path == "/site/login") {
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
