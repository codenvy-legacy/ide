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
package com.google.collide.client.code.autocomplete;

import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.api.contentassist.ContextInformation;
import org.exoplatform.ide.editor.text.IDocument;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ContentAssistProcessorImpl.java Sep 10, 2012 3:55:23 PM azatsarynnyy $
 *
 */
public class ContentAssistProcessorImpl implements ContentAssistProcessor
{

   private CompletionProposal[] completionProposals;

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.ContentAssistProcessor#computeCompletionProposals(org.exoplatform.ide.editor.api.Editor, int)
    */
   @Override
   public CompletionProposal[] computeCompletionProposals(Editor viewer, int offset)
   {
      IDocument document = viewer.getDocument();

      return new CompletionProposal[0];
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.ContentAssistProcessor#computeContextInformation(org.exoplatform.ide.editor.api.Editor, int)
    */
   @Override
   public ContextInformation[] computeContextInformation(Editor viewer, int offset)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.ContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
    */
   @Override
   public char[] getCompletionProposalAutoActivationCharacters()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.ContentAssistProcessor#getContextInformationAutoActivationCharacters()
    */
   @Override
   public char[] getContextInformationAutoActivationCharacters()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.ContentAssistProcessor#getErrorMessage()
    */
   @Override
   public String getErrorMessage()
   {
      // TODO Auto-generated method stub
      return null;
   }

}
