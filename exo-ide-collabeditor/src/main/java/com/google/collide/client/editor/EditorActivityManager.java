// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.collide.client.editor;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.util.Utils;

import com.codenvy.ide.client.util.SignalEvent;
import com.codenvy.ide.client.util.UserActivityManager;
import com.codenvy.ide.commons.shared.ListenerRegistrar;
import com.codenvy.ide.commons.shared.ListenerRegistrar.Remover;
import com.codenvy.ide.json.shared.JsonArray;
import com.codenvy.ide.json.shared.JsonCollections;
import com.google.collide.client.editor.Buffer.ScrollListener;
import com.google.collide.client.editor.Editor.KeyListener;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;


/**
 * A class that listens to editor events to update the user activity manager on
 * the user's status.
 */
public class EditorActivityManager {
    
    private static final int IDLE_DELAY_MS =5000;

    private JsonArray<Remover> listenerRemovers = JsonCollections.createArray();

    EditorActivityManager(final UserActivityManager userActivityManager,
                          ListenerRegistrar<ScrollListener> scrollListenerRegistrar,
                          ListenerRegistrar<KeyListener> keyListenerRegistrar) {

        listenerRemovers.add(scrollListenerRegistrar.add(new ScrollListener() {
            @Override
            public void onScroll(Buffer buffer, int scrollTop) {
                userActivityManager.markUserActive();
                //switchTimer.schedule(IDLE_DELAY_MS);
            }
        }));

        listenerRemovers.add(keyListenerRegistrar.add(new KeyListener() {
            @Override
            public boolean onKeyPress(SignalEvent event) {
                userActivityManager.markUserActive();
                //switchTimer.schedule(IDLE_DELAY_MS);
                return false;
            }
        }));
    }

    void teardown() {
        for (int i = 0, n = listenerRemovers.size(); i < n; i++) {
            listenerRemovers.get(i).remove();
        }
    }
    
    private final Timer switchTimer = new Timer() {
        @Override
        public void run() {
            sendUserActiveEvent();
        }
    };
    
    private void sendUserActiveEvent() {
        String url = Utils.getRestContext() + Utils.getWorkspaceName() + "/event/user/active";
        try {
            AsyncRequest.build(RequestBuilder.GET, URL.encode(url)).loader(IDELoader.get()).send(new AsyncRequestCallback<Void>() {
                            @Override
                            protected void onSuccess(Void result) {
                                //success
                            }

                            @Override
                            protected void onFailure(Throwable exception) {
                            }
                        });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
