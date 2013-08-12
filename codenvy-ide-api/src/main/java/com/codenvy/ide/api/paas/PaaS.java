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