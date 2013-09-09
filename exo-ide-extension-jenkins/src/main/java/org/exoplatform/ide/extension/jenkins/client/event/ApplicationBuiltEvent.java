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
package org.exoplatform.ide.extension.jenkins.client.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.jenkins.shared.JobStatus;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class ApplicationBuiltEvent extends GwtEvent<ApplicationBuiltHandler> {

    public static final GwtEvent.Type<ApplicationBuiltHandler> TYPE = new Type<ApplicationBuiltHandler>();

    private JobStatus jobStatus;

    /** @param jobStatus */
    public ApplicationBuiltEvent(JobStatus jobStatus) {
        super();
        this.jobStatus = jobStatus;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ApplicationBuiltHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ApplicationBuiltHandler handler) {
        handler.onApplicationBuilt(this);
    }

    /** @return the jobStatus */
    public JobStatus getJobStatus() {
        return jobStatus;
    }

}
