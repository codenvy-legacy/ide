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
package com.codenvy.ide.core.editor;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorInitException;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorPartPresenter.EditorPartCloseHandler;
import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.event.ActivePartChangedEvent;
import com.codenvy.ide.api.event.ActivePartChangedHandler;
import com.codenvy.ide.api.event.WindowActionEvent;
import com.codenvy.ide.api.event.WindowActionHandler;
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
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.texteditor.TextEditorPresenter;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;


/** @author Evgen Vidolob */
@Singleton
public class EditorAgentImpl implements EditorAgent {

    private final StringMap<EditorPartPresenter> openedEditors;
    /** Used to notify {@link EditorAgentImpl} that editor has closed */
    private final EditorPartCloseHandler   editorClosed             = new EditorPartCloseHandler() {
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
    private final FileEventHandler         fileEventHandler         = new FileEventHandler() {
        @Override
        public void onFileOperation(final FileEvent event) {
            if (event.getOperationType() == FileOperation.OPEN) {
                openEditor(event.getFile());
            } else if (event.getOperationType() == FileOperation.CLOSE) {
                // close associated editor. OR it can be closed itself TODO
            }
        }
    };
    private final WindowActionHandler      windowActionHandler      = new WindowActionHandler() {
        @Override
        public void onWindowClosing(final WindowActionEvent event) {
            openedEditors.iterate(new StringMap.IterationCallback<EditorPartPresenter>() {
                @Override
                public void onIteration(String s, EditorPartPresenter editorPartPresenter) {
                    if (editorPartPresenter.isDirty()) {
                        event.setMessage(coreLocalizationConstant.changesMayBeLost());
                    }
                }
            });
        }

        @Override
        public void onWindowClosed(WindowActionEvent event) {
        }
    };
    private final WorkspaceAgent workspace;
    private CoreLocalizationConstant coreLocalizationConstant;
    private final EventBus            eventBus;
    private       EditorRegistry      editorRegistry;
    private       ResourceProvider    provider;
    private       EditorPartPresenter activeEditor;

    @Inject
    public EditorAgentImpl(EventBus eventBus, EditorRegistry editorRegistry, ResourceProvider provider,
                           final WorkspaceAgent workspace, CoreLocalizationConstant coreLocalizationConstant) {
        super();
        this.eventBus = eventBus;
        this.editorRegistry = editorRegistry;
        this.provider = provider;
        this.workspace = workspace;
        this.coreLocalizationConstant = coreLocalizationConstant;
        openedEditors = Collections.createStringMap();

        bind();
    }

    protected void bind() {
        eventBus.addHandler(ActivePartChangedEvent.TYPE, activePartChangedHandler);
        eventBus.addHandler(FileEvent.TYPE, fileEventHandler);
        eventBus.addHandler(WindowActionEvent.TYPE, windowActionHandler);
    }

    /** {@inheritDoc} */
    @Override
    public void openEditor(@NotNull final File file) {
        if (openedEditors.containsKey(file.getPath())) {
            workspace.setActivePart(openedEditors.get(file.getPath()));
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
            openedEditors.put(file.getPath(), editor);
        }
    }

    /** @param editor */
    protected void editorClosed(EditorPartPresenter editor) {
        if (activeEditor == editor) {
            activeEditor = null;
        }
        //call close() method
        if (editor instanceof TextEditorPresenter) {
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
}
