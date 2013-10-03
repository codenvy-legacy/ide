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
package com.codenvy.ide.wizard.template;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.wizard.newproject2.NewProjectWizardModel;
import com.google.gwt.resources.client.ImageResource;
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
    private       NewProjectWizardModel newProjectWizardModel;
    private final JsonArray<Template>   templates;

    /** Create agent. */
    @Inject
    protected TemplateAgentImpl(NewProjectWizardModel newProjectWizardModel) {
        this.newProjectWizardModel = newProjectWizardModel;
        this.templates = JsonCollections.createArray();
    }

    /** {@inheritDoc} */
    @Override
    public void register(@NotNull String title, @Nullable ImageResource icon, @NotNull String primaryNature,
                         @NotNull JsonArray<String> secondaryNatures, @NotNull JsonArray<Provider<? extends WizardPage>> wizardPages) {
        Template template = new Template(title, icon, primaryNature, secondaryNatures);
        templates.add(template);
        newProjectWizardModel.addTemplatePages(template, wizardPages);
    }

    /**
     * Returns all available templates for creating project.
     *
     * @param primaryNature
     * @param secondaryNatures
     * @return
     */
    // TODO javadoc
    public JsonArray<Template> getTemplatesForProjectType(String primaryNature, JsonArray<String> secondaryNatures) {
        JsonArray<Template> templates = JsonCollections.createArray();
        for (Template template : templates.asIterable()) {
            if (template.isAvailable(primaryNature, secondaryNatures)) {
                templates.add(template);
            }
        }
        return templates;
    }
}