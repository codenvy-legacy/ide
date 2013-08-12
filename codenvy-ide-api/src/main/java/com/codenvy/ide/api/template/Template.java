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
package com.codenvy.ide.api.template;

import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Provider;

/**
 * Aggregate information about registered Template for creating project.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class Template {
    private ImageResource                           icon;
    private String                                  title;
    private CreateProjectProvider                   createProjectProvider;
    private Provider<? extends WizardPagePresenter> wizardPage;
    private JsonArray<String>                       projectTypes;

    /**
     * Create template.
     *
     * @param icon
     * @param title
     * @param createProjectProvider
     * @param wizardPage
     * @param projectTypes
     */
    public Template(ImageResource icon, String title, CreateProjectProvider createProjectProvider,
                    Provider<? extends WizardPagePresenter> wizardPage, JsonArray<String> projectTypes) {
        this.icon = icon;
        this.title = title;
        this.createProjectProvider = createProjectProvider;
        this.wizardPage = wizardPage;
        this.projectTypes = projectTypes;
    }

    /** @return template's icon */
    public ImageResource getIcon() {
        return icon;
    }

    /** @return template's title */
    public String getTitle() {
        return title;
    }

    /** @return create project provider */
    public CreateProjectProvider getCreateProjectProvider() {
        return createProjectProvider;
    }

    /** @return the wizard page */
    public WizardPagePresenter getWizardPage() {
        return wizardPage != null ? wizardPage.get() : null;
    }

    /** @return available project types */
    public JsonArray<String> getProjectTypes() {
        return projectTypes;
    }
}