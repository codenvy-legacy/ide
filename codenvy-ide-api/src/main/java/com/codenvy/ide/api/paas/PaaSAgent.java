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
import com.codenvy.ide.api.ui.wizard.paas.AbstractPaasPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.StringMap;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Provider;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Provides a way to register a new PaaS Extension.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@SDK(title = "ide.api.ui.paas")
public interface PaaSAgent {
    /**
     * Registers a new PaaS.
     *
     * @param id
     *         id for PaaS identification
     * @param title
     *         title that will be shown on a new project wizard page
     * @param image
     *         image that will be shown on a new project wizard page
     * @param natures
     *         natures which support the PaaS
     * @param wizardPages
     *         pages which need to be added to a new project wizard
     * @param provideTemplate
     *         <code>true</code> if the PaaS doesn't need general templates (it has own template), and <code>false</code> otherwise
     */
    void register(@NotNull String id,
                  @NotNull String title,
                  @Nullable ImageResource image,
                  @NotNull StringMap<Array<String>> natures,
                  @NotNull Array<Provider<? extends AbstractPaasPage>> wizardPages,
                  boolean provideTemplate);
}