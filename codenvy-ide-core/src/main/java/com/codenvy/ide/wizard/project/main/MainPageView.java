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
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.wizard.project.ProjectWizardView;
import com.google.inject.ImplementedBy;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Evgen Vidolob
 */
@ImplementedBy(MainPageViewImpl.class)
public interface MainPageView extends View<MainPageView.ActionDelegate> {

    void selectProjectType(String projectTypeId);

    void setProjectTypeCategories(Map<String, Set<ProjectTypeDescriptor>> categories, Map<String, Set<ProjectTemplateDescriptor>> samples);

    void reset();

    void resetName();

    void setConfigOptions(List<String> options);

    void enableInput();

    void disableInput();

    void setName(String name);

    void setDescription(String description);

    void setVisibility(boolean visible);

    void removeNameError();

    void showNameError();

    void focusOnName();

    void disableAllExceptName();

    public interface ActionDelegate {

        void projectNameChanged(String name);

        void projectDescriptionChanged(String projectDescriptionValue);

        void projectVisibilityChanged(Boolean aPublic);

        ProjectWizardView.ActionDelegate getProjectWizardDelegate();

        void projectTemplateSelected(ProjectTemplateDescriptor template);

        void projectTypeSelected(ProjectTypeDescriptor typeDescriptor);
    }
}
