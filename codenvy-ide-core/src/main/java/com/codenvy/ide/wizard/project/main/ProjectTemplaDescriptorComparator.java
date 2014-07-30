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
package com.codenvy.ide.wizard.project.main;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;

import java.util.Comparator;

/**
 * Helps to sort the template descriptors by display name.
 *
 * @author Oleksii Orel
 */
final class ProjectTemplaDescriptorComparator implements Comparator<ProjectTemplateDescriptor> {
    @Override
    public int compare(ProjectTemplateDescriptor o1, ProjectTemplateDescriptor o2) {
        return o1.getDisplayName().compareTo(o2.getDisplayName());
    }
}