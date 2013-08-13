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

package com.codenvy.ide.util.executor;

import elemental.events.EventListener;
import elemental.html.Document.Event;
import elemental.js.events.JsEvent;

import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.core.client.JavaScriptObject;


/*
 * TODO: Make a scheduler so there's only one event listener for
 * ALL things that want to asyncrun
 */

/** An utility class to execute some logic asynchronously as soon as possible. */
public abstract class AsyncRunner implements Runnable {

    private static final String EVENT_MESSAGE = "message";

    private static class MessageEvent extends JsEvent implements Event {
        protected MessageEvent() {
        }

        public final native Object getData() /*-{
            return this.data;
        }-*/;

        public final native JavaScriptObject getSource() /*-{
            return this.source;
        }-*/;
    }

    private static int instanceId = 0;

    private final String messageName  =
            "test" + ":AsyncRunner." + instanceId++;
    private final String targetOrigin = Elements.getDocument().getLocation().getProtocol() + "//"
                                        + Elements.getDocument().getLocation().getHost();

    private boolean isCancelled;
    private boolean isAttached = false;

    private EventListener messageHandler = new EventListener() {
        @Override
        public void handleEvent(elemental.events.Event rawEvent) {
            MessageEvent event = (MessageEvent)rawEvent;
            if (!isCancelled && event.getData().equals(messageName)) {
                detachMessageHandler();
                event.stopPropagation();
                run();
            }
        }
    };

    public AsyncRunner() {
    }

    public void cancel() {
        isCancelled = true;
        detachMessageHandler();
    }

    public void schedule() {
        isCancelled = false;
        attachMessageHandler();
        scheduleJs();
    }

    private void attachMessageHandler() {
        if (!isAttached) {
            Elements.getWindow().addEventListener(EVENT_MESSAGE, messageHandler, true);
            isAttached = true;
        }
    }

    private void detachMessageHandler() {
        if (isAttached) {
            Elements.getWindow().removeEventListener(EVENT_MESSAGE, messageHandler, true);
            isAttached = false;
        }
    }

    private native void scheduleJs() /*-{
        // This is more responsive than setTimeout(0)
        $wnd.postMessage(this.@com.codenvy.ide.util.executor.AsyncRunner::messageName, this.
            @com.codenvy.ide.util.executor.AsyncRunner::targetOrigin);
    }-*/;
}
