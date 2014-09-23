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
package com.codenvy.ide.jseditor.client.preference.editorselection;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.jseditor.client.editortype.EditorType;

/**
 * View interface for the editor selection section of the editor preference page. */
public interface EditorSelectionPreferenceView extends View<EditorSelectionPreferenceView.ActionDelegate> {


    /** Action delegate for the editor type preference section. */
    public interface ActionDelegate {

        /**
         * Action triggered when the default editor is changed.
         *
         * @param editorType the editor type
         */
        void defaultEditorChanged(EditorType editor);

        EditorType getConfiguredDefaultEditor();
    }
}
