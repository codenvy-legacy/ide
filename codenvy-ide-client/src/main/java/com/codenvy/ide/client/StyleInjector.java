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
package com.codenvy.ide.client;

import com.codenvy.ide.Resources;
import com.google.inject.Inject;

/** @author Evgen Vidolob */
public class StyleInjector {
    private Resources resources;

    @Inject
    public StyleInjector(Resources resources) {
        this.resources = resources;
    }

    public void inject() {
        resources.coreCss().ensureInjected();
        resources.treeCss().ensureInjected();
        resources.defaultSimpleListCss().ensureInjected();
        resources.workspaceNavigationFileTreeNodeRendererCss().ensureInjected();
        resources.partStackCss().ensureInjected();
        resources.dialogBox().ensureInjected();
        resources.clipboardCss().ensureInjected();
        resources.notificationCss().ensureInjected();
        resources.dataGridStyle().ensureInjected();
        resources.cellTableStyle().ensureInjected();
        resources.defaultCategoriesListCss().ensureInjected();
        resources.Css().ensureInjected();
        resources.menuCss().ensureInjected();
    }
}
