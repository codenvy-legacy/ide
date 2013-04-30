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

import com.codenvy.ide.client.util.logging.Log;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContextInformation;
import org.exoplatform.ide.editor.client.api.contentassist.Point;
import org.exoplatform.ide.editor.ruby.client.RubyClientBundle;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.edits.MalformedTreeException;
import org.exoplatform.ide.editor.shared.text.edits.ReplaceEdit;

/**
 * Completion proposal for Ruby.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: RubyProposal.java Apr 30, 2013 4:35:33 PM azatsarynnyy $
 */
public class RubyProposal implements CompletionProposal {

    /** Proposal's text label. */
    private String    proposal;

    /** Triggering string. */
    private String    prefix;

    /** Text offset. */
    private final int offset;

    /**
     * Constructs new {@link RubyProposal} instance with the given proposal, prefix and offset.
     * 
     * @param proposal proposal's text label
     * @param prefix
     * @param offset text offset
     */
    public RubyProposal(String proposal, String prefix, int offset) {
        this.proposal = proposal;
        this.prefix = prefix;
        this.offset = offset;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#apply(org.exoplatform.ide.editor.shared.text.IDocument)
     */
    @Override
    public void apply(IDocument document) {
        ReplaceEdit replaceEdit = new ReplaceEdit(offset - prefix.length(), prefix.length(), proposal);
        try {
            replaceEdit.apply(document);
        } catch (MalformedTreeException e) {
            Log.error(getClass(), e);
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getSelection(org.exoplatform.ide.editor.shared.text.IDocument)
     */
    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getAdditionalProposalInfo()
     */
    @Override
    public Widget getAdditionalProposalInfo() {
        return null;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getDisplayString()
     */
    @Override
    public String getDisplayString() {
        return proposal;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getImage()
     */
    @Override
    public Image getImage() {
        return new Image(RubyClientBundle.INSTANCE.blankImage());
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getContextInformation()
     */
    @Override
    public ContextInformation getContextInformation() {
        return null;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#apply(org.exoplatform.ide.editor.shared.text.IDocument,
     *      char, int)
     */
    @Override
    public void apply(IDocument document, char trigger, int offset) {
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#isValidFor(org.exoplatform.ide.editor.shared.text.IDocument,
     *      int)
     */
    @Override
    public boolean isValidFor(IDocument document, int offset) {
        return false;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getTriggerCharacters()
     */
    @Override
    public char[] getTriggerCharacters() {
        return null;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#isAutoInsertable()
     */
    @Override
    public boolean isAutoInsertable() {
        return false;
    }

}
