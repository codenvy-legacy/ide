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
package org.exoplatform.ide.editor.css.client.contentassist;

import com.google.collide.client.CollabEditor;
import com.google.collide.client.code.autocomplete.AutocompleteProposals;
import com.google.collide.client.code.autocomplete.AutocompleteProposals.ProposalWithContext;
import com.google.collide.client.editor.selection.SelectionModel;

import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.client.api.contentassist.ContextInformation;

/**
 * A content assist processor proposes completions and
 * computes context information for CSS content.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: CssContentAssistProcessor.java Feb 4, 2013 5:52:44 PM azatsarynnyy $
 *
 */
public class CssContentAssistProcessor implements ContentAssistProcessor
{

   /**
    * The auto activation characters for completion proposal.
    */
   private static final char[] ACTIVATION_CHARACTERS = new char[]{':'};

   /**
    * Autocompleter for CSS.
    */
   private CssAutocompleter autocompleter;

   /**
    * Creates new {@link CssContentAssistProcessor} instance.
    * 
    * @param autocompleter {@link CssAutocompleter}
    */
   public CssContentAssistProcessor(CssAutocompleter cssAutocompleter)
   {
      autocompleter = cssAutocompleter;
   }

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#computeCompletionProposals(org.exoplatform.ide.editor.client.api.Editor, int)
    */
   @Override
   public CompletionProposal[] computeCompletionProposals(Editor viewer, int offset)
   {
      SelectionModel selection = ((CollabEditor)viewer).getEditor().getSelection();
      AutocompleteProposals autocompletionProposals = autocompleter.findAutocompletions(selection, null);

      CssCompletionQuery completionQuery = autocompleter.getCompletionQuery();
      if (completionQuery == null)
      {
         return null;
      }
      CompletionType completionType = completionQuery.getCompletionType();

      CompletionProposal[] proposalArray = new CompletionProposal[autocompletionProposals.getItems().size()];
      for (int i = 0; i < autocompletionProposals.getItems().size(); i++)
      {
         ProposalWithContext proposal = autocompletionProposals.select(i);
         String triggeringString = proposal.getContext().getTriggeringString();
         proposalArray[i] = new CssProposal(proposal.getItem().getName(), completionType, triggeringString, offset);
      }
      return proposalArray;
   }

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#computeContextInformation(org.exoplatform.ide.editor.client.api.Editor, int)
    */
   @Override
   public ContextInformation[] computeContextInformation(Editor viewer, int offset)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
    */
   @Override
   public char[] getCompletionProposalAutoActivationCharacters()
   {
      return ACTIVATION_CHARACTERS;
   }

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getContextInformationAutoActivationCharacters()
    */
   @Override
   public char[] getContextInformationAutoActivationCharacters()
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getErrorMessage()
    */
   @Override
   public String getErrorMessage()
   {
      return null;
   }

}
