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
package org.exoplatform.ide.extension.css.editor;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.text.BadLocationException;
import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.text.Region;
import org.exoplatform.ide.text.RegionImpl;
import org.exoplatform.ide.text.edits.MalformedTreeException;
import org.exoplatform.ide.text.edits.ReplaceEdit;
import org.exoplatform.ide.texteditor.api.TextEditorOperations;
import org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal;
import org.exoplatform.ide.util.loging.Log;

/**
 * {@link CompletionProposal} implementation for Css code assistant.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CssCompletionProposal implements CompletionProposal
{

   private static final String PROPERTY_TERMINATOR = ";";

   private static final String PROPERTY_SEPARATOR = ": ";

   private final String name;

   private final CompletionType type;

   private InvocationContext context;

   private int jumpLength;

   private int selectLength;

   /**
    * @param name
    */
   public CssCompletionProposal(String name, CompletionType type)
   {
      this.name = name;
      this.type = type;
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal#apply(org.exoplatform.ide.text.Document)
    */
   @Override
   public void apply(Document document)
   {
      String insert = computateInsertString();
      ReplaceEdit e =
         new ReplaceEdit(context.getOffset() - context.getPrefix().length(), context.getPrefix().length(), insert);
      try
      {
         e.apply(document);
         if(type == CompletionType.PROPERTY)
            context.getEditor().doOperation(TextEditorOperations.CODEASSIST_PROPOSALS);
      }
      catch (MalformedTreeException e1)
      {
         Log.error(getClass(), e1);
      }
      catch (BadLocationException e1)
      {
         Log.error(getClass(), e1);
      }
   }

   /**
    * @return
    */
   private String computateInsertString()
   {
      selectLength = 0;
      if (type == CompletionType.CLASS)
      {
         // In this case implicit autocompletion workflow should trigger,
         // and so execution should never reach this point.
         Log.warn(getClass(), "Invocation of this method in not allowed for type CLASS");
      }
      else if (CompletionType.PROPERTY == type)
      {
         String addend = name + PROPERTY_SEPARATOR + PROPERTY_TERMINATOR;
         jumpLength = addend.length() - PROPERTY_TERMINATOR.length();
         return addend;
      }
      else if (CompletionType.VALUE == type)
      {
         int start = name.indexOf('<');
         int end = name.indexOf('>');
         if ((start >= 0) && (start < end))
         {
            jumpLength = end + 1;
            selectLength = -1 * ((end + 1) - start);
         }
         else
         {
            jumpLength = name.length();
         }
         return name;
      }
      return null;
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal#getSelection(org.exoplatform.ide.text.Document)
    */
   @Override
   public Region getSelection(Document document)
   {
      return new RegionImpl(context.getOffset() + jumpLength - context.getPrefix().length(), selectLength);
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal#getAdditionalProposalInfo()
    */
   @Override
   public Widget getAdditionalProposalInfo()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal#getDisplayString()
    */
   @Override
   public String getDisplayString()
   {
      return new SafeHtmlBuilder().appendEscaped(name).toSafeHtml().asString();
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal#getImage()
    */
   @Override
   public Image getImage()
   {
      Image image = new Image();
      if(type == CompletionType.PROPERTY)
         image.setResource(context.getResources().property());
      return image;
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal#apply(org.exoplatform.ide.text.Document, char, int)
    */
   @Override
   public void apply(Document document, char trigger, int offset)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal#isValidFor(org.exoplatform.ide.text.Document, int)
    */
   @Override
   public boolean isValidFor(Document document, int offset)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal#getTriggerCharacters()
    */
   @Override
   public char[] getTriggerCharacters()
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal#isAutoInsertable()
    */
   @Override
   public boolean isAutoInsertable()
   {
      return true;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param context the context to set
    */
   public void setContext(InvocationContext context)
   {
      this.context = context;
   }
}
