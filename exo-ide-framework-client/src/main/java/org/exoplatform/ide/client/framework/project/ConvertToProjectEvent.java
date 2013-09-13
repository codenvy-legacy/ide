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
package org.exoplatform.ide.client.framework.project;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.shared.Property;

import java.util.Collections;
import java.util.List;

/**
 * Event occurs, when user tries to convert folder to project. Implement {@link ConvertToProjectHandler} to handle event.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 27, 2011 3:53:03 PM anya $
 */
public class ConvertToProjectEvent extends GwtEvent<ConvertToProjectHandler> {
    /** Type used to register event. */
    public static final GwtEvent.Type<ConvertToProjectHandler> TYPE = new GwtEvent.Type<ConvertToProjectHandler>();

    private final String folderId;

    private final String vfsId;

    private final String projectType;

    private List<Property> properties;

    /**
     * @param folderId
     *         item id that be converted to project
     * @param vfsId
     * @param projectType
     *         type of project (optional)
     */
    public ConvertToProjectEvent(String folderId, String vfsId, String projectType) {
        this(folderId, vfsId, projectType, Collections.<Property>emptyList());
    }

    /**
     * @param folderId
     *         item id that be converted to project
     * @param vfsId
     * @param projectType
     *         type of project (optional)
     * @param properties
     *         the properties that be set to converted project
     */
    public ConvertToProjectEvent(String folderId, String vfsId, String projectType, List<Property> properties) {
        this.folderId = folderId;
        this.vfsId = vfsId;
        this.projectType = projectType;
        this.properties = properties;
    }


    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ConvertToProjectHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ConvertToProjectHandler handler) {
        handler.onConvertToProject(this);
    }

    public String getFolderId() {
        return folderId;
    }

    public String getVfsId() {
        return vfsId;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public String getProjectType() {
        return projectType;
    }
}
