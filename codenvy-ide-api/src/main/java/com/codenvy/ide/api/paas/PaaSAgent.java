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

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.extension.SDK;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonStringMap;
import com.google.gwt.resources.client.ImageResource;

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
     *         id of PaaS for identification
     * @param title
     *         title that will be shown on new project wizard page
     * @param image
     *         image that will be shown on new project wizard page
     * @param natures
     *         natures which support the PaaS
     * @param wizardPages
     *         pages which need add to new project wizard
     * @param provideTemplate
     *         <code>true</code> if the PaaS doesn't need general templates (it has own template), and <code>false</code> otherwise
     */
    void register(@NotNull String id,
                  @NotNull String title,
                  @Nullable ImageResource image,
                  @NotNull JsonStringMap<JsonArray<String>> natures,
                  @NotNull JsonArray<WizardPage> wizardPages,
                  boolean provideTemplate);
}