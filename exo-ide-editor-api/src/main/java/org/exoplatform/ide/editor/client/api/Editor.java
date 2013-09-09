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
package org.exoplatform.ide.editor.client.api;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;

import org.exoplatform.ide.editor.client.api.event.*;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * This is abstract Editor for Codenvy<br>
 * Editor - a visual component designed to display and edit content file.<br>
 * Furthermore the editor may support additional features (capabilities), such as:
 * <p/>
 * <li>Syntax coloring ; <li>Validation Code (according to the syntax file to be edited); <li>CodeAssistant (autocomlation,
 * viewing documentation to the code, etc.); <li>Deliver a set of content dependent tokens for alternative interviews (for example
 * CodeOutline);
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Editor Feb 9, 2011 4:24:07 PM evgen $
 */
public interface Editor extends IsWidget {

    /**
     * Get mime type.
     *
     * @return mime type associated with this editor
     */
    String getMimeType();

    /**
     * Get name of editor.
     *
     * @return
     */
    String getName();

    /** @return unique identifier which can be used to found out editor instance in the DOM */
    String getId();

    /** @return content of editor */
    String getText();

    void setText(String text);

    void setFile(FileModel file);

    /** @return content of editor */
    IDocument getDocument();

    /**
     * Check that editor support feature
     *
     * @param capability
     * @return true if editor capable do.
     */
    boolean isCapable(EditorCapability capability);

    /** indents code according to content type */
    void formatSource();

    /**
     * Displays line numbers if showLineNumbers = true, or hides otherwise
     *
     * @param showLineNumbers
     */
    void showLineNumbers(boolean showLineNumbers);

    /** Sets focus into the editor area and displays cursor. */
    void setFocus();

    /**
     * Moves cursor to specified position (row, column). If there are now such row or column in the specified row in the text, then cursor
     * will be stayed as it.
     *
     * @param row
     * @param column
     */
    void setCursorPosition(int row, int column);

    /**
     * Delete line content at cursor
     *
     * @deprecated
     */
    void deleteCurrentLine();

    /**
     * Asynchronous search query in editor.
     *
     * @param query
     *         query to search
     * @param caseSensitive
     *         is case sensitive
     * @param searchCompleteCallback
     *         search complete callback
     */
    void search(String query, boolean caseSensitive, SearchCompleteCallback searchCompleteCallback);

    /**
     * Replace match with a new value
     *
     * @param replacement
     *         new value
     */
    void replaceMatch(String replacement);

    /** @return <b>true</b> if there are any changes which can be undo in editor */
    boolean hasUndoChanges();

    /** undo latest change of content */
    void undo();

    /** @return <b>true</b> if there are any changes which can be redo in editor. */
    boolean hasRedoChanges();

    /** redo latest change of content */
    void redo();

    /** @return <b>true</b> if content is read-only */
    boolean isReadOnly();

    /**
     * Switches editor to read-only mode.
     *
     * @param readOnly
     */
    void setReadOnly(boolean readOnly);

    /**
     * Get cursor row
     *
     * @return number of row with cursor
     */
    int getCursorRow();

    /**
     * Get cursor column
     *
     * @return number of column with cursor
     */
    int getCursorColumn();

    /**
     * Replaces current line content and set, in this line, cursor position
     *
     * @deprecated KILL
     */
    void replaceTextAtCurrentLine(String line, int cursorPosition);

    /**
     * Get text of specified line
     *
     * @param line
     *         line number. <b>Must be larger 0 and less the file line count</b>
     * @return String content of line
     * @deprecated
     */
    String getLineText(int line);

    /**
     * Sets new text at specified line
     *
     * @param line
     *         line number
     * @param text
     *         new text
     * @deprecated
     */
    void setLineText(int line, String text);

    /**
     * Returns the number of lines in document
     *
     * @return number of lines in document
     * @deprecated Use {@link IDocument#getNumberOfLines()}
     */
    int getNumberOfLines();

    /**
     * Get the range of the selection.
     *
     * @return {@link SelectionRange} range of the selection
     */
    SelectionRange getSelectionRange();

    /**
     * Selects specified range
     *
     * @param startLine
     *         start line
     * @param startChar
     *         start character
     * @param endLine
     *         end line
     * @param endChar
     *         end character
     */
    void selectRange(int startLine, int startChar, int endLine, int endChar);

    /** Select all text in editor. */
    void selectAll();

    /** Cut selected text in editor. */
    void cut();

    /** Copy selected text in editor. */
    void copy();

    /** Paste text to editor. */
    void paste();

    /** Delete selected text in editor. */
    void delete();

    /**
     * Get absolute position of
     *
     * @return
     */
    int getCursorOffsetLeft();

    /** @return  */
    int getCursorOffsetTop();

    /** Collapse a fold. Depends on the current caret position. */
    void collapse();

    /** Expand a fold. Depends on the current caret position. */
    void expand();

    /** Collapse all existing folds in the editor. */
    void collapseAll();

    /** Expand all existing folds in the editor. */
    void expandAll();

    /** Fold custom selected text block in the editor. */
    void foldSelection();

    /**
     * @param handler
     * @return
     */
    HandlerRegistration addContentChangedHandler(EditorContentChangedHandler handler);

    /**
     * @param handler
     * @return
     */
    HandlerRegistration addContextMenuHandler(EditorContextMenuHandler handler);

    /**
     * @param handler
     * @return
     */
    HandlerRegistration addCursorActivityHandler(EditorCursorActivityHandler handler);

    /**
     * @param handler
     * @return
     */
    HandlerRegistration addFocusReceivedHandler(EditorFocusReceivedHandler handler);

    /**
     * @param handler
     * @return
     */
    HandlerRegistration addHotKeyPressedHandler(EditorHotKeyPressedHandler handler);

    /**
     * @param handler
     * @return
     */
    HandlerRegistration addInitializedHandler(EditorInitializedHandler handler);

    // ??????????????????????????
    //   listen MouseMoveEvent
    //   row, column, mouseX, mouseY

}
