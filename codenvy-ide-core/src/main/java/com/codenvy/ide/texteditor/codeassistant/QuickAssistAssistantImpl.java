/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
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
