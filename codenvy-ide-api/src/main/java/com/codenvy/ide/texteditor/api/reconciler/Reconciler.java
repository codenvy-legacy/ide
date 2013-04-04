/*
 * Copyright (C) 2012 eXo Platform SAS.
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
