/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.editor.ruby.client.contentassist;

import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.client.api.contentassist.ContextInformation;

/**
 * A {@link RubyContentAssistProcessor} proposes completions and computes context information for Ruby content.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: RubyContentAssistProcessor.java Apr 30, 2013 4:30:39 PM azatsarynnyy $
 */
public class RubyContentAssistProcessor implements ContentAssistProcessor {

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#computeCompletionProposals(org.exoplatform.ide.editor.client.api.Editor,
     *      int)
     */
    @Override
    public CompletionProposal[] computeCompletionProposals(Editor viewer, int offset) {
        // TODO
        CompletionProposal[] proposals = new CompletionProposal[10];
        for (int i = 0; i < proposals.length; i++) {
            proposals[i] = new RubyProposal("proposal " + i, "", offset);
        }
        return proposals;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#computeContextInformation(org.exoplatform.ide.editor.client.api.Editor,
     *      int)
     */
    @Override
    public ContextInformation[] computeContextInformation(Editor viewer, int offset) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
     */
    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getContextInformationAutoActivationCharacters()
     */
    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getErrorMessage()
     */
    @Override
    public String getErrorMessage() {
        return null;
    }

}
