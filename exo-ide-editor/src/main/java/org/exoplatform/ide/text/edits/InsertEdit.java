/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.text.edits;

import org.exoplatform.ide.text.BadLocationException;
import org.exoplatform.ide.text.IDocument;

/**
 * Text edit to insert a text at a given position in a document.
 * <p>
 * An insert edit is equivalent to <code>ReplaceEdit(offset, 0, text)
 * </code>
 * 
 * @since 3.0
 */
public final class InsertEdit extends TextEdit
{

   private String fText;

   /**
    * Constructs a new insert edit.
    * 
    * @param offset the insertion offset
    * @param text the text to insert
    */
   public InsertEdit(int offset, String text)
   {
      super(offset, 0);
      fText = text;
   }

   /* Copy constructor */
   private InsertEdit(InsertEdit other)
   {
      super(other);
      fText = other.fText;
   }

   /**
    * Returns the text to be inserted.
    * 
    * @return the edit's text.
    */
   public String getText()
   {
      return fText;
   }

   /* @see TextEdit#doCopy */
   protected TextEdit doCopy()
   {
      return new InsertEdit(this);
   }

   /* @see TextEdit#accept0 */
   protected void accept0(TextEditVisitor visitor)
   {
      boolean visitChildren = visitor.visit(this);
      if (visitChildren)
      {
         acceptChildren(visitor);
      }
   }

   /* @see TextEdit#performDocumentUpdating */
   int performDocumentUpdating(IDocument document) throws BadLocationException
   {
      document.replace(getOffset(), getLength(), fText);
      fDelta = fText.length() - getLength();
      return fDelta;
   }

   /* @see TextEdit#deleteChildren */
   boolean deleteChildren()
   {
      return false;
   }

   /*
    * @see org.eclipse.text.edits.TextEdit#internalToString(java.lang.StringBuffer, int)
    * @since 3.3
    */
   void internalToString(StringBuffer buffer, int indent)
   {
      super.internalToString(buffer, indent);
      buffer.append(" <<").append(fText); //$NON-NLS-1$
   }
}
