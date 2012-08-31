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
package org.exoplatform.ide.text;

/**
 * Extension interface for {@link org.eclipse.jface.text.IDocumentPartitioner}.
 * <p>
 * Replaces the original concept of the document partitioner by returning the minimal region that includes all partition changes
 * caused by the invocation of the document partitioner. The method <code>documentChanged2</code> is considered the replacement of
 * {@link org.eclipse.jface.text.IDocumentPartitioner#documentChanged(DocumentEvent)}.
 * 
 * @since 2.0
 */
public interface IDocumentPartitionerExtension
{

   /**
    * The document has been changed. The partitioner updates the document's partitioning and returns the minimal region that
    * comprises all partition changes caused in response to the given document event. This method returns <code>null</code> if the
    * partitioning did not change.
    * <p>
    * 
    * Will be called by the connected document and is not intended to be used by clients other than the connected document.
    * <p>
    * Replaces {@link IDocumentPartitioner#documentChanged(DocumentEvent)}.
    * 
    * @param event the event describing the document change
    * @return the region of the document in which the partition type changed or <code>null</code>
    */
   IRegion documentChanged2(DocumentEvent event);
}
