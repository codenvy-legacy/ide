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
package org.exoplatform.ide.extension.aws.client.s3.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when object uploaded to the AWS S3.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: S3ObjectUploadedEvent.java Sep 26, 2012 vetal $
 */
public class S3ObjectUploadedEvent extends GwtEvent<S3ObjectUploadedHandler> {

    /** Type used to register the event. */
    public static final GwtEvent.Type<S3ObjectUploadedHandler> TYPE = new GwtEvent.Type<S3ObjectUploadedHandler>();


    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<S3ObjectUploadedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(S3ObjectUploadedHandler handler) {
        handler.onS3ObjectUploaded(this);
    }
}
