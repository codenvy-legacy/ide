/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.paas;

import com.codenvy.ide.api.extension.SDK;
import com.codenvy.ide.api.ui.wizard.paas.AbstractPaasPage;
import com.codenvy.ide.collections.Array;
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
     * @param projectTypeIds
     *         project type identifiers which PaaS supports
     * @param wizardPages
     *         pages which need to be added to a new project wizard
     * @param provideTemplate
     *         <code>true</code> if the PaaS doesn't need general templates (it has own template), and <code>false</code> otherwise
     */
    void register(@NotNull String id,
                  @NotNull String title,
                  @Nullable ImageResource image,
                  @NotNull Array<String> projectTypeIds,
                  @NotNull Array<Provider<? extends AbstractPaasPage>> wizardPages,
                  boolean provideTemplate);
}