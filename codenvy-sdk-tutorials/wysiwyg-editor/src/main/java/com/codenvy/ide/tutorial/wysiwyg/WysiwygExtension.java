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
package com.codenvy.ide.tutorial.wysiwyg;

import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.tutorial.wysiwyg.part.TutorialHowToPresenter;
import com.google.inject.Inject;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Extension(title = "WYSIWYG Editor Extension", version = "1.0")
public class WysiwygExtension {

    @Inject
    public WysiwygExtension(ResourceProvider resourceProvider, WysiwygEditorProvider editorProvider, EditorRegistry editorRegistry,
                            WorkspaceAgent workspaceAgent, TutorialHowToPresenter howToPresenter) {
        FileType htmlFileType = new FileType(null, "text/htm", "htm");
        resourceProvider.registerFileType(htmlFileType);
        editorRegistry.register(htmlFileType, editorProvider);

        workspaceAgent.openPart(howToPresenter, PartStackType.EDITING);
    }
}
