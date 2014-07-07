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
package com.codenvy.ide.tutorial.editor;

import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.api.filetypes.FileTypeRegistry;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.tutorial.editor.editor.GroovyEditorProvider;
import com.codenvy.ide.tutorial.editor.part.TutorialHowToPresenter;
import com.google.gwt.core.client.ScriptInjector;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_FILE_NEW;
import static com.codenvy.ide.api.ui.workspace.PartStackType.EDITING;
import static com.google.gwt.core.client.ScriptInjector.TOP_WINDOW;

/** Extension used to demonstrate the Editor API. */
@Singleton
@Extension(title = "Editor API tutorial", version = "1.0.0")
public class EditorTutorialExtension {
    public static final String GROOVY_MIME_TYPE = "text/x-groovy";

    @Inject
    public EditorTutorialExtension(WorkspaceAgent workspaceAgent,
                                   TutorialHowToPresenter howToPresenter,
                                   EditorRegistry editorRegistry,
                                   FileTypeRegistry fileTypeRegistry,
                                   GroovyEditorProvider groovyEditorProvider,
                                   EditorTutorialResource editorTutorialResource,
                                   ActionManager actionManager,
                                   NewGroovyFileAction newGroovyFileAction,
                                   EditorTutorialResource resource) {
        editorTutorialResource.groovyCSS().ensureInjected();
        ScriptInjector.fromString(editorTutorialResource.groovyParserJS().getText()).setWindow(TOP_WINDOW).inject();

        workspaceAgent.openPart(howToPresenter, EDITING);

        FileType groovyFile = new FileType("Groovy", resource.groovyFile(), GROOVY_MIME_TYPE, "groovy");
        fileTypeRegistry.registerFileType(groovyFile);

        editorRegistry.register(groovyFile, groovyEditorProvider);

        actionManager.registerAction("newGroovyFileActionId", newGroovyFileAction);
        DefaultActionGroup newGroup = (DefaultActionGroup)actionManager.getAction(GROUP_FILE_NEW);
        newGroup.addSeparator();
        newGroup.add(newGroovyFileAction);
    }
}