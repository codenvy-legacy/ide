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
package com.codenvy.ide.outline;

import com.codenvy.ide.texteditor.api.SelectionModel;
import com.codenvy.ide.texteditor.selection.CursorModelWithListener.CursorListener;
import com.codenvy.ide.util.ListenerRegistrar;

/**
 * Interface for {@link SelectionModel}s that have a registrar for CursorListeners.
 * 
 * @author "Mickaël Leduque"
 */
public interface OutlinableSelectionModel extends SelectionModel {

    ListenerRegistrar<CursorListener> getCursorListenerRegistrar();
}
