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
package com.codenvy.ide.texteditor.api.reconciler;

import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Region;

/**
 * A reconciling strategy is used by an reconciler to reconcile a model
 * based on text of a particular content type.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface ReconcilingStrategy {
    /**
     * Tells this reconciling strategy on which document it will
     * work. This method will be called before any other method
     * and can be called multiple times. The regions passed to the
     * other methods always refer to the most recent document
     * passed into this method.
     *
     * @param document
     *         the document on which this strategy will work
     */
    void setDocument(Document document);

    /**
     * Activates incremental reconciling of the specified dirty region.
     * As a dirty region might span multiple content types, the segment of the
     * dirty region which should be investigated is also provided to this
     * reconciling strategy. The given regions refer to the document passed into
     * the most recent call of {@link #setDocument(Document)}.
     *
     * @param dirtyRegion
     *         the document region which has been changed
     * @param subRegion
     *         the sub region in the dirty region which should be reconciled
     */
    void reconcile(DirtyRegion dirtyRegion, Region subRegion);

    /**
     * Activates non-incremental reconciling. The reconciling strategy is just told
     * that there are changes and that it should reconcile the given partition of the
     * document most recently passed into {@link #setDocument(Document)}.
     *
     * @param partition
     *         the document partition to be reconciled
     */
    void reconcile(Region partition);
}
