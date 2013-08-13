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

import com.codenvy.ide.api.extension.SDK;
import com.codenvy.ide.api.ui.preferences.PreferencesPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Provider;

/**
 * Provides a way to register a new PaaS Extension.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@SDK(title = "ide.api.ui.paas")
public interface PaaSAgent {
    /**
     * Registers new PaaS.
     *
     * @param id
     * @param title
     * @param image
     * @param requiredTypes
     * @param wizardPage
     * @param preferencePage
     */
    void registerPaaS(String id, String title, ImageResource image, JsonArray<String> requiredTypes,
                      Provider<? extends WizardPagePresenter> wizardPage, PreferencesPagePresenter preferencePage);

    /**
     * Returns selected PaaS.
     *
     * @return paas
     */
    PaaS getSelectedPaaS();
}