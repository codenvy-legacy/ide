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
 * Extension interface for {@link org.eclipse.jface.text.IDocumentPartitioner}.
 * <p/>
 * Replaces the original concept of the document partitioner by returning the minimal region that includes all partition changes
 * caused by the invocation of the document partitioner. The method <code>documentChanged2</code> is considered the replacement of
 * {@link org.eclipse.jface.text.IDocumentPartitioner#documentChanged(DocumentEvent)}.
 *
 * @since 2.0
 */
public interface IDocumentPartitionerExtension {

    /**
     * The document has been changed. The partitioner updates the document's partitioning and returns the minimal region that
     * comprises all partition changes caused in response to the given document event. This method returns <code>null</code> if the
     * partitioning did not change.
     * <p/>
     * <p/>
     * Will be called by the connected document and is not intended to be used by clients other than the connected document.
     * <p/>
     * Replaces {@link IDocumentPartitioner#documentChanged(DocumentEvent)}.
     *
     * @param event
     *         the event describing the document change
     * @return the region of the document in which the partition type changed or <code>null</code>
     */
    IRegion documentChanged2(DocumentEvent event);
}
