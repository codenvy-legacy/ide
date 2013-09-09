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
package org.exoplatform.ide.client.application;

import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.util.Utils;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class SessionKeepAlive implements InitializeServicesHandler {

    private static final int PING_DELAY = 1000 * 60 * 5;

    private String url;

    private Timer timer = new Timer() {
        @Override
        public void run() {
            ping();
        }
    };


    private RequestCallback callback = new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
        }

        @Override
        public void onError(Request request, Throwable exception) {
        }
    };

    public SessionKeepAlive() {
        IDE.eventBus().addHandler(InitializeServicesEvent.TYPE, this);
    }

    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        url = Utils.getRestContext() + Utils.getWorkspaceName() + "/configuration/ping?random=" + Random.nextDouble();//avoid caching   
        timer.scheduleRepeating(PING_DELAY);
    }

    private void ping() {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.setCallback(callback);
            builder.send();
        } catch (RequestException e) {
            //ignore
        }

    }
}
