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
package com.codenvy.ide.wizard.newproject.pages.template;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.collections.Array;

/**
 * The view of {@link ChooseTemplatePagePresenter}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
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