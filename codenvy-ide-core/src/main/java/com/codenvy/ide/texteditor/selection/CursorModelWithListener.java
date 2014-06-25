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


import com.codenvy.ide.text.store.LineInfo;
import com.codenvy.ide.texteditor.api.CursorModel;
import com.codenvy.ide.util.ListenerRegistrar.Remover;

/**
 * Interface for a {@link CursorModel} that also have {@link CursorListener}s.
 * 
 * @author "Mickaël Leduque"
 */
public interface CursorModelWithListener extends CursorModel {
    /**
     * Add a cursor listener.
     * 
     * @param listener the listener
     * @return a handle to remove the listener
     */
    Remover addCursorListener(CursorListener listener);

    /** Listener that is called when the user's cursor changes position. */
    public interface CursorListener {
        /**
         * @param isExplicitChange true if this change was a result of either the user moving his cursor or through programatic setting, or
         *            false if it was caused by text mutations in the document
         */
        void onCursorChange(LineInfo lineInfo, int column, boolean isExplicitChange);
    }
}
