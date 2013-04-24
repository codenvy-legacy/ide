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
package com.codenvy.ide.api.template;

import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Provider;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
public class Template {
    private ImageResource icon;

    private String title;

    private CreateProjectProvider createProjectProvider;

    private Provider<? extends WizardPagePresenter> wizardPage;

    private JsonArray<String> projectTypes;

    public Template(ImageResource icon, String title, CreateProjectProvider createProjectProvider,
                    Provider<? extends WizardPagePresenter> wizardPage, JsonArray<String> projectTypes) {
        this.icon = icon;
        this.title = title;
        this.createProjectProvider = createProjectProvider;
        this.wizardPage = wizardPage;
        this.projectTypes = projectTypes;
    }

    public ImageResource getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public CreateProjectProvider getCreateProjectProvider() {
        return createProjectProvider;
    }

    public WizardPagePresenter getWizardPage() {
        return wizardPage != null ? wizardPage.get() : null;
    }

    public JsonArray<String> getProjectTypes() {
        return projectTypes;
    }
}