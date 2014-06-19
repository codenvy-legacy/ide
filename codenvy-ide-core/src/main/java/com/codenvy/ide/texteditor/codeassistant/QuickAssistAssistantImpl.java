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
package com.codenvy.ide.texteditor.codeassistant;

import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.CodeAssistCallback;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.texteditor.api.quickassist.QuickAssistAssistant;
import com.codenvy.ide.texteditor.api.quickassist.QuickAssistProcessor;

/**
 * Default implementation of {@link QuickAssistAssistant}
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class QuickAssistAssistantImpl implements QuickAssistAssistant {

    class QuickAssistImpl extends CodeAssistantImpl {
        public void closeBox() {
            super.closeBox();
        }
    }

    class CodeAssistProcessorImpl implements CodeAssistProcessor {

        private QuickAssistProcessor processor;

        /** @param processor */
        public CodeAssistProcessorImpl(QuickAssistProcessor processor) {
            this.processor = processor;
        }

        /** {@inheritDoc} */
        @Override
        public void computeCompletionProposals(TextEditorPartView view, int offset, CodeAssistCallback callback) {
            processor.computeQuickAssistProposals(new TextInvocationContext(view, offset, -1), callback);
        }

        /** {@inheritDoc} */
        @Override
        public char[] getCompletionProposalAutoActivationCharacters() {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public String getErrorMessage() {
            return null;
        }

    }

    private QuickAssistImpl fQuickAssistAssistantImpl;

    private QuickAssistProcessor fQuickAssistProcessor;

    /**
     *
     */
    public QuickAssistAssistantImpl() {
        fQuickAssistAssistantImpl = new QuickAssistImpl();
    }

    /** {@inheritDoc} */
    @Override
    public void install(TextEditorPartView textEditor) {
        fQuickAssistAssistantImpl.install(textEditor);
    }

    /** {@inheritDoc} */
    @Override
    public void uninstall() {
        fQuickAssistAssistantImpl.uninstall();
    }

    /** {@inheritDoc} */
    @Override
    public String showPossibleQuickAssists() {
        return fQuickAssistAssistantImpl.showPossibleCompletions();
    }

    /** {@inheritDoc} */
    @Override
    public void setQuickAssistProcessor(QuickAssistProcessor processor) {
        fQuickAssistProcessor = processor;
        fQuickAssistAssistantImpl.setCodeAssistantProcessor(Document.DEFAULT_CONTENT_TYPE, new CodeAssistProcessorImpl(
                processor));
    }

    /** {@inheritDoc} */
    @Override
    public QuickAssistProcessor getQuickAssistProcessor() {
        return fQuickAssistProcessor;
    }

    /** Hides any open pop-ups */
    protected void hide() {
        fQuickAssistAssistantImpl.closeBox();
    }

}
