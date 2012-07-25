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

import com.google.collide.shared.document.Document;

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

   private IDocument document;
   private Document editorDocument;

   /**
    * @see org.exoplatform.ide.editor.text.IDocumentListener#documentChanged(org.exoplatform.ide.editor.text.DocumentEvent)
    */
   @Override
   public void documentChanged(DocumentEvent event)
   {
//      editorDocument.
   }

   /**
    * @param document
    */
   public void setDocument(IDocument document)
   {
      this.document = document;
   }

   /**
    * @param editorDocument
    */
   public void setEditorDocument(Document editorDocument)
   {
      this.editorDocument = editorDocument;
   }

}
