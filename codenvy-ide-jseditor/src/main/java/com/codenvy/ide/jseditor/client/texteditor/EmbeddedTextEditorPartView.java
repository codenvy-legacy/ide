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
package com.codenvy.ide.jseditor.client.texteditor;

import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.text.Region;
import com.codenvy.ide.api.texteditor.HandlesTextOperations;
import com.codenvy.ide.api.texteditor.HasReadOnlyProperty;
import com.codenvy.ide.api.texteditor.IsConfigurable;
import com.codenvy.ide.api.texteditor.UndoableEditor;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument;
import com.codenvy.ide.jseditor.client.editorconfig.EmbeddedTextEditorConfiguration;
import com.codenvy.ide.texteditor.selection.HasCursorModelWithHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * View interface for the embedded editors components.
 *
 * @author "Mickaël Leduque"
 */
public interface EmbeddedTextEditorPartView extends HasCursorModelWithHandler, HasReadOnlyProperty, HandlesTextOperations,
                                                    IsConfigurable<EmbeddedTextEditorConfiguration>, RequiresResize, IsWidget,
                                                    HasChangeHandlers, UndoableEditor {

    /**
     * Tells is the editor is dirty (if changes were made since opening or since last time {@link #markClean()} was called).
     *
     * @return the dirty state
     */
    boolean isDirty();

    /**
     * Marks the editor as clean (not dirty).
     */
    void markClean();

    /**
     * Returns the whole editor contents as string.
     *
     * @return the contents
     */
    String getContents();

    /**
     * Sets the editor contents.
     *
     * @param contents the new contents
     */
    void setContents(String contents);

    /**
     * Returns an object that describes the current selection (primary selection if the editor implementation supports multiple selection).
     *
     * @return the selection
     */
    Region getSelectedRegion();

    /**
     * Configures the editor.
     *
     * @param configuration the configuration object
     * @param file the file object
     */
    void configure(EmbeddedTextEditorConfiguration configuration, FileNode file);

    /**
     * Returns the instance of embedded document for this editor.
     *
     * @return the document
     */
    EmbeddedDocument getEmbeddedDocument();

    /** Gives the focus to the editor. */
    void setFocus();


    /**
     * Returns a handle for this editor view.
     * @return an editor handle
     */
    EditorHandle getEditorHandle();

    /**
     * Replaces the selection by the given range.
     *
     * @param region the new selection
     */
    void setSelectedRegion(Region region);

    /**
     * Replaces the selection by the given range and optionally scrolls the editor to show the range.
     *
     * @param region the new selection
     * @param show scroll to show iff value is true
     */
    void setSelectedRegion(Region region, boolean show);

}
