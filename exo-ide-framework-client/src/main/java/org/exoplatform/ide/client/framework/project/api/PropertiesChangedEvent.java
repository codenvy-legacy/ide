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
package org.exoplatform.ide.client.framework.project.api;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:dvishinskiy@codenvy.com">Dmitriy Vyshinskiy</a>
 */
public class PropertiesChangedEvent extends GwtEvent<PropertiesChangedHandler> {

    public static final GwtEvent.Type<PropertiesChangedHandler> TYPE = new GwtEvent.Type<PropertiesChangedHandler>();

    private final ProjectModel                                  project;

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<PropertiesChangedHandler> getAssociatedType() {
        return TYPE;
    }

    public PropertiesChangedEvent(ProjectModel project) {
        this.project = project;
    }

    public ProjectModel getProject() {
        return this.project;
    }

    @Override
    protected void dispatch(PropertiesChangedHandler handler) {
        handler.onPropertiesChanged(this);
    }

}
