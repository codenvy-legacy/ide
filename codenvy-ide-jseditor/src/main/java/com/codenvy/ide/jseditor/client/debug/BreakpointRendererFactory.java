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
package com.codenvy.ide.jseditor.client.debug;

import com.codenvy.ide.debug.BreakpointRenderer;
import com.codenvy.ide.jseditor.client.document.Document;
import com.codenvy.ide.jseditor.client.texteditor.HasGutter;
import com.codenvy.ide.jseditor.client.texteditor.LineStyler;

/** Factory for {@link BreakpointRenderer} instances.*/
public interface BreakpointRendererFactory {

    /**
     * Creates an instance of {@link BreakpointRenderer}.
     * @param hasGutter the gutter manager
     * @param lineStyler the line style manager
     * @param document the document
     * @return a {@link BreakpointRenderer}
     */
    BreakpointRenderer create(final HasGutter hasGutter,
                              final LineStyler lineStyler,
                              final Document document);
}
