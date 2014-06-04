/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.text;

/**
 *
 */
public interface DocumentPartitioningListener {

    /**
     * Signals the change of document partitionings.
     * <p/>
     *
     * @param event
     *         the event describing the change
     * @see Document#addDocumentPartitioningListener(DocumentPartitioningListener)
     */
    void documentPartitioningChanged(DocumentPartitioningChangedEvent event);
}
