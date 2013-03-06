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
package com.codenvy.ide.text.edits;

import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates the reverse changes of an executed text edit tree. To apply an undo memento to a document use method
 * <code>apply(IDocument)</code>.
 * <p>
 * Clients can't add additional children to an undo edit nor can they add an undo edit as a child to another edit. Doing so
 * results in both cases in a <code>MalformedTreeException<code>.
 * 
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public final class UndoEdit extends TextEdit
{

   UndoEdit()
   {
      super(0, Integer.MAX_VALUE);
   }

   private UndoEdit(UndoEdit other)
   {
      super(other);
   }

   /*
    * @see org.eclipse.text.edits.TextEdit#internalAdd(org.eclipse.text.edits.TextEdit )
    */
   void internalAdd(TextEdit child) throws MalformedTreeException
   {
      throw new MalformedTreeException(null, this, "Cannot add children to an undo edit"); //$NON-NLS-1$
   }

   /*
    * @see org.eclipse.text.edits.MultiTextEdit#aboutToBeAdded(org.eclipse.text.edits .TextEdit)
    */
   void aboutToBeAdded(TextEdit parent)
   {
      throw new MalformedTreeException(parent, this, "Cannot add an undo edit to another edit"); //$NON-NLS-1$
   }

   UndoEdit dispatchPerformEdits(TextEditProcessor processor) throws BadLocationException
   {
      return processor.executeUndo();
   }

   void dispatchCheckIntegrity(TextEditProcessor processor) throws MalformedTreeException
   {
      processor.checkIntegrityUndo();
   }

   /* @see org.eclipse.text.edits.TextEdit#doCopy() */
   protected TextEdit doCopy()
   {
      return new UndoEdit(this);
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
   int performDocumentUpdating(Document document) throws BadLocationException
   {
      fDelta = 0;
      return fDelta;
   }

   void add(ReplaceEdit edit)
   {
      List children = internalGetChildren();
      if (children == null)
      {
         children = new ArrayList(2);
         internalSetChildren(children);
      }
      children.add(edit);
   }

   void defineRegion(int offset, int length)
   {
      internalSetOffset(offset);
      internalSetLength(length);
   }

   boolean deleteChildren()
   {
      return false;
   }
}
