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
 * Interface of objects which are interested in getting informed about changes of a document's partitioning.
 * <p>
 * Clients may implement this interface.
 * </p>
 * <p>
 * In order to provided backward compatibility for clients of <code>IDocumentPartitioningListener</code>, extension interfaces are
 * used to provide a means of evolution. The following extension interfaces exist:
 * <ul>
 * <li> {@link org.eclipse.jface.text.IDocumentPartitioningListenerExtension} since version 2.0 replacing the original notification
 * mechanism.</li>
 * <li> {@link org.eclipse.jface.text.IDocumentPartitioningListenerExtension2} since version 3.0 replacing all previous
 * notification mechanisms. Thus, implementers up-to-date with version 3.0 do not have to implement
 * {@link org.eclipse.jface.text.IDocumentPartitioningListenerExtension}.</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jface.text.IDocumentPartitioningListenerExtension
 * @see org.eclipse.jface.text.IDocumentPartitioningListenerExtension2
 * @see org.eclipse.jface.text.IDocument
 * @see org.eclipse.jface.text.IDocumentPartitioner
 */
public interface IDocumentPartitioningListener {

    /**
     * The partitioning of the given document changed.
     * <p/>
     * In version 2.0 this method has been replaces by
     * {@link IDocumentPartitioningListenerExtension#documentPartitioningChanged(IDocument, IRegion)}.
     * <p/>
     * In version 3.0 this method has been replaces by
     * {@link IDocumentPartitioningListenerExtension2#documentPartitioningChanged(DocumentPartitioningChangedEvent)}
     * <p/>
     *
     * @param document
     *         the document whose partitioning changed
     * @see IDocumentPartitioningListenerExtension#documentPartitioningChanged(IDocument, IRegion)
     * @see IDocumentPartitioningListenerExtension2#documentPartitioningChanged(DocumentPartitioningChangedEvent)
     * @see IDocument#addDocumentPartitioningListener(IDocumentPartitioningListener)
     */
    void documentPartitioningChanged(IDocument document);
}
