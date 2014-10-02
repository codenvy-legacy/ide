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

import elemental.dom.Element;

/**
 * Interface for components that expose a gutter.
 */
public interface HasGutter {
    /**
     * Adds a gutter item.
     *
     * @param line the line for the item
     * @param gutterId the gutter identifier
     * @param element the (DOM) element to add
     */
    void addGutterItem(int line, String gutterId, Element element);
    /**
     * Adds a gutter item.
     *
     * @param line the line for the item
     * @param gutterId the gutter identifier
     * @param element the (DOM) element to add
     * @param lineCallback callback to call when the line is removed
     */
    void addGutterItem(int line, String gutterId, Element element, LineNumberingChangeCallback lineCallback);

    /**
     * Remove a gutter item.
     * @param line the line of the item
     * @param gutterId the gutter
     */
    void removeGutterItem(int line, String gutterId);

    /**
     * Returns the gutter item at th given line for the given gutter (if present).
     * @param line the line
     * @param gutterId the gutter
     * @return the gutter element or null
     */
    Element getGutterItem(int line, String gutterId);

    /**
     * Clears the given gutter. Removes all gutter items.
     *
     * @param gutterId the gutter identifier
     */
    void clearGutter(String gutterId);

    /** Callback to be warned when line numbering changes (lines are removed or inserted). */
    public interface LineNumberingChangeCallback {
        /** Method called when the line numbering changes. */
        void onLineNumberingChange(int fromLine, int linesRemoved, int linesAdded);
    }
}
