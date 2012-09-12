// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.exoplatform.ide.editor.javascript.client.contentassist;

import static com.google.collide.client.code.autocomplete.AutocompleteResult.PopupAction.CLOSE;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.api.contentassist.ContextInformation;
import org.exoplatform.ide.editor.api.contentassist.Point;
import org.exoplatform.ide.editor.javascript.client.JavaScriptEditorExtension;
import org.exoplatform.ide.editor.text.IDocument;

import com.google.collide.client.code.autocomplete.AutocompleteProposal;
import com.google.collide.client.code.autocomplete.AutocompleteResult;
import com.google.collide.client.code.autocomplete.DefaultAutocompleteResult;
import com.google.collide.shared.util.StringUtils;

/**
 * Proposal that contains template and knows how to process it.
 *
 * <p>Template is a string which may contain the following wildcard symbols:<ul>
 *   <li>%n - new line character with indentation to the level of
 *            the inserting place;
 *   <li>%i - additional indentation;
 *   <li>%c - a point to place the cursor to after inserting.
 * </ul>
 */
public class TemplateProposal implements CompletionProposal
{

   private final String template;
   
   private final String name;

   public TemplateProposal(String name, String template)
   {
      this.name = name;
      this.template = template;
   }

//   /**
//    * Translates template to {@link AutocompleteResult}.
//    */
//   private AutocompleteResult buildResult(String triggeringString, int indent)
//   {
//
//   }

   /**
    * Adds an indentation to a given string.
    *
    * <p>We suppose extra indention to be double-space.
    */
   private static String indent(String s)
   {
      return s + "  ";
   }

   /**
    * Removes extra indention.
    *
    * <p>We suppose extra indention to be double-space.
    */
   private static String dedent(String s)
   {
      if (s.endsWith("  "))
      {
         return s.substring(0, s.length() - 2);
      }
      return s;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.CompletionProposal#apply(org.exoplatform.ide.editor.text.IDocument)
    */
   @Override
   public void apply(IDocument document)
   {
      String lineStart = "\n" + StringUtils.getSpaces(0);
      String replaced =
         template.replace("%n", lineStart).replace("%i", indent(lineStart)).replace("%d", dedent(lineStart));
      int pos = replaced.indexOf("%c");
      pos = (pos == -1) ? replaced.length() : pos;
      String completion = replaced.replace("%c", "");
      
//      new DefaultAutocompleteResult(completion, pos, 0, 0, 0, CLOSE, triggeringString);
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.CompletionProposal#getSelection(org.exoplatform.ide.editor.text.IDocument)
    */
   @Override
   public Point getSelection(IDocument document)
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.CompletionProposal#getAdditionalProposalInfo()
    */
   @Override
   public Widget getAdditionalProposalInfo()
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.CompletionProposal#getDisplayString()
    */
   @Override
   public String getDisplayString()
   {
      return name;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.CompletionProposal#getImage()
    */
   @Override
   public Image getImage()
   {
      return new Image(JavaScriptEditorExtension.RESOURCES.blankImage());
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.CompletionProposal#getContextInformation()
    */
   @Override
   public ContextInformation getContextInformation()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.CompletionProposal#apply(org.exoplatform.ide.editor.text.IDocument, char, int)
    */
   @Override
   public void apply(IDocument document, char trigger, int offset)
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.CompletionProposal#isValidFor(org.exoplatform.ide.editor.text.IDocument, int)
    */
   @Override
   public boolean isValidFor(IDocument document, int offset)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.CompletionProposal#getTriggerCharacters()
    */
   @Override
   public char[] getTriggerCharacters()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.CompletionProposal#isAutoInsertable()
    */
   @Override
   public boolean isAutoInsertable()
   {
      // TODO Auto-generated method stub
      return false;
   }
}
