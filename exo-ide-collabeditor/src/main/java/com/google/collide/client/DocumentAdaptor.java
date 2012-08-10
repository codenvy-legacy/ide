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
package com.google.collide.client;

import com.google.collide.shared.document.Line;

import com.google.collide.shared.document.LineInfo;

import com.google.collide.client.editor.EditorDocumentMutator;
import com.google.collide.shared.document.Document;
import com.google.collide.shared.document.DocumentMutator;

import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.DocumentEvent;
import org.exoplatform.ide.editor.text.IDocument;
import org.exoplatform.ide.editor.text.IDocumentListener;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class DocumentAdaptor implements IDocumentListener
{

   private DocumentMutator mutator;

   private Document editorDocument;

   /**
    * @see org.exoplatform.ide.editor.text.IDocumentListener#documentChanged(org.exoplatform.ide.editor.text.DocumentEvent)
    */
   @Override
   public void documentChanged(DocumentEvent event)
   {
//      mutator.insertText(editorDocument.getFirstLine(), 0, 0, "it's alive");
      try
      {
         IDocument document = event.getDocument();
         int lineNumber = document.getLineOfOffset(event.getOffset());
         int col = event.getOffset() - document.getLineOffset(lineNumber);
         
         LineInfo lineInfo = editorDocument.getLineFinder().findLine(lineNumber);
         Line line = lineInfo.line();
         mutator.deleteText(line, col, event.fLength);
//         StringBuilder b = new StringBuilder(line.getText());
//         int length = col + event.getLength();
//         int nextLine = lineNumber + 1;
         
//         while (length > b.length())
//         {
//            line = editorDocument.getLineFinder().findLine(nextLine).line();
//            b.append(line.getText());
//            mutator.deleteText(line, 0, line.length());
////            deleteLine(editorObject, nextLine);
//            // symbol '\n' not present in line content
//            length--;
//         }
//         b.replace(col, length, event.getText());
         
//         LineInfo lineIn = editorDocument.getLineFinder().findLine(lineNumber);
//         mutator.deleteText(lineIn.line(), 0, lineIn.line().length());
         mutator.insertText(line, col, event.fText);
//         setLineText(lineNumber, b.toString());
      }
      catch (BadLocationException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @param editorDocument
    * @param editorDocumentMutator
    */
   public void setDocument(Document editorDocument, EditorDocumentMutator editorDocumentMutator)
   {
      this.editorDocument = editorDocument;
      mutator = editorDocumentMutator;
   }

}
