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

import com.codenvy.ide.text.Region;
import com.codenvy.ide.texteditor.OutlinableTextEditorView;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * View interface for the embedded editors components.
 * 
 * @author "Mickaël Leduque"
 */
public interface EmbeddedTextEditorPartView extends OutlinableTextEditorView, RequiresResize, IsWidget, HasChangeHandlers {

    boolean isDirty();

    String getContents();

    Region getSelectedRegion();
}
