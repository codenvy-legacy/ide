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
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.user.client.Window;

/**
 * Abstract implementation of {@link EditorPartPresenter} that is intended to be used by subclassing
 * instead of directly implementing an interface.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public abstract class AbstractEditorPresenter extends AbstractPartPresenter implements EditorPartPresenter {

    protected boolean dirtyState;

    protected EditorInput input;

    protected final JsonArray<EditorPartCloseHandler> closeHandlers = JsonCollections.createArray();

    /** {@inheritDoc} */
    @Override
    public void init(EditorInput input) throws EditorInitException {
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

    /** @see com.codenvy.ide.api.editor.EditorPartPresenter#isDirty() */
    @Override
    public boolean isDirty() {
        return dirtyState;
    }

    /** {@inheritDoc} */
    @Override
    public void addCloseHandler(EditorPartCloseHandler closeHandler) {
        if (!closeHandlers.contains(closeHandler)) {
            closeHandlers.add(closeHandler);
        }
    }

    /** {@inheritDoc} */
    @Override
    public EditorInput getEditorInput() {
        return input;
    }

    /** @see com.codenvy.ide.api.ui.workspace.PartPresenter#onClose() */
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
