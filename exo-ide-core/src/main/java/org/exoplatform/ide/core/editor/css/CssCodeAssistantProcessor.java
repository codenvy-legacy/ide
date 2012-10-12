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
package org.exoplatform.ide.core.editor.css;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.js.JsoArray;
import org.exoplatform.ide.text.store.Line;
import org.exoplatform.ide.text.store.Position;
import org.exoplatform.ide.texteditor.api.TextEditorPartDisplay;
import org.exoplatform.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal;
import org.exoplatform.ide.util.AbstractTrie;

/**
 * Implementation of {@link CodeAssistProcessor} for Css files.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CssCodeAssistantProcessor implements CodeAssistProcessor
{

   private static final AbstractTrie<CssCompletionProposal> cssTrie = CssTrie.createTrie();

   private CssCompletionQuery completionQuery;

   private CssResources resourcess;

   /**
    * @param resourcess
    */
   public CssCodeAssistantProcessor(CssResources resourcess)
   {
      super();
      this.resourcess = resourcess;
   }

   /**
    * Creates a completion query from the position of the caret and the editor.
    * The completion query contains the string to complete and the type of
    * code assistant.
    *
    */
   CssCompletionQuery updateOrCreateQuery(CssCompletionQuery completionQuery, Position cursor)
   {
      Line line = cursor.getLine();
      int column = cursor.getColumn();
      Line lineWithCursor = line;
      boolean parsingLineWithCursor = true;

      /*
       * textSoFar will contain the text of the CSS rule (only the stuff within
       * the curly braces). If we are not in an open rule, return false
       */
      String textBefore = "";
      while ((line != null) && (!textBefore.contains("{")))
      {
         int lastOpen;
         int lastClose;

         String text;
         if (parsingLineWithCursor)
         {
            text = line.getText().substring(0, column);
            parsingLineWithCursor = false;
         }
         else
         {
            /*
             * Don't include the newline character; it is irrelevant for
             * autocompletion.
             */
            text = line.getText();//.trim();
         }

         textBefore = text + textBefore;
         lastOpen = text.lastIndexOf('{');
         lastClose = text.lastIndexOf('}');

         // Either we have only a } or the } appears after {
         if (lastOpen < lastClose)
         {
            return new CssCompletionQuery(textBefore, "");
         }
         else if ((lastOpen == -1) && (lastClose == -1))
         {
            line = line.getPreviousLine();
         }
         else
         {
            if (textBefore.endsWith("{"))
            {
               // opening a new css class, no text after to consider
               return new CssCompletionQuery(textBefore, "");
            }
            else if (textBefore.endsWith(";") && completionQuery != null)
            {
               // we don't want to create a new query, otherwise we lose the
               // completed proposals
               completionQuery.setCompletionType(CompletionType.NONE);
               return completionQuery;
            }
         } 
      }

      parsingLineWithCursor = true;
      String textAfter = "";
      line = lineWithCursor;
      while ((line != null) && (!textAfter.contains("}")))
      {
         int lastOpen;
         int lastClose;

         String text;
         if (parsingLineWithCursor)
         {
            text = line.getText().substring(column);
            parsingLineWithCursor = false;
         }
         else
         {
            /*
             * Don't include the newline character; it is irrelevant for
             * autocompletion.
             */
            text = line.getText().trim();
         }

         textAfter = textAfter + text;
         lastOpen = text.lastIndexOf('{');
         lastClose = text.lastIndexOf('}');

         // Either we have only a } or the } appears after {
         if (lastClose < lastOpen)
         {
            return completionQuery;
         }
         else if ((lastOpen == -1) && (lastClose == -1))
         {
            line = line.getNextLine();
         }
         else
         {
            if ((!textAfter.isEmpty()) && (textAfter.charAt(textAfter.length() - 1) == ';'))
            {
               return completionQuery;
            }
         }
      }

      if (textBefore.contains("{"))
      {
         textBefore = textBefore.substring(textBefore.indexOf('{') + 1);
      }
      if (textAfter.contains("}"))
      {
         textAfter = textAfter.substring(0, textAfter.indexOf('}'));
      }

      return new CssCompletionQuery(textBefore, textAfter);
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CodeAssistProcessor#computeCompletionProposals(org.exoplatform.ide.texteditor.api.TextEditorPartDisplay, int)
    */
   @Override
   public CompletionProposal[] computeCompletionProposals(TextEditorPartDisplay display, int offset)
   {
      if (display.getSelection().hasSelection())
      {
         // Doesn't make much sense to autocomplete CSS when something is selected.
         return null;
      }

      completionQuery = updateOrCreateQuery(completionQuery, display.getSelection().getCursorPosition());
      if (completionQuery == null)
      {
         return null;
      }

      String triggeringString = completionQuery.getTriggeringString();
      if (triggeringString == null)
      {
         return null;
      }
      InvocationContext context = new InvocationContext(triggeringString, offset, resourcess, display);
      switch (completionQuery.getCompletionType())
      {
         case PROPERTY :
            JsonArray<CssCompletionProposal> autocompletions =
               CssTrie
                  .findAndFilterAutocompletions(cssTrie, triggeringString, completionQuery.getCompletedProperties());
            return jsToArray(autocompletions, context);

         case VALUE :
            JsoArray<CssCompletionProposal> jsoArray =
               CssPartialParser.getInstance().getAutocompletions(completionQuery.getProperty(),
                  completionQuery.getValuesBefore(), triggeringString, completionQuery.getValuesAfter());
            return jsToArray(jsoArray, context);

         case CLASS :
            // TODO: Implement css-class autocompletions (pseudoclasses
            //               and HTML elements).
            return null;

         default :
            return null;
      }
   }

   private CssCompletionProposal[] jsToArray(JsonArray<CssCompletionProposal> autocompletions, InvocationContext context)
   {
      if (autocompletions == null)
         return null;
      CssCompletionProposal[] proposals = new CssCompletionProposal[autocompletions.size()];
      for (int i = 0; i < autocompletions.size(); i++)
      {
         proposals[i] = autocompletions.get(i);
         proposals[i].setContext(context);
      }
      return proposals;
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CodeAssistProcessor#getCompletionProposalAutoActivationCharacters()
    */
   @Override
   public char[] getCompletionProposalAutoActivationCharacters()
   {
      return new char[]{':'};
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CodeAssistProcessor#getErrorMessage()
    */
   @Override
   public String getErrorMessage()
   {
      return null;
   }

}
