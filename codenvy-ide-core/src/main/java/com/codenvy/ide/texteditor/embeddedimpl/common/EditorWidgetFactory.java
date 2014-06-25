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

import com.codenvy.ide.text.Document;

/**
 * Interface for {@link EditorWidget} factories.
 * 
 * @author "Mickaël Leduque"
 */
public interface EditorWidgetFactory {

    /**
     * Create an editor instance.
     * 
     * @param editorMode the editor mode
     * @param document the document for the editor
     * @return an editor instance
     */
    EditorWidget createEditorWidget(final String editorMode, final Document document);
}
