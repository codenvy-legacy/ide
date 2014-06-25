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

import com.codenvy.ide.debug.BreakpointGutterManager;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.util.executor.UserActivityManager;

/**
 * Interface for {@link EmbeddedTextEditorPartView} factories.
 * 
 * @author "Mickaël Leduque"
 */
public interface EmbeddedTextEditorViewFactory {

    /**
     * Creates an instance of {@link EmbeddedTextEditorPartView}.
     * 
     * @param resources
     * @param userActivityManager
     * @param breakpointGutterManager
     * @param dtoFactory
     * @return
     */
    EmbeddedTextEditorPartView createTextEditorPartView(com.codenvy.ide.Resources resources,
                                                        UserActivityManager userActivityManager,
                                                        BreakpointGutterManager breakpointGutterManager,
                                                        DtoFactory dtoFactory);
}
