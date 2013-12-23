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
package com.codenvy.ide.api.editor;

import com.codenvy.ide.api.ui.workspace.AbstractPartPresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.user.client.Window;

import javax.validation.constraints.NotNull;

/**
 * Abstract implementation of {@link EditorPartPresenter} that is intended to be used by subclassing instead of directly implementing an
 * interface.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
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
        if (isDirty()) {
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
}
