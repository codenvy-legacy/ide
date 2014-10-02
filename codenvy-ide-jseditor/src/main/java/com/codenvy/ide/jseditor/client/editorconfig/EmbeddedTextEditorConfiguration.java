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
package com.codenvy.ide.jseditor.client.editorconfig;

import com.codenvy.ide.api.texteditor.outline.OutlineModel;

import javax.annotation.Nullable;

public interface EmbeddedTextEditorConfiguration {

    /**
     * Returns the visual width of the tab character. This implementation always returns 3.
     *
     * @return the tab width
     */
    public int getTabWidth();

    /**
     * Return the outline model. This implementation always returns <code>null</code>.
     *
     * @return a model that used to build outline tree.
     */
    @Nullable
    public OutlineModel getOutline();

}
