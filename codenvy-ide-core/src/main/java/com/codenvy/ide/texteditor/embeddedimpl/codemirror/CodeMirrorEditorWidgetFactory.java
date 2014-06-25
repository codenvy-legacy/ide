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
package com.codenvy.ide.texteditor.embeddedimpl.codemirror;

import com.codenvy.ide.text.Document;

/**
 * Interface for factories of {@link CodeMirrorEditorWidget}.
 * 
 * @author "Mickaël Leduque"
 */
public interface CodeMirrorEditorWidgetFactory {

    /**
     * Create an instance of {@link CodeMirrorEditorWidget}.
     * 
     * @param editorMode the language mode of the editor
     * @param document the displayed document
     * @return an instance of {@link CodeMirrorEditorWidget}
     */
    CodeMirrorEditorWidget createEditorWidget(final String editorMode, final Document document);
}
