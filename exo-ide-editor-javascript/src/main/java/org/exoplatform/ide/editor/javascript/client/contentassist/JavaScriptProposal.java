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
package org.exoplatform.ide.editor.javascript.client.contentassist;

import com.codenvy.ide.client.util.logging.Log;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContextInformation;
import org.exoplatform.ide.editor.client.api.contentassist.Point;
import org.exoplatform.ide.editor.javascript.client.JavaScriptEditorExtension;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.edits.InsertEdit;
import org.exoplatform.ide.editor.shared.text.edits.MalformedTreeException;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaScriptProposal implements CompletionProposal {

    private JsProposal prop;
    private final int offset;

    /** @param prop */
    public JavaScriptProposal(JsProposal prop, int offset) {
        super();
        this.prop = prop;
        this.offset = offset;
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#apply(org.exoplatform.ide.editor.shared.text
     * .IDocument) */
    @Override
    public void apply(IDocument document) {
        InsertEdit insertEdit = new InsertEdit(offset, prop.getProposal());
        try {
            insertEdit.apply(document);
        } catch (MalformedTreeException e) {
            Log.error(getClass(), e);
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getSelection(org.exoplatform.ide.editor.shared.text
     * .IDocument) */
    @Override
    public Point getSelection(IDocument document) {
        int escapePosition = prop.getEscapePosition();
        if (escapePosition == -1) {
            escapePosition = offset + prop.getProposal().length();
        }
        return new Point(escapePosition, 0);
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getAdditionalProposalInfo() */
    @Override
    public Widget getAdditionalProposalInfo() {
        return null;
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getDisplayString() */
    @Override
    public String getDisplayString() {
        return prop.getDescription();
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
        return new Image(JavaScriptEditorExtension.RESOURCES.javaScript());
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
     * .IDocument,
     *      int)
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
