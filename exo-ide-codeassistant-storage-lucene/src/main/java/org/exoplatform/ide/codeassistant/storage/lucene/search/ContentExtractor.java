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
package org.exoplatform.ide.codeassistant.storage.lucene.search;

import org.apache.lucene.index.IndexReader;

import java.io.IOException;

/** Extract content from index by lucene document identificator. */
public interface ContentExtractor<T> {
    /**
     * @param reader
     *         - current lucene Index read.
     * @param doc
     *         - id of lucene document
     * @return - content of the document
     * @throws IOException
     */
    T getValue(IndexReader reader, int doc) throws IOException;
}
