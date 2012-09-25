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
 * Extension interface to {@link org.eclipse.jface.text.IDocumentPartitioningListener}.
 * <p>
 * 
 * Replaces the previous notification mechanisms by introducing an explicit document partitioning changed event.
 * 
 * @see org.eclipse.jface.text.DocumentPartitioningChangedEvent
 * @since 3.0
 */
public interface IDocumentPartitioningListenerExtension2
{

   /**
    * Signals the change of document partitionings.
    * <p>
    * This method replaces {@link IDocumentPartitioningListener#documentPartitioningChanged(Document)} and
    * {@link IDocumentPartitioningListenerExtension#documentPartitioningChanged(Document, IRegion)}
    * 
    * @param event the event describing the change
    * @see Document#addDocumentPartitioningListener(IDocumentPartitioningListener)
    */
   void documentPartitioningChanged(DocumentPartitioningChangedEvent event);
}
