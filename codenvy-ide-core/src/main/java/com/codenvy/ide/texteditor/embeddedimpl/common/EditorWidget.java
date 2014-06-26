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
package com.codenvy.ide.texteditor.embeddedimpl.common;

import com.codenvy.ide.text.Region;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * An interface for editor widget implementations.
 * 
 * @author "Mickaël Leduque"
 */
public interface EditorWidget extends IsWidget, HasChangeHandlers {

    /**
     * Returns the contents of the editor.
     * 
     * @return
     */
    String getValue();

    /**
     * Sets the content of the editor.
     * 
     * @param newValue the new contents
     */
    void setValue(String newValue);

    /**
     * Sets the language mode for highlighting.
     * 
     * @param modeName the new mode
     */
    void setMode(String modeName);

    /**
     * Change readonly state of the editor.
     * 
     * @param isReadOnly true to set the editor in readonly mode, false to allow edit
     */
    void setReadOnly(boolean isReadOnly);

    /**
     * Returns the readonly state of the editor.
     * 
     * @return the readonly state, true iff the editor is readonly
     */
    boolean isReadOnly();

    /**
     * Returns the dirty state of the editor.
     * 
     * @return true iff the editor is dirty (i.e. unsaved change were made)
     */
    boolean isDirty();

    /** Marks the editor as clean i.e change the dirty state to false. */
    void markClean();

    /**
     * The instance of {@link EmbeddedDocument}.
     * 
     * @return the embedded document
     */
    EmbeddedDocument getDocument();

    /**
     * Returns the selected range in the editor. In case of multiple selection support, returns the primary selection. When no actual
     * selection is done, a selection with a zero length is given
     * 
     * @return the selected range
     */
    Region getSelectedRange();
}
