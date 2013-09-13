/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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
