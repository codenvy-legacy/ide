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
    }
}
