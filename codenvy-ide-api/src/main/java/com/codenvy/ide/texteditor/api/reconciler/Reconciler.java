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

import com.codenvy.ide.texteditor.api.TextEditorPartView;

/**
 * An <code>Reconciler</code> defines and maintains a model of the content
 * of the text view document in the presence of changes applied to this
 * document.
 * Reconciler have a list of {@link ReconcilingStrategy}
 * objects each of which is registered for a  particular document content type.
 * The reconciler uses the strategy objects to react on the changes applied
 * to the text view document.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface Reconciler {

    /**
     * Installs the reconciler on the given text view. After this method has been
     * finished, the reconciler is operational, i.e., it works without requesting
     * further client actions until <code>uninstall</code> is called.
     *
     * @param view
     *         the view on which the reconciler is installed
     */
    void install(TextEditorPartView view);

    /**
     * Removes the reconciler from the text view it has
     * previously been installed on.
     */
    void uninstall();

    /**
     * Returns the reconciling strategy registered with the reconciler
     * for the specified content type.
     *
     * @param contentType
     *         the content type for which to determine the reconciling strategy
     * @return the reconciling strategy registered for the given content type, or
     *         <code>null</code> if there is no such strategy
     */
    ReconcilingStrategy getReconcilingStrategy(String contentType);

    /**
     * Returns the partitioning this reconciler is using.
     *
     * @return the partitioning this reconciler is using
     */
    String getDocumentPartitioning();
}
