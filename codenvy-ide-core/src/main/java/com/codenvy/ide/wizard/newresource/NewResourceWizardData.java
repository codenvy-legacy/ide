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
package com.codenvy.ide.wizard.newresource;

import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Provider;


/**
 * Aggregate information about registered wizard for creating new file.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewResourceWizardData {
    private String title;

    private String category;

    private ImageResource icon;

    private Provider<? extends WizardPagePresenter> wizardPage;

    /**
     * Create wizard's data
     *
     * @param title
     * @param category
     * @param icon
     * @param wizardPage
     */
    public NewResourceWizardData(String title, String category, ImageResource icon,
                                 Provider<? extends WizardPagePresenter> wizardPage) {
        super();
        this.title = title;
        this.category = category;
        this.icon = icon;
        this.wizardPage = wizardPage;
    }

    /**
     * Returns wizard's title.
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns wizard's page presenter.
     *
     * @return
     */
    public WizardPagePresenter getWizardPage() {
        return wizardPage.get();
    }

    /**
     * Returns wizard's category.
     *
     * @return
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns wizard's icon.
     *
     * @return the wizard's icon, or <code>null</code> if nones
     */
    public ImageResource getIcon() {
        return icon;
    }
}