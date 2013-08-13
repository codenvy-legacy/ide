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

package com.codenvy.ide.commons.exception;

import com.google.gwt.event.shared.GwtEvent;

/** @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a> */

public class ExceptionThrownEvent extends ServerExceptionEvent<ExceptionThrownHandler> {

    private String errorMessage;

    public static final GwtEvent.Type<ExceptionThrownHandler> TYPE = new GwtEvent.Type<ExceptionThrownHandler>();

    @Override
    public GwtEvent.Type<ExceptionThrownHandler> getAssociatedType() {
        return TYPE;
    }

    public ExceptionThrownEvent(Throwable throwable) {
        this(throwable, null);
    }

    public ExceptionThrownEvent(String errorMessage) {
        this(null, errorMessage);
    }

    public ExceptionThrownEvent(Throwable throwable, String errorMesage) {
        super(throwable);
        this.errorMessage = errorMesage;
    }

    @Override
    protected void dispatch(ExceptionThrownHandler handler) {
        handler.onError(this);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
