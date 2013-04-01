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
package org.exoplatform.ide.editor.shared.text;

/**
 * Interface for objects which are interested in getting informed about document changes. A listener is informed about document
 * changes before they are applied and after they have been applied. It is ensured that the document event passed into the
 * listener is the same for the two notifications, i.e. the two document events can be checked using object identity.
 * <p>
 * Clients may implement this interface.
 * </p>
 * 
 * @see org.eclipse.jface.text.IDocument
 */
public interface IDocumentListener
{

   /**
    * The manipulation described by the document event will be performed.
    * 
    * @param event the document event describing the document change
    */
   void documentAboutToBeChanged(DocumentEvent event);

   /**
    * The manipulation described by the document event has been performed.
    * 
    * @param event the document event describing the document change
    */
   void documentChanged(DocumentEvent event);
}
