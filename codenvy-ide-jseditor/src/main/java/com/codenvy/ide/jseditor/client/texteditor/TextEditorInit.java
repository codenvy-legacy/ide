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

import com.codenvy.ide.jseditor.client.document.DocumentHandle;
import com.codenvy.ide.jseditor.client.editorconfig.TextEditorConfiguration;
import com.codenvy.ide.jseditor.client.events.DocumentReadyEvent;
import com.codenvy.ide.jseditor.client.events.doc.DocReadyWrapper;
import com.codenvy.ide.jseditor.client.events.doc.DocReadyWrapper.DocReadyInit;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Initialization controller for the text editor.
 * Sets-up (when available) the different components that depend on the document being ready.
 */
public class TextEditorInit {

    private final TextEditorConfiguration configuration;
    private final EventBus generalEventBus;
    private final EditorHandle editorHandle;

    public TextEditorInit(final TextEditorConfiguration configuration,
                          final EventBus generalEventBus,
                          final EditorHandle editorHandle) {
        this.configuration = configuration;
        this.generalEventBus = generalEventBus;
        this.editorHandle = editorHandle;
    }

    /**
     * Initialize the text editor.
     * Sets itself as {@link DocumentReadyEvent} handler.
     */
    public void init() {

        final DocReadyInit<TextEditorInit> init = new DocReadyInit<TextEditorInit>() {

            @Override
            public void initialize(final DocumentHandle documentHandle, final TextEditorInit wrapped) {

            }
        };
        new DocReadyWrapper<TextEditorInit>(generalEventBus, this.editorHandle, init, this);
    }

}
