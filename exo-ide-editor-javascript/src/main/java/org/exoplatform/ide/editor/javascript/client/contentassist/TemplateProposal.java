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

import com.google.collide.client.util.logging.Log;

import com.google.collide.shared.util.StringUtils;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.api.contentassist.ContextInformation;
import org.exoplatform.ide.editor.api.contentassist.Point;
import org.exoplatform.ide.editor.javascript.client.JavaScriptEditorExtension;
import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.IDocument;
import org.exoplatform.ide.editor.text.edits.InsertEdit;
import org.exoplatform.ide.editor.text.edits.MalformedTreeException;

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

   private final String name;

   private int pos;

   private String completion;
   
   private int offset;

   private String prefix;

   public TemplateProposal(String name, String template)
   {
      this.name = name;
      String lineStart = "\n" + StringUtils.getSpaces(0);
      String replaced =
         template.replace("%n", lineStart).replace("%i", indent(lineStart)).replace("%d", dedent(lineStart));
      pos = replaced.indexOf("%c");
      pos = (pos == -1) ? replaced.length() : pos;
      completion = replaced.replace("%c", "");
   }

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
      InsertEdit edit = new InsertEdit(offset, completion.substring(prefix.length()));
      try
      {
         edit.apply(document);
      }
      catch (MalformedTreeException e)
      {
         Log.error(getClass(), e);
      }
      catch (BadLocationException e)
      {
         Log.error(getClass(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.CompletionProposal#getSelection(org.exoplatform.ide.editor.text.IDocument)
    */
   @Override
   public Point getSelection(IDocument document)
   {
      return new Point(offset + pos, 0);
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
   
   /**
    * @param offset the offset to set
    */
   public void setOffset(int offset)
   {
      this.offset = offset;
   }
   
   public void setPrefix(String prefix)
   {
      this.prefix = prefix;
      
   }
}
