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
package org.exoplatform.ide.editor.html.client.contentassist;

import com.codenvy.ide.client.util.logging.Log;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContextInformation;
import org.exoplatform.ide.editor.client.api.contentassist.Point;
import org.exoplatform.ide.editor.html.client.HtmlEditorExtension;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.edits.MalformedTreeException;
import org.exoplatform.ide.editor.shared.text.edits.ReplaceEdit;

/**
 * Completion proposal for HTML.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlProposal.java Feb 7, 2013 11:51:45 AM azatsarynnyy $
 */
public class HtmlProposal implements CompletionProposal {

    private static final String   ELEMENT_SEPARATOR_CLOSE          = ">";

    private static final String   ELEMENT_SELF_CLOSE               = " />";

    private static final String   ELEMENT_SEPARATOR_OPEN_FINISHTAG = "</";

    private static final String   ATTRIBUTE_SEPARATOR_OPEN         = "=\"";

    private static final String   ATTRIBUTE_SEPARATOR_CLOSE        = "\"";

    /** Proposal text label. */
    private String                proposal;

    /** HTML type of autocompletion. */
    private CompletionType        type;

    /** Triggering string. */
    private String                prefix;

    /** Text offset. */
    private final int             offset;

    /** Number of chars, relative to beginning of replacement to move cursor right. */
    private int                   jumpLength;

    /** Holds map of HTML tags with corresponding attributes. */
    private HtmlTagsAndAttributes htmlAttributes;

    /**
     * Is this a proposal to close closeable tag.
     */
    private final boolean         isClosingTagProposal;

    /**
     * Constructs new {@link HtmlProposal} instance with the given proposal, prefix and offset.
     * 
     * @param proposal proposal text label
     * @param type type of autocompletion
     * @param prefix
     * @param offset
     * @param htmlAttributes
     * @param isCloseTagProposal is this a proposal to close closeable tag
     */
    public HtmlProposal(String proposal, CompletionType type, String prefix, int offset,
                        HtmlTagsAndAttributes htmlAttributes, boolean isCloseTagProposal) {
        this.proposal = proposal;
        this.type = type;
        this.prefix = prefix;
        this.offset = offset;
        this.htmlAttributes = htmlAttributes;
        this.isClosingTagProposal = isCloseTagProposal;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#apply(org.exoplatform.ide.editor.shared.text .IDocument)
     */
    @Override
    public void apply(IDocument document) {
        ReplaceEdit replaceEdit = new ReplaceEdit(offset - prefix.length(), prefix.length(), computeProposalLabel());
        try {
            replaceEdit.apply(document);
        } catch (MalformedTreeException e) {
            Log.error(getClass(), e);
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
    }

    /**
     * Compute string to insert depending on what type of HTML autocompletion.
     * 
     * @return result proposal label to insert
     */
    private String computeProposalLabel() {
        switch (type) {
            case ELEMENT:
                if (htmlAttributes.isSelfClosedTag(proposal)) {
                    jumpLength = proposal.length();
                    return proposal + ELEMENT_SELF_CLOSE;
                }

                String label;
                if (isClosingTagProposal) {
                    jumpLength = ELEMENT_SEPARATOR_CLOSE.length() + ELEMENT_SEPARATOR_OPEN_FINISHTAG.length() + proposal.length()
                                 + ELEMENT_SEPARATOR_CLOSE.length();
                    label = ELEMENT_SEPARATOR_CLOSE + ELEMENT_SEPARATOR_OPEN_FINISHTAG + proposal + ELEMENT_SEPARATOR_CLOSE;
                } else {
                    jumpLength = proposal.length() + ELEMENT_SEPARATOR_CLOSE.length();
                    label = proposal + ELEMENT_SEPARATOR_CLOSE + ELEMENT_SEPARATOR_OPEN_FINISHTAG + proposal + ELEMENT_SEPARATOR_CLOSE;
                }
                return label;
            case ATTRIBUTE:
                jumpLength = proposal.length() + ATTRIBUTE_SEPARATOR_OPEN.length();
                return proposal + ATTRIBUTE_SEPARATOR_OPEN + ATTRIBUTE_SEPARATOR_CLOSE;
            default:
                Log.warn(getClass(), "Invocation of this method in not allowed for type " + type);
                return null;
        }
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getSelection(org.exoplatform.ide.editor.shared.text
     *      .IDocument)
     */
    @Override
    public Point getSelection(IDocument document) {
        return new Point(offset + jumpLength - prefix.length(), 0);
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getAdditionalProposalInfo() */
    @Override
    public Widget getAdditionalProposalInfo() {
        return null;
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getDisplayString() */
    @Override
    public String getDisplayString() {
        if (isClosingTagProposal) {
            String displayString = ELEMENT_SEPARATOR_OPEN_FINISHTAG + proposal + ELEMENT_SEPARATOR_CLOSE + " (close tag)";
            return SafeHtmlUtils.fromString(displayString).asString();
        }
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
        Image image = new Image();
        switch (type) {
            case ELEMENT:
                image.setResource(HtmlEditorExtension.RESOURCES.tag());
                break;
            case ATTRIBUTE:
                image.setResource(HtmlEditorExtension.RESOURCES.attribute());
                break;
            default:
                break;
        }
        return image;
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getContextInformation() */
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
     * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#isValidFor(org.exoplatform.ide.editor.shared.text
     *      .IDocument, int)
     */
    @Override
    public boolean isValidFor(IDocument document, int offset) {
        return false;
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getTriggerCharacters() */
    @Override
    public char[] getTriggerCharacters() {
        return null;
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#isAutoInsertable() */
    @Override
    public boolean isAutoInsertable() {
        return true;
    }

}
