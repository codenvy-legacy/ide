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
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;

import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class DependecySearchConstraint implements LuceneSearchConstraint {

    private final String field;

    private final Set<String> artifacts;

    /**
     * @param artifact
     * @param artifacts
     */
    public DependecySearchConstraint(String field, Set<String> artifacts) {
        this.field = field;
        this.artifacts = artifacts;
    }

    /** @see org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneSearchConstraint#getQuery() */
    @Override
    public Query getQuery() throws CodeAssistantException {
        BooleanQuery q = new BooleanQuery();
        for (String s : artifacts)
            q.add(new TermQuery(new Term(field, s)), BooleanClause.Occur.SHOULD);
        return q;
    }

    /** @see org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneSearchConstraint#matchAll() */
    @Override
    public boolean matchAll() {
        return false;
    }

    public static LuceneSearchConstraint inArtifacts(Set<String> artifacts) {
        return new DependecySearchConstraint(DataIndexFields.ARTIFACT, artifacts);
    }

}
