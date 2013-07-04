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
