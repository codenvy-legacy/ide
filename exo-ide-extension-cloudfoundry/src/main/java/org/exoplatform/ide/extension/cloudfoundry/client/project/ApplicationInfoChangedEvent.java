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
package org.exoplatform.ide.extension.cloudfoundry.client.project;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when CloudFoundry application information, properties were changed.
 * It can be state (after run/stop/restart), memory size and so on.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Dec 8, 2011 4:10:33 PM anya $
 */
public class ApplicationInfoChangedEvent extends GwtEvent<ApplicationInfoChangedHandler> {
    /** Type used to register event. */
    public static final GwtEvent.Type<ApplicationInfoChangedHandler> TYPE =
            new GwtEvent.Type<ApplicationInfoChangedHandler>();

    /** VFS's id. */
    private String vfsId;

    /** Project's id. */
    private String projectId;

    /**
     * @param vfsId
     *         VFS id
     * @param projectId
     *         project's id
     */
    public ApplicationInfoChangedEvent(String vfsId, String projectId) {
        this.vfsId = vfsId;
        this.projectId = projectId;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ApplicationInfoChangedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ApplicationInfoChangedHandler handler) {
        handler.onApplicationInfoChanged(this);
    }

    /** @return the vfsId VFS id */
    public String getVfsId() {
        return vfsId;
    }

    /** @return the projectId project's id */
    public String getProjectId() {
        return projectId;
    }
}
