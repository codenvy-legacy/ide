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
package com.codenvy.ide.texteditor.api;

import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Region;

/**
 * The interface of a document content formatter. The formatter formats ranges
 * within documents. The documents are modified by the formatter.<p>
 * The content formatter is assumed to determine the partitioning of the document
 * range to be formatted. For each partition, the formatter determines based
 * on the partition's content type the formatting strategy to be used. Before
 * the first strategy is activated all strategies are informed about the
 * start of the formatting process. After that, the formatting strategies are
 * activated in the sequence defined by the partitioning of the document range to be
 * formatted. It is assumed that a strategy must be finished before the next strategy
 * can be activated. After the last strategy has been finished, all strategies are
 * informed about the termination of the formatting process.</p>
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface ContentFormatter {
    /**
     * Formats the given region of the specified document.The formatter may safely
     * assume that it is the only subject that modifies the document at this point in time.
     *
     * @param document
     *         the document to be formatted
     * @param region
     *         the region within the document to be formatted
     */
    void format(Document document, Region region);
}
