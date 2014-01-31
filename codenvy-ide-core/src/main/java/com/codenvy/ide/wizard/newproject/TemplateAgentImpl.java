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
package com.codenvy.ide.wizard.newproject;

import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.api.ui.wizard.template.AbstractTemplatePage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link TemplateAgent}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class TemplateAgentImpl implements TemplateAgent {
    private final StringMap<Template> templates;
    private       NewProjectWizard    newProjectWizard;

    /** Create agent. */
    @Inject
    protected TemplateAgentImpl(NewProjectWizard newProjectWizard) {
        this.newProjectWizard = newProjectWizard;
        this.templates = Collections.createStringMap();
    }

    /** {@inheritDoc} */
    @Override
    public void register(@NotNull String id,
                         @NotNull String title,
                         @NotNull String description,
                         @Nullable ImageResource icon,
                         @NotNull String projectTypeId,
                         @NotNull Array<Provider<? extends AbstractTemplatePage>> wizardPages) {
        if (templates.containsKey(id)) {
            Window.alert("Template with ID " + id + " already exists");
            return;
        }

        Template template = new Template(id, title, description, icon, projectTypeId);
        templates.put(id, template);
        for (Provider<? extends AbstractTemplatePage> provider : wizardPages.asIterable()) {
            newProjectWizard.addPageAfterChooseTemplate(provider);
        }
    }

    /**
     * Returns all available templates for the specified project type id.
     *
     * @param projectTypeId
     *         project type id
     * @return available project types
     */
    @NotNull
    public Array<Template> getTemplatesForProjectType(@NotNull String projectTypeId) {
        Array<Template> availableTemplates = Collections.createArray();
        for (Template template : templates.getValues().asIterable()) {
            if (template.isAvailable(projectTypeId)) {
                availableTemplates.add(template);
            }
        }
        return availableTemplates;
    }
}