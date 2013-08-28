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
package org.exoplatform.ide.extension.openshift.client;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs on exception that concerns actions with OpenShift. Implement {@link OpenShiftExceptionThrownHandler} to handle
 * event.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 10, 2011 5:26:52 PM anya $
 */
public class OpenShiftExceptionThrownEvent extends GwtEvent<OpenShiftExceptionThrownHandler> {
    /** Type used to register event, */
    public static final GwtEvent.Type<OpenShiftExceptionThrownHandler> TYPE =
            new GwtEvent.Type<OpenShiftExceptionThrownHandler>();

    /** Exception, that occurred. */
    private Throwable exception;

    /** Error message. */
    private String errorMessage;

    /**
     * @param exception
     *         occurred exception
     * @param errorMessage
     *         error message
     */
    public OpenShiftExceptionThrownEvent(Throwable exception, String errorMessage) {
        this.exception = exception;
        this.errorMessage = errorMessage;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<OpenShiftExceptionThrownHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(OpenShiftExceptionThrownHandler handler) {
        handler.onOpenShiftExceptionThrown(this);
    }

    /** @return the exception */
    public Throwable getException() {
        return exception;
    }

    /** @return the errorMessage */
    public String getErrorMessage() {
        return errorMessage;
    }
}
