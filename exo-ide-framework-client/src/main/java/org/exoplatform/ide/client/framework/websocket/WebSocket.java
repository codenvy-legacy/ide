/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.websocket;

import com.google.gwt.core.client.JavaScriptObject;

import org.exoplatform.ide.client.framework.websocket.MessageBus.ReadyState;
import org.exoplatform.ide.client.framework.websocket.events.ConnectionClosedHandler;
import org.exoplatform.ide.client.framework.websocket.events.ConnectionErrorHandler;
import org.exoplatform.ide.client.framework.websocket.events.ConnectionOpenedHandler;
import org.exoplatform.ide.client.framework.websocket.events.MessageReceivedHandler;

/**
 * Class that wraps native JavaScript WebSocket object.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: WebSocket.java Dec 4, 2012 4:46:55 PM azatsarynnyy $
 */
public class WebSocket extends JavaScriptObject {
    protected WebSocket() {
    }

    /**
     * Creates a new WebSocket instance.
     * WebSocket attempt to connect to their URL immediately upon creation.
     *
     * @param url
     *         WebSocket server URL
     * @return the created {@link WebSocket} object
     */
    public static native WebSocket create(String url)
   /*-{
       return new WebSocket(url);
   }-*/;

    /**
     * Creates a WebSocket object.
     * WebSocket attempt to connect to their URL immediately upon creation.
     *
     * @param url
     *         WebSocket server URL
     * @param protocol
     *         subprotocol name
     * @return the created {@link WebSocket} object
     */
    public static native WebSocket create(String url, String protocol)
   /*-{
       return new WebSocket(url, protocol);
   }-*/;

    /**
     * Closes the WebSocket connection. If the connection state
     * is already {@link ReadyState#CLOSED}, this method does nothing.
     */
    public final native void close()
   /*-{
       this.close();
   }-*/;

    /**
     * Method can be used to detect WebSocket support in the current browser.
     *
     * @return <code>true</code>  if WebSockets are supported;
     *         <code>false</code> if they are not.
     */
    public static native boolean isSupported()
   /*-{
       return !!window.WebSocket;
   }-*/;

    /**
     * Returns the state of the WebSocket connection.
     *
     * @return ready-state value
     */
    public final native short getReadyState()
   /*-{
       return this.readyState;
   }-*/;

    /**
     * Represents the number of bytes of UTF-8 text
     * that have been queued using send() method.
     *
     * @return the number of queued bytes
     */
    public final native int getBufferedAmount()
   /*-{
       return this.bufferedAmount;
   }-*/;

    /**
     * Transmits data to the server over the WebSocket connection.
     *
     * @param data
     *         the data to be sent to the server
     */
    public final native void send(String data)
   /*-{
       this.send(data);
   }-*/;

    /**
     * Sets the {@link ConnectionOpenedHandler} to be notified when the WebSocket connection established.
     *
     * @param handler
     *         WebSocket open handler
     */
    public final native void setOnOpenHandler(ConnectionOpenedHandler handler)
   /*-{
       this.onopen = $entry(function () {
           handler.@org.exoplatform.ide.client.framework.websocket.events.ConnectionOpenedHandler::onConnectionOpened()();
       });
   }-*/;

    /**
     * Sets the {@link ConnectionClosedHandler} to be notified when the WebSocket close.
     *
     * @param handler
     *         WebSocket close handler
     */
    public final native void setOnCloseHandler(ConnectionClosedHandler handler)
   /*-{
       this.onclose = $entry(function () {
           var webSocketClosedEventInstance = @org.exoplatform.ide.client.framework.websocket.events.WebSocketClosedEvent::new(ILjava/lang/String;Z)(event.code, event.reason, event.wasClean);
           handler.@org.exoplatform.ide.client.framework.websocket.events.ConnectionClosedHandler::onConnectionClosed(Lorg/exoplatform/ide/client/framework/websocket/events/WebSocketClosedEvent;)(webSocketClosedEventInstance);
       });
   }-*/;

    /**
     * Sets the {@link ConnectionErrorHandler} to be notified when there is any error in communication.
     *
     * @param handler
     *         WebSocket error handler
     */
    public final native void setOnErrorHandler(ConnectionErrorHandler handler)
   /*-{
       this.onerror = $entry(function () {
           handler.@org.exoplatform.ide.client.framework.websocket.events.ConnectionErrorHandler::onConnectionError()();
       });
   }-*/;

    /**
     * Sets the {@link MessageReceivedHandler} to be notified when
     * client receives data from the WebSocket server.
     *
     * @param handler
     *         WebSocket message handler
     */
    public final native void setOnMessageHandler(MessageReceivedHandler handler)
   /*-{
       this.onmessage = $entry(function (event) {
           var webSocketMessageEventInstance = @org.exoplatform.ide.client.framework.websocket.events.MessageReceivedEvent::new(Ljava/lang/String;)(event.data);
           handler.@org.exoplatform.ide.client.framework.websocket.events.MessageReceivedHandler::onMessageReceived(Lorg/exoplatform/ide/client/framework/websocket/events/MessageReceivedEvent;)(webSocketMessageEventInstance);
       });
   }-*/;

}