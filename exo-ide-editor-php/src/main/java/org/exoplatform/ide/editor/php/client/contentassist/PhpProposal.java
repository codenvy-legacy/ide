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
package org.exoplatform.ide.editor.php.client.contentassist;

import com.codenvy.ide.client.util.logging.Log;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.api.codeassitant.Modifier;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContextInformation;
import org.exoplatform.ide.editor.client.api.contentassist.Point;
import org.exoplatform.ide.editor.php.client.PhpClientBundle;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.edits.MalformedTreeException;
import org.exoplatform.ide.editor.shared.text.edits.ReplaceEdit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Completion proposal for PHP.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: PhpCompletionProposal.java Apr 16, 2013 12:56:54 PM azatsarynnyy $
 *
 */
public class PhpProposal implements CompletionProposal {

    /** Proposal's text label. */
    private String proposal;

    /** Triggering string. */
    private String prefix;

    /** Text offset. */
    private final int offset;

    private final TokenType tokenType;

    private List<Modifier> modifieres = new ArrayList<Modifier>();

    /**
     * Constructs new {@link PhpProposal} instance with the given proposal, prefix and offset.
     * 
     * @param proposal
     *         proposal text label
     * @param prefix
     * @param offset
     * @param token
     */
    @SuppressWarnings("unchecked")
    public PhpProposal(String proposal, String prefix, int offset, Token token) {
        this.proposal = proposal;
        this.prefix = prefix;
        this.offset = offset;
        this.tokenType = token.getType();

        if (token.hasProperty(TokenProperties.MODIFIERS)) {
            modifieres.addAll((Collection<Modifier>)token.getProperty(TokenProperties.MODIFIERS).isObjectProperty().objectValue());
        }
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

    /** @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getAdditionalProposalInfo() */
    @Override
    public Widget getAdditionalProposalInfo() {
        return null;
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getDisplayString() */
    @Override
    public String getDisplayString() {
        return proposal;
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getImage() */
    @Override
    public Image getImage() {
        switch (tokenType) {
            case FUNCTION:
            case METHOD:
                return new Image(PhpClientBundle.INSTANCE.publicMethod());
            case PROPERTY:
                if (modifieres.contains(Modifier.PRIVATE)) {
                    return new Image(PhpClientBundle.INSTANCE.privateField());
                } else if (modifieres.contains(Modifier.PROTECTED)) {
                    return new Image(PhpClientBundle.INSTANCE.protectedField());
                } else {
                    return new Image(PhpClientBundle.INSTANCE.publicField());
                }
            case CONSTANT:
            case CLASS_CONSTANT:
                return new Image(PhpClientBundle.INSTANCE.constantItem());
            case PARAMETER:
            case VARIABLE:
            case LOCAL_VARIABLE:
                return new Image(PhpClientBundle.INSTANCE.variable());
            case INTERFACE:
                return new Image(PhpClientBundle.INSTANCE.interfaceItem());
            case CLASS:
                return new Image(PhpClientBundle.INSTANCE.classItem());
            case KEYWORD:
            default:
                return new Image(PhpClientBundle.INSTANCE.blankImage());
        }
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getContextInformation() */
    @Override
    public ContextInformation getContextInformation() {
        return null;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#apply(org.exoplatform.ide.editor.shared.text.IDocument, char, int)
     */
    @Override
    public void apply(IDocument document, char trigger, int offset) {
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#isValidFor(org.exoplatform.ide.editor.shared.text.IDocument, int)
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
