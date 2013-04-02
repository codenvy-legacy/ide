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
