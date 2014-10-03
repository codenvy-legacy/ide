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


/**
 * Interface for {@link EditorWidget} factories.
 *
 * @author "Mickaël Leduque"
 */
public interface EditorWidgetFactory<T extends EditorWidget> {

    /**
     * Create an editor instance.
     *
     * @param editorMode
     *         the editor mode
     * @return an editor instance
     */
    T createEditorWidget(final String editorMode);
}
