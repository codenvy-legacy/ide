/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.event;

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Vitaly Parfonov
 */
public class ProjectDescriptorChangedEvent extends GwtEvent<ProjectDescriptorChangedHandler> {

    private ProjectDescriptor projectDescriptor;


    public static Type<ProjectDescriptorChangedHandler> TYPE = new Type<>();

    public ProjectDescriptorChangedEvent(ProjectDescriptor projectDescriptor) {
        this.projectDescriptor = projectDescriptor;
    }


    public ProjectDescriptor getProjectDescriptor() {
        return projectDescriptor;
    }

    public void setProjectDescriptor(ProjectDescriptor projectDescriptor) {
        this.projectDescriptor = projectDescriptor;
    }

    @Override
    public Type<ProjectDescriptorChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ProjectDescriptorChangedHandler handler) {
        handler.onProjectDescriptorChanged(this);

    }
}
