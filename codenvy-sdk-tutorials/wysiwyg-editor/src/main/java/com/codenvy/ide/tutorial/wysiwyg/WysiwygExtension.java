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
        FileType htmlFileType = new FileType(null, "text/html", "html");
        resourceProvider.registerFileType(htmlFileType);
        editorRegistry.register(htmlFileType, editorProvider);

        workspaceAgent.openPart(howToPresenter, PartStackType.EDITING);
    }
}
