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

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class StyleInjector {
    private Resources resources;

    /**
     *
     */
    @Inject
    public StyleInjector(Resources resources) {
        this.resources = resources;
    }

    public void inject() {
        resources.coreCss().ensureInjected();
        resources.editableContentAreaCss().ensureInjected();
        resources.editorSelectionLineRendererCss().ensureInjected();
        resources.lineNumberRendererCss().ensureInjected();
        resources.treeCss().ensureInjected();
        resources.defaultSimpleListCss().ensureInjected();
        resources.workspaceEditorBufferCss().ensureInjected();
        resources.workspaceEditorCss().ensureInjected();
        resources.workspaceEditorCursorCss().ensureInjected();
        resources.workspaceNavigationFileTreeNodeRendererCss().ensureInjected();
        resources.partStackCss().ensureInjected();
        resources.parenMatchHighlighterCss().ensureInjected();
        resources.dialogBox().ensureInjected();
        resources.welcomeCSS().ensureInjected();
        resources.notificationCss().ensureInjected();
        resources.dataGridStyle().ensureInjected();
        resources.cellTableStyle().ensureInjected();
        resources.infoPanelCss().ensureInjected();
        resources.defaultCategoriesListCss().ensureInjected();
    }
}
