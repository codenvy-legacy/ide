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
package com.codenvy.ide.texteditor.embeddedimpl.common.preference;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.core.editor.EditorType;

/**
 * View interface for the preference page for the editor implementation selection.
 * 
 * @author "Mickaël Leduque"
 */
public interface EditorTypePreferenceView extends View<EditorTypePreferenceView.ActionDelegate> {

    /**
     * Changes the editor type selected in the view.
     * 
     * @param editorType the new displayed value
     */
    void setEditorType(EditorType editorType);

    /**
     * Action delegate for the EditorType preference view.
     * 
     * @author "Mickaël Leduque"
     */
    public interface ActionDelegate {
        /**
         * Action triggered when an editor type selection occurs.
         * 
         * @param selection the new selection
         */
        void editorTypeSelected(EditorType selection);
    }
}
