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
package com.codenvy.ide.jseditor.client.codeassist;

import com.codenvy.ide.api.text.Region;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.jseditor.client.partition.DocumentPartitioner;
import com.codenvy.ide.jseditor.client.texteditor.EditorHandle;
import com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorPartView;
import com.codenvy.ide.texteditor.codeassistant.AutocompleteUiController.Resources;
import com.google.gwt.core.client.GWT;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation of CodeAssistant.
 */
public class CodeAssistantImpl implements CodeAssistant {

    private final StringMap<CodeAssistProcessor> processors;

    private final EmbeddedTextEditorPartView textEditor;

    private String lastErrorMessage;

    private final DocumentPartitioner partitioner;


    public static final Resources res = GWT.create(Resources.class);

    @AssistedInject
    public CodeAssistantImpl(@Assisted final DocumentPartitioner partitioner,
                             @Assisted EditorHandle editorHandle) {
        processors = Collections.createStringMap();
        res.defaultSimpleListCss().ensureInjected();
        res.autocompleteComponentCss().ensureInjected();
        res.popupCss().ensureInjected();
        this.partitioner = partitioner;
        this.textEditor = editorHandle.getEditor();
    }

    /**
     * Returns an array of completion proposals computed based on the specified document position. The position is used to determine the
     * appropriate content assist processor to invoke.
     * 
     * @param offset a document offset
     * @return an array of completion proposals or <code>null</code> if no proposals are possible
     */
    void computeCompletionProposals(final int offset, final CodeAssistCallback callback) {
        this.lastErrorMessage = "processing";

        final CodeAssistProcessor processor = getProcessor(textEditor, offset);
        if (processor != null) {
            processor.computeCompletionProposals(textEditor, offset, callback);
            this.lastErrorMessage = processor.getErrorMessage();
            if (this.lastErrorMessage != null) {
                this.textEditor.showMessage(this.lastErrorMessage);
            }
        }
    }

    @Override
    public void computeCompletionProposals(final CodeAssistCallback callback) {

        final Region region = textEditor.getSelectedRegion();
        final int offset = region.getOffset();

        if (offset > 0) {
            computeCompletionProposals(offset, callback);
        }
    }

    /**
     * Returns the code assist processor for the content type of the specified document position.
     * 
     * @param view the text view
     * @param offset a offset within the document
     * @return a code-assist processor or <code>null</code> if none exists
     */
    private CodeAssistProcessor getProcessor(final EmbeddedTextEditorPartView view, final int offset) {
        final String contentType = view.getContentType();
        if (contentType == null) {
            return null;
        }

        final String type = this.partitioner.getContentType(offset);
        return getCodeAssistProcessor(type);
    }

    @Override
    public CodeAssistProcessor getCodeAssistProcessor(final String contentType) {
        return processors.get(contentType);
    }

    @Override
    public void setCodeAssistantProcessor(final String contentType, final CodeAssistProcessor processor) {
        processors.put(contentType, processor);
    }
}
