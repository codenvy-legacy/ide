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
package org.exoplatform.ide.client.framework.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for opening form to creat project.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CreateNewProjectEvent.java Dec 8, 2011 5:38:18 PM vereshchaka $
 */
public class CreateProjectEvent extends GwtEvent<CreateProjectHandler> {

    public static final GwtEvent.Type<CreateProjectHandler> TYPE = new GwtEvent.Type<CreateProjectHandler>();

    /** Creates new instance of this event */
    public CreateProjectEvent() {
    }

    /**
     * Creates new instance of this event
     *
     * @param projectName
     * @param projectType
     */
    public CreateProjectEvent(String projectName, String projectType) {
    }

    @Override
    protected void dispatch(CreateProjectHandler handler) {
        handler.onCreateProject(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<CreateProjectHandler> getAssociatedType() {
        return TYPE;
    }

}
