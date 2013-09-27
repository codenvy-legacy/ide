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
package org.exoplatform.ide.editor.javascript.client.contentassist;

import com.codenvy.ide.json.client.JsoArray;
import com.codenvy.ide.json.shared.JsonArray;
import com.codenvy.ide.json.shared.JsonCollections;

import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.client.api.contentassist.ContextInformation;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IRegion;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaScriptContentAssistProcessor implements ContentAssistProcessor {

    private static char[] activationCharacters = new char[]{'.'};

    private native JavaScriptContenassistProvider getProvider()/*-{
        return $wnd.jsEsprimaContentAssistProvider;
    }-*/;

    private JavaScriptContenassistProvider provider;

    private boolean isTextToCompleteBeforeDot;

    /**
     *
     */
    public JavaScriptContentAssistProcessor() {
        provider = getProvider();
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#computeCompletionProposals(org.exoplatform.ide
     * .editor.client.api.Editor,
     *      int)
     */
    @Override
    public CompletionProposal[] computeCompletionProposals(Editor viewer, int offset) {
        Context c = Context.create();
        String prefix = computePrefix(viewer.getDocument(), offset);
        c.setPrefix(prefix);
        JsonArray<CompletionProposal> prop = JsonCollections.createArray();

        try {
            JsoArray<JsProposal> jsProposals = provider.computeProposals(viewer.getDocument().get(), offset, c);
            if (jsProposals != null && jsProposals.size() != 0) {
                for (int i = 0; i < jsProposals.size(); i++) {
                    prop.add(new JavaScriptProposal(jsProposals.get(i), offset));
                }
            }
        } catch (Exception ignore) {
        }
        if (!isTextToCompleteBeforeDot) {
            JsonArray<? extends TemplateProposal> search = JsConstants.getInstance().getTemplatesTrie().search(prefix);
            for (TemplateProposal p : search.asIterable()) {
                p.setOffset(offset);
                p.setPrefix(prefix);
                prop.add(p);
            }
        }
        CompletionProposal[] proposals = new CompletionProposal[prop.size()];
        for (int i = 0; i < prop.size(); i++) {
            proposals[i] = prop.get(i);
        }
        return proposals;
    }

    /**
     * @param document
     * @param offset
     * @return
     */
    private String computePrefix(IDocument document, int offset) {
        isTextToCompleteBeforeDot = false;
        try {
            IRegion lineInfo = document.getLineInformationOfOffset(offset);
            String line = document.get(lineInfo.getOffset(), lineInfo.getLength());
            String partLine = line.substring(0, offset - lineInfo.getOffset());
            for (int i = partLine.length() - 1; i >= 0; i--) {
                switch (partLine.charAt(i)) {
                    case '.':
                        isTextToCompleteBeforeDot = true;
                    case ' ':
                    case '(':
                    case ')':
                    case '{':
                    case '}':
                    case ';':
                    case '[':
                    case ']':
                    case '"':
                    case '\'':
                        return partLine.substring(i + 1);
                    default:
                        break;
                }
            }
            return partLine;

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#computeContextInformation(org.exoplatform.ide
     * .editor.client.api.Editor,
     *      int)
     */
    @Override
    public ContextInformation[] computeContextInformation(Editor viewer, int offset) {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getCompletionProposalAutoActivationCharacters() */
    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return activationCharacters;
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getContextInformationAutoActivationCharacters() */
    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getErrorMessage() */
    @Override
    public String getErrorMessage() {
        return null;
    }

}
