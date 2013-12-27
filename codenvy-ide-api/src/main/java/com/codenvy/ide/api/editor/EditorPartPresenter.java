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

import com.codenvy.ide.api.ui.workspace.PartPresenter;

import javax.validation.constraints.NotNull;

/**
 * An editor is a visual component.
 * It is typically used to edit or browse a document or input object. The input
 * is identified using an <code>EditorInput</code>.  Modifications made
 * in an editor part follow an open-save-close lifecycle model
 * <p>
 * An editor is document or input-centric.  Each editor has an input, and only
 * one editor can exist for each editor input within a page.
 * </p>
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface EditorPartPresenter extends PartPresenter {
    public interface EditorPartCloseHandler {
        void onClose(EditorPartPresenter editor);
    }

    /** The property id for <code>isDirty</code>. */
    int PROP_DIRTY = 0x101;

    /** The property id for editor input changed. */
    int PROP_INPUT = 0x102;

    /**
     * Initializes this editor with the given input.
     * <p>
     * This method is automatically called shortly after the part is instantiated.
     * It marks the start of the part's lifecycle.
     * <p>
     * Implementors of this method must examine the editor input object type to
     * determine if it is understood.  If not, the implementor must throw
     * a <code>PartInitException</code>
     * </p>
     *
     * @param input
     *         the editor input
     * @throws EditorInitException
     *         if this editor was not initialized successfully
     */
    void init(@NotNull EditorInput input) throws EditorInitException;

    /**
     * Returns the input for this editor.  If this value changes the part must
     * fire a property listener event with <code>PROP_INPUT</code>.
     *
     * @return the editor input
     */
    @NotNull
    EditorInput getEditorInput();

    /** Saves the contents of this editor. */
    void doSave();

    /** Saves the contents of this part to another object. */
    void doSaveAs();

    /**
     * Returns whether the contents of this part have changed since the last save
     * operation.
     *
     * @return <code>true</code> if the contents have been modified and need
     *         saving, and <code>false</code> if they have not changed since the last
     *         save
     */
    boolean isDirty();

    /**
     * Add EditorPart close handler.
     *
     * @param closeHandler
     *         the instance of CloseHandler
     */
    void addCloseHandler(@NotNull EditorPartCloseHandler closeHandler);

    /** Call this method then editor became visible */
    void activate();
}
