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
package com.codenvy.ide.template;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * The implementation of {@link TemplateAgent}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class TemplateAgentImpl implements TemplateAgent {
    private       NewProjectWizard    newProjectWizard;
    private final JsonArray<Template> templates;

    /** Create agent. */
    @Inject
    protected TemplateAgentImpl(NewProjectWizard newProjectWizard) {
        this.newProjectWizard = newProjectWizard;
        this.templates = JsonCollections.createArray();
    }

    /** {@inheritDoc} */
    @Override
    public void register(@NotNull String id,
                         @NotNull String title,
                         @Nullable ImageResource icon,
                         @NotNull String primaryNature,
                         @NotNull JsonArray<String> secondaryNatures,
                         @NotNull JsonArray<Provider<? extends WizardPage>> wizardPages) {
        if (isIdExist(id)) {
            Window.alert("Template with " + id + " id already exists");
        }

        Template template = new Template(id, title, icon, primaryNature, secondaryNatures);
        templates.add(template);
        if (wizardPages != null) {
            for (Provider<? extends WizardPage> provider : wizardPages.asIterable()) {
                newProjectWizard.addPageAfterChooseTemplate(provider);
            }
        }
    }

    /**
     * Returns whether the template with this id already exists.
     *
     * @return <code>true</code> if the template already exists, and <code>false</code> if it doesn't
     */
    private boolean isIdExist(@NotNull String id) {
        for (Template template : templates.asIterable()) {
            if (template.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all available templates for creating project.
     *
     * @param primaryNature
     *         needed primary nature
     * @param secondaryNatures
     *         needed secondary nature
     * @return available project type
     */
    @NotNull
    public JsonArray<Template> getTemplatesForProjectType(@NotNull String primaryNature, @NotNull JsonArray<String> secondaryNatures) {
        JsonArray<Template> availableTemplates = JsonCollections.createArray();
        for (Template template : templates.asIterable()) {
            if (template.isAvailable(primaryNature, secondaryNatures)) {
                availableTemplates.add(template);
            }
        }
        return availableTemplates;
    }
}