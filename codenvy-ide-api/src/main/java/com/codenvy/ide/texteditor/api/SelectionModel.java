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
package com.codenvy.ide.texteditor.api;

import com.codenvy.ide.text.Position;

/**
 * A interface that models the user's selection.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface SelectionModel {
    /** Clear selection */
    void deselect();

    /** @return true if editor has selection */
    boolean hasSelection();

    /** Select all test in editor. */
    void selectAll();

    /**
     * Get selected range
     *
     * @return the selected range
     */
    Position getSelectedRange();

    /**
     * Move cursor to offset.
     *
     * @param offset
     *         the offset
     */
    void setCursorPosition(int offset);

    /**
     * Select and reveal text in editor
     *
     * @param offset
     *         the offset, start selection
     * @param length
     *         the length of the selection
     */
    void selectAndReveal(int offset, int length);

    /**
     * Get cursor position
     *
     * @return the position of cursor.
     */
    Position getCursorPosition();
}
