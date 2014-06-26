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
package com.codenvy.ide.texteditor.selection;


import com.codenvy.ide.texteditor.api.CursorModel;
import com.codenvy.ide.util.ListenerRegistrar.Remover;

/**
 * Interface for a {@link CursorModel} that also have {@link CursorHandler}s.
 * 
 * @author "Mickaël Leduque"
 */
public interface CursorModelWithHandler extends CursorModel {

    Remover addCursorHandler(CursorHandler handler);

    public interface CursorHandler {
        void onCursorChange(int line, int column, boolean isExplicitChange);
    }
}
