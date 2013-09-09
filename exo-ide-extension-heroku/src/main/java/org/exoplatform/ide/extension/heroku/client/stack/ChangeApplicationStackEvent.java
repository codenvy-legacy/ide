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
package org.exoplatform.ide.extension.heroku.client.stack;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to change the stack of Heroku application.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 28, 2011 6:00:14 PM anya $
 */
public class ChangeApplicationStackEvent extends GwtEvent<ChangeApplicationStackHandler> {
    /** Type used to register the event. */
    public static final GwtEvent.Type<ChangeApplicationStackHandler> TYPE =
            new GwtEvent.Type<ChangeApplicationStackHandler>();

    /** Application, for which to change stack. */
    private String application;

    /**
     * @param application
     *         application, for which to change stack, may be <code>null</code>
     */
    public ChangeApplicationStackEvent(String application) {
        this.application = application;
    }

    public ChangeApplicationStackEvent() {
        this.application = null;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ChangeApplicationStackHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ChangeApplicationStackHandler handler) {
        handler.onChangeApplicationStack(this);
    }

    /** @return the application */
    public String getApplication() {
        return application;
    }
}
