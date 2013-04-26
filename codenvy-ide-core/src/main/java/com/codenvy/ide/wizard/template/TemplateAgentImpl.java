/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.wizard.template;

import com.codenvy.ide.api.template.CreateProjectProvider;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
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
    private final JsonArray<Template> templates;
    private       Template            selectedTemplate;

    /** Create agent. */
    @Inject
    protected TemplateAgentImpl() {
        this.templates = JsonCollections.createArray();
    }

    /** {@inheritDoc} */
    @Override
    public void registerTemplate(String title, ImageResource icon, JsonArray<String> projectTypes,
                                 CreateProjectProvider createProjectProvider, Provider<? extends WizardPagePresenter> wizardPage) {
        Template template = new Template(icon, title, createProjectProvider, wizardPage, projectTypes);
        templates.add(template);
    }

    /** {@inheritDoc} */
    @Override
    public Template getSelectedTemplate() {
        return selectedTemplate;
    }

    /**
     * Sets selected template for creating project.
     *
     * @param template
     */
    public void setSelectedTemplate(Template template) {
        selectedTemplate = template;
    }

    /**
     * Returns all available templates for creating project.
     *
     * @param projectType
     * @return
     */
    public JsonArray<Template> getTemplatesForProjectType(String projectType) {
        JsonArray<Template> templates = JsonCollections.createArray();

        for (int i = 0; i < this.templates.size(); i++) {
            Template template = this.templates.get(i);
            if (template.getProjectTypes().contains(projectType)) {
                templates.add(template);
            }
        }

        return templates;
    }
}