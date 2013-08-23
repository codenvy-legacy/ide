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
 * Extension interface to {@link org.eclipse.jface.text.IDocumentPartitioningListener}.
 * <p/>
 * <p/>
 * Replaces the previous notification mechanisms by introducing an explicit document partitioning changed event.
 *
 * @see org.eclipse.jface.text.DocumentPartitioningChangedEvent
 * @since 3.0
 */
public interface IDocumentPartitioningListenerExtension2 {

    /**
     * Signals the change of document partitionings.
     * <p/>
     * This method replaces {@link IDocumentPartitioningListener#documentPartitioningChanged(IDocument)} and
     * {@link IDocumentPartitioningListenerExtension#documentPartitioningChanged(IDocument, IRegion)}
     *
     * @param event
     *         the event describing the change
     * @see IDocument#addDocumentPartitioningListener(IDocumentPartitioningListener)
     */
    void documentPartitioningChanged(DocumentPartitioningChangedEvent event);
}
