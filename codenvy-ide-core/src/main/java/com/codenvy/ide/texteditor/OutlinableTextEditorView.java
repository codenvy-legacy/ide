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
package com.codenvy.ide.texteditor;

import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.selection.CursorModelWithHandler;

/**
 * Interface for text editors that can have an outline.
 * 
 * @author "Mickaël Leduque"
 */
public interface OutlinableTextEditorView extends TextEditorPartView {

    CursorModelWithHandler getCursorModel();

}
