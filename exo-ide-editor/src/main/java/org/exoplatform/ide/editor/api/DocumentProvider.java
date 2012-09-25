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
package org.exoplatform.ide.editor.api;

import org.exoplatform.ide.source.AnnotationModel;
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
    * Connects the given element to this document provider. This tells the provider
    * that caller of this method is interested to work with the document provided for
    * the given domain model element. By counting the invocations of this method and
    * <code>disconnect(Object)</code> this provider can assume to know the
    * correct number of clients working with the document provided for that
    * domain model element. <p>
    * The given element must not be <code>null</code>.
    *
    * @param element the element
    *
    */
   //TODO throw some exception 
   void connect(Object element);

   /**
    * Disconnects the given element from this document provider. This tells the provider
    * that the caller of this method is no longer interested in working with the document
    * provided for the given domain model element. By counting the invocations of
    * <code>connect(Object)</code> and of this method this provider can assume to
    * know the correct number of clients working with the document provided for that
    * domain model element. <p>
    * The given element must not be <code>null</code>.
    *
    * @param element the element
    */
   void disconnect(Object element);

   /**
    * Returns the document for the given element. Usually the document contains
    * a textual presentation of the content of the element, or is the element itself.
    *
    * @param element the element, or <code>null</code>
    * @return the document, or <code>null</code> if none
    */
   Document getDocument(Object element);
   
   /**
    * Saves the given document provided for the given element.
    *
    * @param monitor a progress monitor to report progress and request cancelation
    * @param element the element, or <code>null</code>
    * @param document the document
    * @param overwrite indicates whether overwrite should be performed
    *          while saving the given element if necessary
    */
   void saveDocument(Object element, Document document, boolean overwrite);
   
   /**
    * Returns the annotation model for the given element.
    *
    * @param element the element, or <code>null</code>
    * @return the annotation model, or <code>null</code> if none
    */
   AnnotationModel getAnnotationModel(Object element);
}
