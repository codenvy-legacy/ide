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
package org.exoplatform.ide.client.framework.editor;

import org.exoplatform.ide.editor.api.SelectionRange;
import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.FindReplaceDocumentAdapter;
import org.exoplatform.ide.editor.text.IDocument;
import org.exoplatform.ide.editor.text.IRegion;
import org.exoplatform.ide.editor.text.edits.DeleteEdit;
import org.exoplatform.ide.editor.text.edits.InsertEdit;
import org.exoplatform.ide.editor.text.edits.MultiTextEdit;
import org.exoplatform.ide.editor.text.edits.TextEdit;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 10, 2012 5:29:30 PM anya $
 * 
 */
public abstract class AbstractCommentsModifier implements CommentsModifier
{

   /**
    * @see org.exoplatform.ide.client.framework.editor.CommentsModifier#addBlockComment(org.exoplatform.ide.client.framework.editor.SelectionRange,
    *      org.exoplatform.ide.client.framework.editor.IDocument)
    */
   @Override
   public TextEdit addBlockComment(SelectionRange selectionRange, IDocument document)
   {
      MultiTextEdit textEdit = new MultiTextEdit();
      int startOffset = 0;
      int endOffset = 0;
      try
      {
         startOffset = document.getLineOffset(selectionRange.getStartLine() - 1) + selectionRange.getStartSymbol();
         endOffset = document.getLineOffset(selectionRange.getEndLine() - 1) + selectionRange.getEndSymbol();
      }
      catch (BadLocationException e)
      {
      }

      FindReplaceDocumentAdapter findReplaceDocument = new FindReplaceDocumentAdapter(document);
      try
      {
         IRegion region = findReplaceDocument.find(startOffset, getOpenBlockComment(), true, false, false, false);
         if (region != null && region.getOffset() < endOffset)
         {
            textEdit.addChild(new DeleteEdit(region.getOffset(), region.getLength()));
         }

         region = findReplaceDocument.find(startOffset, getCloseBlockComment(), true, false, false, false);
         if (region != null && region.getOffset() < endOffset)
         {
            textEdit.addChild(new DeleteEdit(region.getOffset(), region.getLength()));
         }
      }
      catch (BadLocationException e)
      {
      }

      textEdit.addChild(new InsertEdit(startOffset, getOpenBlockComment()));
      textEdit.addChild(new InsertEdit(endOffset, getCloseBlockComment()));

      return textEdit;
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.CommentsModifier#removeBlockComment(org.exoplatform.ide.client.framework.editor.SelectionRange,
    *      org.exoplatform.ide.client.framework.editor.IDocument)
    */
   @Override
   public TextEdit removeBlockComment(SelectionRange selectionRange, IDocument document)
   {
      MultiTextEdit textEdit = new MultiTextEdit();
      int startOffset = 0;
      int endOffset = 0;
      try
      {
         startOffset = document.getLineOffset(selectionRange.getStartLine() - 1) + selectionRange.getStartSymbol();
         endOffset = document.getLineOffset(selectionRange.getEndLine() - 1) + selectionRange.getEndSymbol();
      }
      catch (BadLocationException e)
      {
      }

      FindReplaceDocumentAdapter findReplaceDocument = new FindReplaceDocumentAdapter(document);
      try
      {
         IRegion region = findReplaceDocument.find(startOffset, getOpenBlockComment(), true, false, false, false);
         if (region != null && region.getOffset() < endOffset)
         {
            textEdit.addChild(new DeleteEdit(region.getOffset(), region.getLength()));
         }

         region = findReplaceDocument.find(startOffset, getCloseBlockComment(), true, false, false, false);
         if (region != null)
         {
            textEdit.addChild(new DeleteEdit(region.getOffset(), region.getLength()));
         }
      }
      catch (BadLocationException e)
      {
      }
      return textEdit;
   }

   /**
    * Returns mark of the opening block comment.
    * 
    * @return {@link String} mark of the opening block comment
    */
   public abstract String getOpenBlockComment();

   /**
    * Returns mark of the closing block comment.
    * 
    * @return {@link String} mark of the closing block comment
    */
   public abstract String getCloseBlockComment();
}
