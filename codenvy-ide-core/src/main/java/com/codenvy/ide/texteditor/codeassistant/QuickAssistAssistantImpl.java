/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.texteditor.codeassistant;

import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.texteditor.api.codeassistant.CompletionProposal;
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
        public CompletionProposal[] computeCompletionProposals(TextEditorPartView view, int offset) {
            return processor.computeQuickAssistProposals(new TextInvocationContext(view, offset, -1));
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
