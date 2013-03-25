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

class UndoCollector
{

   protected UndoEdit undo;

   private int fOffset;

   private int fLength;

   private String fLastCurrentText;

   public UndoCollector(TextEdit root)
   {
      fOffset = root.getOffset();
      fLength = root.getLength();
   }

   // public void connect(IDocument document) {
   // document.addDocumentListener(this);
   // undo= new UndoEdit();
   // }
   //
   // public void disconnect(IDocument document) {
   // if (undo != null) {
   // document.removeDocumentListener(this);
   // undo.defineRegion(fOffset, fLength);
   // }
   // }
   //
   // public void documentChanged(DocumentEvent event) {
   // fLength+= getDelta(event);
   // }
   //
   // private static int getDelta(DocumentEvent event) {
   // String text= event.getText();
   // return text == null ? -event.getLength() : (text.length() - event.getLength());
   // }
   //
   // public void documentAboutToBeChanged(DocumentEvent event) {
   // int offset= event.getOffset();
   // int currentLength= event.getLength();
   // String currentText= null;
   // try {
   // currentText= event.getDocument().get(offset, currentLength);
   // } catch (BadLocationException cannotHappen) {
   //         Assert.isTrue(false, "Can't happen"); //$NON-NLS-1$
   // }
   //
   // /*
   // * see https://bugs.eclipse.org/bugs/show_bug.cgi?id=93634
   // * If the same string is replaced on many documents (e.g. rename
   // * package), the size of the undo can be reduced by using the same
   // * String instance in all edits, instead of using the unique String
   // * returned from IDocument.get(int, int).
   // */
   // if (fLastCurrentText != null && fLastCurrentText.equals(currentText))
   // currentText= fLastCurrentText;
   // else
   // fLastCurrentText= currentText;
   //
   // String newText= event.getText();
   // undo.add(new ReplaceEdit(offset, newText != null ? newText.length() : 0, currentText));
   // }
}
