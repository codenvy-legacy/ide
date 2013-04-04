/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.api.editor;

import com.codenvy.ide.api.ui.perspective.PartPresenter;

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
        public void onClose(EditorPartPresenter editor);
    }

    /** The property id for <code>isDirty</code>. */
    public static final int PROP_DIRTY = 0x101;

    /** The property id for editor input changed. */
    public static final int PROP_INPUT = 0x102;

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
    public void init(EditorInput input) throws EditorInitException;

    /**
     * Returns the input for this editor.  If this value changes the part must
     * fire a property listener event with <code>PROP_INPUT</code>.
     *
     * @return the editor input
     */
    public EditorInput getEditorInput();

    /** Saves the contents of this editor. */
    public void doSave();

    /** Saves the contents of this part to another object. */
    public void doSaveAs();

    /**
     * Returns whether the contents of this part have changed since the last save
     * operation.
     *
     * @return <code>true</code> if the contents have been modified and need
     *         saving, and <code>false</code> if they have not changed since the last
     *         save
     */
    public boolean isDirty();

    /**
     * Add EditorPart close handler.
     *
     * @param closeHandler
     *         the instance of CloseHandler
     */
    public void addCloseHandler(EditorPartCloseHandler closeHandler);
}
