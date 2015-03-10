/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.projecttype.wizard.categoriespage;

import org.eclipse.che.api.project.shared.dto.ProjectTemplateDescriptor;
import org.eclipse.che.api.project.shared.dto.ProjectTypeDefinition;
import org.eclipse.che.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Evgen Vidolob
 */
@ImplementedBy(CategoriesPageViewImpl.class)
public interface CategoriesPageView extends View<CategoriesPageView.ActionDelegate> {

    void selectProjectType(String projectTypeId);

    void setCategories(Map<String, Set<ProjectTypeDefinition>> typesByCategory,
                       Map<String, Set<ProjectTemplateDescriptor>> templatesByCategory);

    void updateCategories(boolean includeTemplates);

    void reset();

    void resetName();

    void setConfigOptions(List<String> options);

    void setName(String name);

    void setDescription(String description);

    void setVisibility(boolean visible);

    void removeNameError();

    void showNameError();

    void focusName();

    void setProjectTypes(List<ProjectTypeDefinition> availableProjectTypes);

    public interface ActionDelegate {

        void projectNameChanged(String name);

        void projectDescriptionChanged(String projectDescriptionValue);

        void projectVisibilityChanged(boolean visible);

        void projectTemplateSelected(ProjectTemplateDescriptor template);

        void projectTypeSelected(ProjectTypeDefinition typeDescriptor);
    }
}
