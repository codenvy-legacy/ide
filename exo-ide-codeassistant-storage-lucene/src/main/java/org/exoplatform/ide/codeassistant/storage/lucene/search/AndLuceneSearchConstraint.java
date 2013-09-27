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

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;

/** Combine with logical AND two constrains. */
public class AndLuceneSearchConstraint implements LuceneSearchConstraint {

    private final LuceneSearchConstraint leftConstraint;

    private final LuceneSearchConstraint rightConstraint;

    public AndLuceneSearchConstraint(LuceneSearchConstraint leftConstraint, LuceneSearchConstraint rightConstraint) {
        this.leftConstraint = leftConstraint;
        this.rightConstraint = rightConstraint;

    }

    /** @see org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneSearchConstraint#getQuery() */
    @Override
    public Query getQuery() throws CodeAssistantException {
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(leftConstraint.getQuery(), BooleanClause.Occur.MUST);
        booleanQuery.add(rightConstraint.getQuery(), BooleanClause.Occur.MUST);
        return booleanQuery;
    }

    /**
     * Combine with logical AND two constrains.
     *
     * @param leftConstraint
     * @param rightConstraint
     * @return
     */
    public static LuceneSearchConstraint and(LuceneSearchConstraint leftConstraint,
                                             LuceneSearchConstraint rightConstraint) {
        if (leftConstraint.matchAll() && !rightConstraint.matchAll()) {
            return rightConstraint;
        } else if (!leftConstraint.matchAll() && rightConstraint.matchAll()) {
            return leftConstraint;
        }
        return new AndLuceneSearchConstraint(leftConstraint, rightConstraint);
    }

    /** @see org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneSearchConstraint#matchAll() */
    @Override
    public boolean matchAll() {
        return false;
    }

}
