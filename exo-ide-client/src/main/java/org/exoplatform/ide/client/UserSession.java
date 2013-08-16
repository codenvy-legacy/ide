/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.client;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.Document;

import org.exoplatform.ide.client.framework.util.UUID;
import org.exoplatform.ide.client.framework.websocket.events.ConnectionOpenedHandler;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class UserSession implements ConnectionOpenedHandler {

    private String uuid;

    private boolean targetWindow = false;

    public UserSession() {
        uuid = UUID.uuid();
        IDE.messageBus().setOnOpenHandler(this);
        Document document = Browser.getDocument();
        document.setOnFocus(new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                uuid = UUID.uuid();
                if (targetWindow) {
                    targetWindow = false;
                    uuid = UUID.uuid();
                    sendLog(uuid, "start");
                }
            }
        });
        document.setOnBlur(new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                if (!targetWindow) {
                    targetWindow = true;
                    sendLog(uuid, "stop");
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onConnectionOpened() {
        sendLog(uuid, "start");
    }

    private void sendLog(String uuid, String status){
//        RequestMessageBuilder builder =
//                RequestMessageBuilder.build(RequestBuilder.POST, IDE.currentWorkspace.getName() + "/session/ide/" + status);
//        builder.data("{\"sessionId\":\""+uuid+"\",\"browserInfo\":\""+getBrowserInfo()+"\"}");
//        IDE.messageBus().send(builder.getRequestMessage(), new RequestCallback<Void>() {
//            @Override
//            protected void onSuccess(Void result) {
//            }
//
//            @Override
//            protected void onFailure(Throwable exception) {
//            }
//        });
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
}
