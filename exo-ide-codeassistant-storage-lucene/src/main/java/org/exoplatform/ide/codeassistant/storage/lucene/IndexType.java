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
package org.exoplatform.ide.codeassistant.storage.lucene;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/** Types of indexes. */
public enum IndexType {

    JAVA("indexType", "java"), //
    DOC("indexType", "doc"), //
    PACKAGE("indexType", "package");

    private final String indexFieldName;

    private final String indexFieldValue;

    private IndexType(String indexFieldName, String indexFieldValue) {
        this.indexFieldName = indexFieldName;
        this.indexFieldValue = indexFieldValue;
    }

    /** @return the indexFieldName */
    public String getIndexFieldName() {
        return indexFieldName;
    }

    /** @return the indexFieldValue */
    public String getIndexFieldValue() {
        return indexFieldValue;
    }

    public Query getQuery() {
        return new TermQuery(new Term(indexFieldName, indexFieldValue));
    }

}
