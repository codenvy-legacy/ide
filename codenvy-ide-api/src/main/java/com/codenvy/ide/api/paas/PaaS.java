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
package com.codenvy.ide.api.paas;

import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Provider;


/**
 * Aggregate information about registered PaaS.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class PaaS {
    /** Id of the PaaS. */
    private String id;

    /** Title of the PaaS. */
    private String title;

    /** PaaS image. */
    private ImageResource image;

    /** List of project types, required by the PaaS (can be deployed). */
    private JsonArray<String> requiredProjectTypes;

    private Provider<? extends WizardPagePresenter> wizardPage;

    /**
     * Create PaaS.
     *
     * @param id
     * @param title
     * @param image
     * @param requiredProjectTypes
     * @param wizardPage
     */
    public PaaS(String id, String title, ImageResource image, JsonArray<String> requiredProjectTypes,
                Provider<? extends WizardPagePresenter> wizardPage) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.requiredProjectTypes = requiredProjectTypes;
        this.wizardPage = wizardPage;
    }

    /** @return the wizardPage */
    public WizardPagePresenter getWizardPage() {
        return wizardPage != null ? wizardPage.get() : null;
    }

    /** @return {@link String} PaaS id */
    public String getId() {
        return id;
    }

    /** @return the title */
    public String getTitle() {
        return title;
    }

    /** @return the image */
    public ImageResource getImage() {
        return image;
    }

    /** @return the requiredProjectTypes */
    public JsonArray<String> getRequiredProjectTypes() {
        return requiredProjectTypes;
    }
}