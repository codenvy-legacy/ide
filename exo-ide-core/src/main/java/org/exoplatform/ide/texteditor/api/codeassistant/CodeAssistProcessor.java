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
package org.exoplatform.ide.texteditor.api.codeassistant;

import org.exoplatform.ide.texteditor.api.TextEditorPartView;

/**
 * A code assist processor proposes completions for a particular content type.
 * <p>
 * This interface must be implemented by clients. Implementers should be
 * registered with a code assistant in order to get involved in the
 * assisting process.
 * </p>
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface CodeAssistProcessor
{
   /**
    * Returns a list of completion proposals based on the
    * specified location within the document that corresponds
    * to the current cursor position within the text view.
    *
    * @param view the view whose document is used to compute the proposals
    * @param offset an offset within the document for which completions should be computed
    * @return an array of completion proposals or <code>null</code> if no proposals are possible
    */
   CompletionProposal[] computeCompletionProposals(TextEditorPartView view, int offset);
   
   /**
    * Returns the characters which when entered by the user should
    * automatically trigger the presentation of possible completions.
    *
    * @return the auto activation characters for completion proposal or <code>null</code>
    *    if no auto activation is desired
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
