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
package com.codenvy.ide.api.editor;

import com.codenvy.ide.api.ui.workspace.AbstractPartPresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.user.client.Window;

import javax.validation.constraints.NotNull;

/**
 * Abstract implementation of {@link EditorPartPresenter} that is intended
 * to be used by subclassing instead of directly implementing an interface.
 *
 * @author Evgen Vidolob
 */
public abstract class AbstractEditorPresenter extends AbstractPartPresenter implements EditorPartPresenter {
    protected boolean     dirtyState;
    protected EditorInput input;
    protected final Array<EditorPartCloseHandler> closeHandlers = Collections.createArray();

    /** {@inheritDoc} */
    @Override
    public void init(@NotNull EditorInput input) throws EditorInitException {
        this.input = input;
        initializeEditor();
    }

    /** Initializes this editor. */
    protected abstract void initializeEditor();

    /**
     * Set dirty state and notify expressions
     *
     * @param dirty
     */
    protected void updateDirtyState(boolean dirty) {
        dirtyState = dirty;
        firePropertyChange(EditorPartPresenter.TITLE_PROPERTY);
        firePropertyChange(EditorPartPresenter.PROP_DIRTY);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDirty() {
        return dirtyState;
    }

    /** {@inheritDoc} */
    @Override
    public void addCloseHandler(@NotNull EditorPartCloseHandler closeHandler) {
        if (!closeHandlers.contains(closeHandler)) {
            closeHandlers.add(closeHandler);
        }
    }

    /** {@inheritDoc} */
    @Override
    public EditorInput getEditorInput() {
        return input;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onClose() {
        if (isDirty() && getEditorInput().getFile().getProject() != null) {
            if (Window.confirm("'" + getEditorInput().getName() + "' has been modified. Save changes?")) {
                doSave();
            }
        }
        handleClose();
        return true;
    }

    protected void handleClose() {
        for (int i = 0; i < closeHandlers.size(); i++) {
            EditorPartCloseHandler handler = closeHandlers.get(i);
            handler.onClose(this);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void onFileChanged() {
        firePropertyChange(TITLE_PROPERTY);
    }
}
