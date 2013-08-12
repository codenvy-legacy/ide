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
package com.codenvy.ide.texteditor.api;

import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.RegionImpl;

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
    void format(Document document, RegionImpl region);

    //TODO
//   /**
//    * Returns the formatting strategy registered for the given content type.
//    *
//    * @param contentType the content type for which to look up the formatting strategy
//    * @return the formatting strategy for the given content type, or
//    *    <code>null</code> if there is no such strategy
//    */
//   IFormattingStrategy getFormattingStrategy(String contentType);
}
