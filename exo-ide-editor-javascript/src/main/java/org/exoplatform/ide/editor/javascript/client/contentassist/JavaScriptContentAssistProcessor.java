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

import org.exoplatform.ide.json.shared.JsonCollections;

import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.client.api.contentassist.ContextInformation;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IRegion;
import org.exoplatform.ide.json.client.JsoArray;
import org.exoplatform.ide.json.shared.JsonArray;

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

   private boolean isTextToCompleteBeforeDot;

   /**
    * 
    */
   public JavaScriptContentAssistProcessor()
   {
      provider = getProvider();
   }

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#computeCompletionProposals(org.exoplatform.ide.editor.client.api.Editor, int)
    */
   @Override
   public CompletionProposal[] computeCompletionProposals(Editor viewer, int offset)
   {
      Context c = Context.create();
      String prefix = computatePrefix(viewer.getDocument(), offset);
      c.setPrefix(prefix);
      JsonArray<CompletionProposal> prop = JsonCollections.createArray();

      try
      {
         JsoArray<JsProposal> jsProposals = provider.computeProposals(viewer.getDocument().get(), offset, c);
         if (jsProposals != null && jsProposals.size() != 0)
         {
            for (int i = 0; i < jsProposals.size(); i++)
            {
               prop.add(new JavaScriptProposal(jsProposals.get(i), offset));
            }
         }
      }
      catch (Exception ignore)
      {
      }
      if (!isTextToCompleteBeforeDot)
      {
         JsonArray<? extends TemplateProposal> search = JsConstants.getInstance().getTemplatesTrie().search(prefix);
         for (TemplateProposal p : search.asIterable())
         {
            p.setOffset(offset);
            p.setPrefix(prefix);
            prop.add(p);
         }
      }
      CompletionProposal[] proposals = new CompletionProposal[prop.size()];
      for (int i = 0; i < prop.size(); i++)
      {
         proposals[i] = prop.get(i);
      }
      return proposals;
   }

   /**
    * @param document
    * @param offset
    * @return
    */
   private String computatePrefix(IDocument document, int offset)
   {
      isTextToCompleteBeforeDot = false;
      try
      {
         IRegion lineInfo = document.getLineInformationOfOffset(offset);
         String line = document.get(lineInfo.getOffset(), lineInfo.getLength());
         String partLine = line.substring(0, offset - lineInfo.getOffset());
         for (int i = partLine.length() - 1; i >= 0; i--)
         {
            switch (partLine.charAt(i))
            {
               case '.' :
                  isTextToCompleteBeforeDot = true;
               case ' ' :
               case '(' :
               case ')' :
               case '{' :
               case '}' :
               case ';' :
               case '[' :
               case ']' :
               case '"' :
               case '\'' :
                  return partLine.substring(i + 1);
               default :
                  break;
            }
         }
         return partLine;

      }
      catch (BadLocationException e)
      {
         e.printStackTrace();
      }
      return "";
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
      return activationCharacters;
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
      // TODO Auto-generated method stub
      return null;
   }

}
