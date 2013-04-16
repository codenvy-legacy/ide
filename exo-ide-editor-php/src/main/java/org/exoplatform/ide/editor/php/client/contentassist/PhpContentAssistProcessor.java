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

import com.google.collide.client.CollabEditor;
import com.google.collide.client.documentparser.DocumentParser;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.collide.codemirror2.CodeMirror2;
import com.google.collide.codemirror2.Token;
import com.google.collide.codemirror2.TokenUtil;
import com.google.collide.shared.Pair;
import com.google.collide.shared.document.Line;
import com.google.collide.shared.document.Position;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.client.api.contentassist.ContextInformation;
import org.exoplatform.ide.json.shared.JsonArray;

/**
 * A {@link ContentAssistProcessor} proposes completions and
 * computes context information for PHP content.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: PhpContentAssistProcessor.java Apr 15, 2013 3:47:18 PM azatsarynnyy $
 *
 */
public class PhpContentAssistProcessor implements ContentAssistProcessor {

    /** A {@link ContentAssistProcessor} for HTML. */
    private final ContentAssistProcessor htmlContentAssistProcessor;

    /**
     * Constructs new {@link PhpContentAssistProcessor} instance.
     *
     * @param htmlContentAssistProcessor
     *         {@link ContentAssistProcessor}
     */
    public PhpContentAssistProcessor(ContentAssistProcessor htmlContentAssistProcessor) {
        this.htmlContentAssistProcessor = htmlContentAssistProcessor;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#computeCompletionProposals(org.exoplatform.ide.editor.client.api.Editor, int)
     */
    @Override
    public CompletionProposal[] computeCompletionProposals(Editor editor, int offset) {
        CollabEditor collabEditor = (CollabEditor)editor;
        SelectionModel selection = collabEditor.getEditor().getSelection();
        DocumentParser parser = collabEditor.getEditorBundle().getParser();

        Position cursor = selection.getCursorPosition();
        final Line line = cursor.getLine();
        final int column = cursor.getColumn();

        JsonArray<Token> tokens = parser.parseLineSync(line);
        if (tokens == null) {
            // This line has never been parsed yet. No variants.
            return null;
        }

        // We do not ruin parse results for "clean" lines.
//        if (parser.isLineDirty(cursor.getLineNumber())) {
//            // But "processing" of "dirty" line is harmless.
//            XmlCodeAnalyzer.processLine(TaggableLineUtil.getPreviousLine(line), line, tokens);
//        }
        String initialMode = parser.getInitialMode(line);
        JsonArray<Pair<Integer, String>> modes = TokenUtil.buildModes(initialMode, tokens);
//        putModeAnchors(line, modes);
        String mode = TokenUtil.findModeForColumn(initialMode, modes, column);

        if (htmlContentAssistProcessor != null) {
            if (CodeMirror2.CSS.equals(mode) || CodeMirror2.JAVASCRIPT.equals(mode) || CodeMirror2.HTML.equals(mode)) {
                return htmlContentAssistProcessor.computeCompletionProposals(editor, offset);
            }
        }

        if (CodeMirror2.PHP.equals(mode)) {
            // TODO keywords autocompletion support
        }

        return null;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#computeContextInformation(org.exoplatform.ide.editor.client.api.Editor, int)
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
