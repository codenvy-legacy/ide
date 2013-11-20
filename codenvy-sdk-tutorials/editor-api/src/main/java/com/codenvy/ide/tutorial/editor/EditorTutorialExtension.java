package com.codenvy.ide.tutorial.editor;

import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceAgent;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.tutorial.editor.editor.GroovyEditorProvider;
import com.codenvy.ide.tutorial.editor.part.TutorialHowToPresenter;
import com.codenvy.ide.tutorial.editor.provider.NewGroovyFileProvider;
import com.google.gwt.core.client.ScriptInjector;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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
                                   ResourceProvider resourceProvider,
                                   GroovyEditorProvider groovyEditorProvider,
                                   EditorTutorialResource editorTutorialResource,
                                   NewResourceAgent newResourceAgent,
                                   NewGroovyFileProvider newGroovyFileProvider,
                                   EditorTutorialResource resource) {
        editorTutorialResource.groovyCSS().ensureInjected();
        ScriptInjector.fromString(editorTutorialResource.groovyParserJS().getText()).setWindow(TOP_WINDOW).inject();

        workspaceAgent.openPart(howToPresenter, EDITING);

        FileType groovyFile = new FileType(resource.groovyFile(), GROOVY_MIME_TYPE, "groovy");
        resourceProvider.registerFileType(groovyFile);

        editorRegistry.register(groovyFile, groovyEditorProvider);

        newResourceAgent.register(newGroovyFileProvider);
    }
}