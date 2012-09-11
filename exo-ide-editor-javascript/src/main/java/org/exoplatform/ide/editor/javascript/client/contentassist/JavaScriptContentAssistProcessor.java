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

import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.api.contentassist.ContextInformation;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaScriptContentAssistProcessor implements ContentAssistProcessor
{

   private static char[] activationCharacters = new char[]{'.'};

   private native JavaScriptContenassistProvider getProvider()/*-{
		return $wnd.jsEsprimaContentAssistProvider;
   }-*/;

   private JavaScriptContenassistProvider provider;

   /**
    * 
    */
   public JavaScriptContentAssistProcessor()
   {
      provider = getProvider();
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.ContentAssistProcessor#computeCompletionProposals(org.exoplatform.ide.editor.api.Editor, int)
    */
   @Override
   public CompletionProposal[] computeCompletionProposals(Editor viewer, int offset)
   {
      JsProposal[] jsProposals = provider.computeProposals(viewer.getDocument().get(), offset);
      if (jsProposals == null || jsProposals.length == 0)
         return null;

      JavaScriptProposal[] prop = new JavaScriptProposal[jsProposals.length];
      for (int i = 0; i < jsProposals.length; i++)
      {
         prop[i] = new JavaScriptProposal(jsProposals[i], offset);
      }
      return prop;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.ContentAssistProcessor#computeContextInformation(org.exoplatform.ide.editor.api.Editor, int)
    */
   @Override
   public ContextInformation[] computeContextInformation(Editor viewer, int offset)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.ContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
    */
   @Override
   public char[] getCompletionProposalAutoActivationCharacters()
   {
      return activationCharacters;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.ContentAssistProcessor#getContextInformationAutoActivationCharacters()
    */
   @Override
   public char[] getContextInformationAutoActivationCharacters()
   {
      throw new UnsupportedOperationException();
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
