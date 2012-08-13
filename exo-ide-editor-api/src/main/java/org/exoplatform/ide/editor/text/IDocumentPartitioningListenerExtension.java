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
package org.exoplatform.ide.editor.text;

/**
 * Extension interface for {@link org.eclipse.jface.text.IDocumentPartitioningListener}.
 * <p>
 * Replaces the original notification mechanism by telling the listener the minimal region that comprises all partitioning
 * changes.
 * 
 * @see org.eclipse.jdt.client.text.jface.text.IDocumentPartitionerExtension
 * @since 2.0
 */
public interface IDocumentPartitioningListenerExtension
{

   /**
    * The partitioning of the given document changed in the given region.
    * <p>
    * In version 3.0, this method has been replaced with
    * {@link IDocumentPartitioningListenerExtension2#documentPartitioningChanged(DocumentPartitioningChangedEvent)}.
    * 
    * @param document the document whose partitioning changed
    * @param region the region in which the partitioning changed
    * @see IDocumentPartitioningListenerExtension2#documentPartitioningChanged(DocumentPartitioningChangedEvent)
    * @see IDocument#addDocumentPartitioningListener(IDocumentPartitioningListener)
    */
   void documentPartitioningChanged(IDocument document, IRegion region);
}
