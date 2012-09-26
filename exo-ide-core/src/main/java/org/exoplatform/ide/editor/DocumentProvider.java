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
package org.exoplatform.ide.editor;

import org.exoplatform.ide.text.Document;

/**
 *  A document provider maps between domain elements and documents. A document provider has the
 * following responsibilities:
 * <ul>
 * <li>create an annotation model of a domain model element
 * <li>create and manage a textual representation, i.e., a document, of a domain model element
 * <li>create and save the content of domain model elements based on given documents
 * <li>update the documents this document provider manages for domain model elements to changes
 * directly applied to those domain model elements
 * <li>notify all element state listeners about changes directly applied to domain model elements
 * this document provider manages a document for, i.e. the document provider must know which changes
 * of a domain model element are to be interpreted as element moves, deletes, etc.
 * </ul>
 * Text editors use document providers to bridge the gap between their input elements and the
 * documents they work on. A single document provider may be shared between multiple editors; the
 * methods take the editors' input elements as a parameter.
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface DocumentProvider
{
   /**
    * Returns the document for the given element. Usually the document contains
    * a textual presentation of the content of the element, or is the element itself.
    *
    * @param input the input, or <code>null</code>
    * @return the document, or <code>null</code> if none
    */
   Document getDocument(EditorInput input);
   
   /**
    * Saves the given document provided for the given element.
    *
    * @param input the input, or <code>null</code>
    * @param document the document
    * @param overwrite indicates whether overwrite should be performed
    *          while saving the given element if necessary
    */
   void saveDocument(EditorInput input, Document document, boolean overwrite);
   
   //TODO
//   /**
//    * Returns the annotation model for the given input.
//    *
//    * @param input the input, or <code>null</code>
//    * @return the annotation model, or <code>null</code> if none
//    */
//   AnnotationModel getAnnotationModel(EditorInput input);
}
