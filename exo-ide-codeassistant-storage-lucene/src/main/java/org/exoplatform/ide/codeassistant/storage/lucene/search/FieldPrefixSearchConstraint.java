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
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;

/** Field prefixed constraint */
public class FieldPrefixSearchConstraint implements LuceneSearchConstraint {
    /** Name of field to search */
    private final String filedName;

    /** Prefix of the field */
    private final String prefix;

    public FieldPrefixSearchConstraint(String filedName, String prefix) {
        super();
        this.filedName = filedName;
        this.prefix = prefix;
    }

    /** @throws CodeAssistantException */
    @Override
    public Query getQuery() throws CodeAssistantException {
        return new PrefixQuery(new Term(filedName, prefix));
    }

    public static LuceneSearchConstraint prefix(String fieldName, String prefix) {
        return new FieldPrefixSearchConstraint(fieldName, prefix);
    }

    /** @see org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneSearchConstraint#matchAll() */
    @Override
    public boolean matchAll() {
        return prefix == null || prefix.isEmpty();
    }
}
