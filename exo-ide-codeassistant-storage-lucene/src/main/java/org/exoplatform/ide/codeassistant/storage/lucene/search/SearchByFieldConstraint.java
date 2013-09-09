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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.shared.JavaType;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;

/** Constrain where field match specific value. */
public class SearchByFieldConstraint implements LuceneSearchConstraint {

    private final String filedName;

    private final String value;

    public SearchByFieldConstraint(String filedName, String value) {
        super();
        this.filedName = filedName;
        this.value = value;
    }

    /** @throws CodeAssistantException */
    @Override
    public Query getQuery() throws CodeAssistantException {
        return new TermQuery(new Term(filedName, value));
    }

    /**
     * Create SearchByFieldConstraint where fieldName = value.
     *
     * @param fieldName
     * @param value
     * @return
     */
    public static SearchByFieldConstraint eq(String fieldName, String value) {
        return new SearchByFieldConstraint(fieldName, value);
    }

    /**
     * Create SearchByFieldConstraint where DataIndexFields.ENTITY_TYPE = value.
     *
     * @param type
     * @return
     */
    public static LuceneSearchConstraint eqJavaType(JavaType type) {
        return new SearchByFieldConstraint(DataIndexFields.ENTITY_TYPE, type.toString());
    }

    /** @see org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneSearchConstraint#matchAll() */
    @Override
    public boolean matchAll() {
        return false;
    }
}
