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

import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument;
import com.codenvy.ide.jseditor.client.editorconfig.EmbeddedTextEditorConfiguration;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.texteditor.api.HandlesTextOperations;
import com.codenvy.ide.texteditor.api.HasReadOnlyProperty;
import com.codenvy.ide.texteditor.api.IsConfigurable;
import com.codenvy.ide.texteditor.selection.HasCursorModelWithHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * View interface for the embedded editors components.
 * 
 * @author "Mickaël Leduque"
 */
public interface EmbeddedTextEditorPartView extends HasCursorModelWithHandler, HasReadOnlyProperty, HandlesTextOperations,
                                           IsConfigurable<EmbeddedTextEditorConfiguration>, RequiresResize, IsWidget, HasChangeHandlers {

    boolean isDirty();

    void markClean();

    String getContents();

    void setContents(String contents);


    Region getSelectedRegion();

    void configure(EmbeddedTextEditorConfiguration configuration, File file);

    EmbeddedDocument getEmbeddedDocument();

}
