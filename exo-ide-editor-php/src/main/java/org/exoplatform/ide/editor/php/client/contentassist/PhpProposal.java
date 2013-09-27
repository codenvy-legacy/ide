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
package org.exoplatform.ide.editor.php.client.contentassist;

import com.codenvy.ide.client.util.logging.Log;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.api.codeassitant.Modifier;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
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

    private final Token token;

    private final List<Modifier> modifieres = new ArrayList<Modifier>();

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
        this.token = token;

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

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getAdditionalDisplayString()
     */
    @Override
    public String getAdditionalDisplayString() {
        return null;
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getImage() */
    @Override
    public Image getImage() {
        switch (token.getType()) {
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
