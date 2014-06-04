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
package com.codenvy.ide.wizard.newproject.pages.template;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;

/**
 * The view of {@link ChooseTemplatePagePresenter}.
 *
 * @author Andrey Plotnikov
 */
public interface ChooseTemplatePageView extends View<ChooseTemplatePageView.ActionDelegate> {
    /** Needs for delegate some function into TemplatePage view. */
    public interface ActionDelegate {
        /**
         * Performs any actions appropriate in response to the user having selected template for creating project.
         *
         * @param template
         */
        void onTemplateSelected(ProjectTemplateDescriptor template);
    }

    /**
     * Sets available templates.
     *
     * @param templates
     */
    void setTemplates(Array<ProjectTemplateDescriptor> templates);

    void selectItem(ProjectTemplateDescriptor template);
}