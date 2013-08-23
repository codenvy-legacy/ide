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
package org.exoplatform.ide.client;

import com.codenvy.ide.client.util.logging.Log;

import org.exoplatform.ide.client.framework.configuration.InitialConfigurationReceivedEvent;
import org.exoplatform.ide.client.framework.configuration.InitialConfigurationReceivedHandler;
import org.exoplatform.ide.client.framework.util.UUID;
import org.exoplatform.ide.client.framework.websocket.events.ConnectionOpenedHandler;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class UserSession implements ConnectionOpenedHandler, InitialConfigurationReceivedHandler {

    private static UserSession instance;
    private        String      uuid;
    private boolean targetWindow = false;
    private boolean connectionOpened;
    private boolean settingsReceived;

    public UserSession() {
        instance = this;
        uuid = UUID.uuid();
        IDE.messageBus().setOnOpenHandler(this);
        addFocusHandler();
        addBlurHandler();
        IDE.addHandler(InitialConfigurationReceivedEvent.TYPE, this);
    }

    protected static UserSession get() {
        return instance;
    }

    private void handleFocus() {
        if (targetWindow) {
            uuid = UUID.uuid();
            targetWindow = false;
            sendLog(uuid, "start");
        }
    }

    private void handleBlur() {
        if (!targetWindow) {
            targetWindow = true;
            sendLog(uuid, "stop");
        }
    }

    private native void addFocusHandler() /*-{
        $wnd.onfocus = function () {
            var newVar = @org.exoplatform.ide.client.UserSession::get()();
            newVar.@org.exoplatform.ide.client.UserSession::handleFocus()();
        }
    }-*/;

    private native void addBlurHandler() /*-{
        $wnd.onblur = function () {
            var newVar = @org.exoplatform.ide.client.UserSession::get()();
            newVar.@org.exoplatform.ide.client.UserSession::handleBlur()();
        }
    }-*/;

    /** {@inheritDoc} */
    @Override
    public void onConnectionOpened() {
        try {
            if (settingsReceived) {
                sendLog(uuid, "start");
            } else {
                connectionOpened = true;
            }

        } catch (Throwable e) {
            Log.error(getClass(), e);
        }
    }

    private void sendLog(String uuid, String status) {
        try {
            IDE.messageBus().send(IDE.currentWorkspace.getName() + "/session/ide/" + status,
                                  "{\"sessionId\":\"" + uuid + "\",\"browserInfo\":\"" + getBrowserInfo() + "\"}");
        } catch (Throwable e) {
            Log.error(getClass(), e);
        }
    }

    private native String getBrowserInfo()/*-{
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

        return browserName + '/' + fullVersion;
    }-*/;

    public void close() {
        sendLog(uuid, "stop");
    }

    @Override
    public void onInitialConfigurationReceived(InitialConfigurationReceivedEvent event) {
        if (connectionOpened) {
            sendLog(uuid, "start");
        } else {
            settingsReceived = true;
        }
    }
}
