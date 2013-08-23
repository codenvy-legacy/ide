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
 * Extension interface for {@link org.eclipse.jface.text.IDocumentPartitioningListener}.
 * <p/>
 * Replaces the original notification mechanism by telling the listener the minimal region that comprises all partitioning
 * changes.
 *
 * @see org.eclipse.jdt.client.text.jface.text.IDocumentPartitionerExtension
 * @since 2.0
 */
public interface IDocumentPartitioningListenerExtension {

    /**
     * The partitioning of the given document changed in the given region.
     * <p/>
     * In version 3.0, this method has been replaced with
     * {@link IDocumentPartitioningListenerExtension2#documentPartitioningChanged(DocumentPartitioningChangedEvent)}.
     *
     * @param document
     *         the document whose partitioning changed
     * @param region
     *         the region in which the partitioning changed
     * @see IDocumentPartitioningListenerExtension2#documentPartitioningChanged(DocumentPartitioningChangedEvent)
     * @see IDocument#addDocumentPartitioningListener(IDocumentPartitioningListener)
     */
    void documentPartitioningChanged(IDocument document, IRegion region);
}
