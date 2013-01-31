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
package org.exoplatform.ide.editor.shared.text.edits;

import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.BadLocationException;

/**
 * Text edit to delete a range in a document.
 * <p>
 * A delete edit is equivalent to <code>ReplaceEdit(
 * offset, length, "")</code>.
 * 
 * @since 3.0
 */
public final class DeleteEdit extends TextEdit
{

   /**
    * Constructs a new delete edit.
    * 
    * @param offset the offset of the range to replace
    * @param length the length of the range to replace
    */
   public DeleteEdit(int offset, int length)
   {
      super(offset, length);
   }

   /* Copy constructor */
   private DeleteEdit(DeleteEdit other)
   {
      super(other);
   }

   /* @see TextEdit#doCopy */
   protected TextEdit doCopy()
   {
      return new DeleteEdit(this);
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
      document.replace(getOffset(), getLength(), ""); //$NON-NLS-1$
      fDelta = -getLength();
      return fDelta;
   }

   /* @see TextEdit#deleteChildren */
   boolean deleteChildren()
   {
      return true;
   }
}
