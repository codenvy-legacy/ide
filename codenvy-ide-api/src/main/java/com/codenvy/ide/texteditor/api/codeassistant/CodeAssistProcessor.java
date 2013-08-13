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
package com.codenvy.ide.texteditor.api.codeassistant;

import com.codenvy.ide.texteditor.api.TextEditorPartView;

/**
 * A code assist processor proposes completions for a particular content type.
 * <p>
 * This interface must be implemented by clients. Implementers should be
 * registered with a code assistant in order to get involved in the
 * assisting process.
 * </p>
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface CodeAssistProcessor {
    /**
     * Returns a list of completion proposals based on the
     * specified location within the document that corresponds
     * to the current cursor position within the text view.
     *
     * @param view
     *         the view whose document is used to compute the proposals
     * @param offset
     *         an offset within the document for which completions should be computed
     * @return an array of completion proposals or <code>null</code> if no proposals are possible
     */
    CompletionProposal[] computeCompletionProposals(TextEditorPartView view, int offset);

    /**
     * Returns the characters which when entered by the user should
     * automatically trigger the presentation of possible completions.
     *
     * @return the auto activation characters for completion proposal or <code>null</code>
     *         if no auto activation is desired
     */
    char[] getCompletionProposalAutoActivationCharacters();

    /**
     * Returns the reason why this content assist processor
     * was unable to produce any completion proposals or context information.
     *
     * @return an error message or <code>null</code> if no error occurred
     */
    String getErrorMessage();
}
