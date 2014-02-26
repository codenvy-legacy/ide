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
package com.codenvy.ide.core.editor;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorInitException;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorPartPresenter.EditorPartCloseHandler;
import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.event.ActivePartChangedEvent;
import com.codenvy.ide.api.event.ActivePartChangedHandler;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.FileEvent.FileOperation;
import com.codenvy.ide.api.resources.FileEventHandler;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.texteditor.TextEditorPresenter;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class EditorAgentImpl implements EditorAgent {

    private final StringMap<EditorPartPresenter> openedEditors;

    /** Used to notify {@link EditorAgentImpl} that editor has closed */
    private final EditorPartCloseHandler editorClosed = new EditorPartCloseHandler() {
        @Override
        public void onClose(EditorPartPresenter editor) {
            editorClosed(editor);
        }
    };

    private final ActivePartChangedHandler activePartChangedHandler = new ActivePartChangedHandler() {
        @Override
        public void onActivePartChanged(ActivePartChangedEvent event) {
            if (event.getActivePart() instanceof EditorPartPresenter) {
                activeEditor = (EditorPartPresenter)event.getActivePart();
                activeEditor.activate();
            }
        }
    };

    private final FileEventHandler fileEventHandler = new FileEventHandler() {
        @Override
        public void onFileOperation(final FileEvent event) {
            if (event.getOperationType() == FileOperation.OPEN) {
                openEditor(event.getFile());
            } else if (event.getOperationType() == FileOperation.CLOSE) {
                // close associated editor. OR it can be closed itself TODO
            }
        }
    };

    /**
     *
     */
    final class EditorInputImpl implements EditorInput {
        /**
         *
         */
        private File file;

        /** @param file */
        private EditorInputImpl(File file) {
            this.file = file;
        }

        @Override
        public String getToolTipText() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getName() {
            return file.getName();
        }

        @Override
        public ImageResource getImageResource() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public File getFile() {
            return file;
        }

        /** {@inheritDoc} */
        @Override
        public void setFile(File file) {
            this.file = file;
        }
    }

    private EditorRegistry editorRegistry;

    private ResourceProvider provider;

    private final WorkspaceAgent workspace;

    private EditorPartPresenter activeEditor;

    private final EventBus eventBus;

    @Inject
    public EditorAgentImpl(EventBus eventBus, EditorRegistry editorRegistry, ResourceProvider provider,
                           final WorkspaceAgent workspace) {
        super();
        this.eventBus = eventBus;
        this.editorRegistry = editorRegistry;
        this.provider = provider;
        this.workspace = workspace;
        openedEditors = Collections.createStringMap();

        bind();
    }

    protected void bind() {
        eventBus.addHandler(ActivePartChangedEvent.TYPE, activePartChangedHandler);
        eventBus.addHandler(FileEvent.TYPE, fileEventHandler);
    }

    /** {@inheritDoc} */
    @Override
    public void openEditor(@NotNull final File file) {
        if (openedEditors.containsKey(file.getId())) {
            workspace.setActivePart(openedEditors.get(file.getId()));
        } else {
            FileType fileType = provider.getFileType(file);
            EditorProvider editorProvider = editorRegistry.getDefaultEditor(fileType);
            EditorPartPresenter editor = editorProvider.getEditor();
            try {
                editor.init(new EditorInputImpl(file));
                editor.addCloseHandler(editorClosed);
            } catch (EditorInitException e) {
                Log.error(getClass(), e);
            }
            workspace.openPart(editor, PartStackType.EDITING);
            openedEditors.put(file.getId(), editor);
        }
    }

    /** @param editor */
    protected void editorClosed(EditorPartPresenter editor) {
        if (activeEditor == editor) {
            activeEditor = null;
        }
        //call close() method
        if(editor instanceof TextEditorPresenter){
            ((TextEditorPresenter)editor).close(false);
        }
        Array<String> keys = openedEditors.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            String fileId = keys.get(i);
            // same instance
            if (openedEditors.get(fileId) == editor) {
                openedEditors.remove(fileId);
                return;
            }
        }

    }

    /** {@inheritDoc} */
    @Override
    public StringMap<EditorPartPresenter> getOpenedEditors() {
        return openedEditors;
    }

    /** {@inheritDoc} */
    @Override
    public EditorPartPresenter getActiveEditor() {
        return activeEditor;
    }
}
