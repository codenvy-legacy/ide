package com.codenvy.ide.jseditor.client.texteditor;

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
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument;
import com.codenvy.ide.jseditor.client.text.LinearRange;
import com.codenvy.ide.jseditor.client.text.TextPosition;
import com.codenvy.ide.jseditor.client.text.TextRange;

/**
 * Public view on the editor component.
 */
public interface TextEditor extends EditorPartPresenter {

    /**
     * Closes this text editor after optionally saving changes.
     *
     * @param save
     *         <code>true</code> if unsaved changed should be saved, and <code>false</code> if unsaved changed should be discarded
     */
    void close(boolean save);

    /**
     * Returns whether the text in this text editor can be changed by the user.
     *
     * @return <code>true</code> if it can be edited, and <code>false</code> if it is read-only
     */
    boolean isEditable();

    /**
     * Abandons all modifications applied to this text editor's input element's textual presentation since the last save operation.
     */
    void doRevertToSaved();

    /**
     * Returns the document backing the text content.
     * @return the document
     */
    EmbeddedDocument getDocument();

    /**
     * Return the content type of the editor content.<br>
     * Returns null if the type is not known yet.
     *
     * @return the content type
     */
    String getContentType();

    /**
     * Returns the selection range as a {@link TextRange} (i.e. two line, char objects: start en end).
     * @return the selection range
     */
    TextRange getSelectedTextRange();

    /**
     * Returns the selection range as a {@link LinearRange} (ie.e a start offset and a length).
     * @return the selection range
     */
    LinearRange getSelectedLinearRange();

    /**
     * Returns the cursor position as a {@link TextPosition} object (a line char position).
     * @return the cursor position
     */
    TextPosition getCursorPosition();

    /**
     * Returns the cursor position as an offset from the start.
     * @return the cursor position
     */
    int getCursorOffset();

    /**
     * Displays a message to the user.
     * @param message message
     */
    void showMessage(String message);
}
