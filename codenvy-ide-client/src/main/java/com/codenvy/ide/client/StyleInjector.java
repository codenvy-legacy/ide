/*
 * Copyright (C) 2012 eXo Platform SAS.
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
        resources.wizardCSS().ensureInjected();
    }
}
