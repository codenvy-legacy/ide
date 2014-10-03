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
package com.codenvy.ide.jseditor.client.infopanel;

import com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorPartView;

/**
 * Factory for {@link InfoPanel} instances.
 *
 * @author "Mickaël Leduque"
 */
public interface InfoPanelFactory {

    /**
     * Creates an {@link InfoPanel}
     *
     * @param editor
     *         the associated editor
     * @return a new {@link InfoPanel} instance
     */
    InfoPanel create(EmbeddedTextEditorPartView editor);
}
