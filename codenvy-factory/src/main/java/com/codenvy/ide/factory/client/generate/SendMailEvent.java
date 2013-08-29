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
package com.codenvy.ide.factory.client.generate;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to share your Factory URL by e-mail.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: SendMailEvent.java Jun 11, 2013 11:27:15 AM azatsarynnyy $
 */
public class SendMailEvent extends GwtEvent<SendMailHandler> {

    /** Type used to register this event. */
    public static final Type<SendMailHandler> TYPE = new Type<SendMailHandler>();

    /** Text message to send. */
    private String                            message;

    /**
     * Constructs new event with specified text message to send.
     * 
     * @param message text message to send
     */
    public SendMailEvent(String message) {
        this.message = message;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public Type<SendMailHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(SendMailHandler handler) {
        handler.onSendMail(this);
    }

    public String getMessage() {
        return message;
    }

}
