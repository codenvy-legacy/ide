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

import com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorPartView;

/**
 * An <code>CodeAssistant</code> provides support on interactive content completion. The content assistant is a
 * {@link EmbeddedTextEditorPartView} add-on.
 * <p>
 * A content assistant has a list of {@link com.codenvy.ide.api.texteditor.codeassistant.CodeAssistProcessor} objects each of which is
 * registered for a particular document content type. The content assistant uses the processors to react on the request of completing
 * documents or presenting context information.
 * </p>
 */
public interface CodeAssistant {

    /**
     * Returns the code assist processor to be used for the given content type.
     *
     * @param contentType the type of the content for which this content assistant is to be requested
     * @return an instance code assist processor or <code>null</code> if none exists for the specified content type
     */
    CodeAssistProcessor getCodeAssistProcessor(String contentType);

    /**
     * Registers a content processor for the given content type.
     *
     * @param contentType the content type
     * @param processor the processor
     */
    void setCodeAssistantProcessor(String contentType, CodeAssistProcessor processor);

    /**
     * Request to compute the proposals.
     * @param callback what to do when the proposals are ready
     */
    void computeCompletionProposals(CodeAssistCallback callback);

}
