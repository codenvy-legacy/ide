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
package org.exoplatform.ide.client.framework.documentation;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fire this to add documentation for specific media file. <br>
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RegisterDocumentationEvent Jan 24, 2011 11:11:39 AM evgen $
 */
public class RegisterDocumentationEvent extends GwtEvent<RegisterDocumentationHandler> {

    public static GwtEvent.Type<RegisterDocumentationHandler> TYPE = new Type<RegisterDocumentationHandler>();

    private String mimeType;

    private String url;

    /**
     * @param mimeType
     *         type of file
     * @param url
     *         to documentation
     */
    public RegisterDocumentationEvent(String mimeType, String url) {
        this.mimeType = mimeType;
        this.url = url;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RegisterDocumentationHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(RegisterDocumentationHandler handler) {
        handler.onRegisterDocumentation(this);
    }

    /** @return the mimeType of file */
    public String getMimeType() {
        return mimeType;
    }

    /** @return the url to the documentation */
    public String getUrl() {
        return url;
    }

}
