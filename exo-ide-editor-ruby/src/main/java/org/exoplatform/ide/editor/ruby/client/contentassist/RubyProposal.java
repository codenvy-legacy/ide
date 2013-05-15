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

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperty;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
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
    private String      proposal;

    /** Proposal's additional text. */
    private String      additionalInfo;

    /** Triggering string. */
    private String      prefix;

    /** Text offset. */
    private final int   offset;

    private final Token token;

    private final int   modifieres;

    /**
     * Constructs new {@link RubyProposal} instance with the given proposal, prefix and offset.
     * 
     * @param proposal proposal's text label
     * @param additionalInfo proposal's additional text
     * @param prefix
     * @param offset text offset
     * @param token
     */
    public RubyProposal(String proposal, String additionalInfo, String prefix, int offset, Token token) {
        this.proposal = proposal;
        this.additionalInfo = additionalInfo;
        this.prefix = prefix;
        this.offset = offset;
        this.token = token;

        if (token.hasProperty(TokenProperties.MODIFIERS)) {
            final TokenProperty modifieresProperty = token.getProperty(TokenProperties.MODIFIERS);
            if (modifieresProperty.isNumericProperty() != null)
                modifieres = modifieresProperty.isNumericProperty().numericValue().intValue();
            else {
                modifieres = 0;
            }
        } else {
            modifieres = 0;
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
        if (token.getType() == TokenType.METHOD) {
            final String parameters = token.getProperty(TokenProperties.PARAMETER_TYPES).isStringProperty().stringValue();
            if (!parameters.equals("()")) {
                final int indexOfParameters = proposal.indexOf(parameters);
                return new Point(offset + indexOfParameters + 1, 0);
            }
        }
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
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getAdditionalDisplayString()
     */
    @Override
    public String getAdditionalDisplayString() {
        return additionalInfo;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getImage()
     */
    @Override
    public Image getImage() {
        switch (token.getType()) {
            case METHOD:
                if (modifieres == 0) {
                    return new Image(RubyClientBundle.INSTANCE.publicMethod());
                } else {
                    return new Image(RubyClientBundle.INSTANCE.defaultMethod());
                }
            case CLASS:
                return new Image(RubyClientBundle.INSTANCE.classItem());
            case CONSTANT:
                return new Image(RubyClientBundle.INSTANCE.rubyConstant());
            case VARIABLE:
            case LOCAL_VARIABLE:
                return new Image(RubyClientBundle.INSTANCE.variable());
            case INSTANCE_VARIABLE:
                return new Image(RubyClientBundle.INSTANCE.rubyObjectVariable());
            case CLASS_VARIABLE:
                return new Image(RubyClientBundle.INSTANCE.rubyClassVariable());
            case GLOBAL_VARIABLE:
                return new Image(RubyClientBundle.INSTANCE.rubyGlobalVariable());
            default:
                return new Image(RubyClientBundle.INSTANCE.blankImage());
        }
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
