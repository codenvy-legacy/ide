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
package com.codenvy.ide.api.texteditor;

/**
 * Interface for a undo manager.
 *
 * @author Roman Nikitenko
 */
public interface UndoManager extends HandlesUndoRedo {

    /**
     * Signals the undo manager that all subsequent changes until
     * <code>endCompoundChange</code> is called are to be undone in one piece.
     */
    void beginCompoundChange();

    /**
     * Signals the undo manager that the sequence of changes which started with
     * <code>beginCompoundChange</code> has been finished. All subsequent changes
     * are considered to be individually undo-able.
     */
    void endCompoundChange();

    /**
     * The given parameter determines the maximal length of the history
     * remembered by the undo manager.
     *
     * @param undoLevel the length of this undo manager's history
     */
    void setMaximalUndoLevel(int undoLevel);

    /**
     * Connects this undo manager to the given text viewer.
     *
     * @param textViewer the viewer the undo manager is connected to
     */
    void connect(TextEditorPartView textViewer);

    /**
     * Disconnects this undo manager from its text viewer.
     * If this undo manager hasn't been connected before this
     * operation has no effect.
     */
    void disconnect();

    /**
     * Resets the history of the undo manager. After that call,
     * there aren't any undo-able or redo-able text changes.
     */
    void reset();
}
