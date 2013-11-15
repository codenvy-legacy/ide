package com.codenvy.ide.tutorial.editor;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.tutorial.editor.part.TutorialHowToPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.workspace.PartStackType.EDITING;

/** Extension used to demonstrate the Notification feature. */
@Singleton
@Extension(title = "Editor API tutorial", version = "1.0.0")
public class EditorTutorialExtension {

    @Inject
    public EditorTutorialExtension(WorkspaceAgent workspaceAgent, TutorialHowToPresenter howToPresenter) {
        workspaceAgent.openPart(howToPresenter, EDITING);
    }
}