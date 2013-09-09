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

import org.apache.lucene.search.*;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.storage.lucene.IndexType;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Execute queries against lucene index. */
public class LuceneQueryExecutor {
    private final LuceneInfoStorage infoStorage;

    public LuceneQueryExecutor(LuceneInfoStorage infoStorage) {
        this.infoStorage = infoStorage;
    }

    /**
     * Execute query
     *
     * @param select
     *         - extractor of results.
     * @param from
     *         - type of index to search (java code ,jvadoc)
     * @param where
     *         - constrain of search
     * @param limit
     *         - limit number of results.
     * @param offset
     *         - offset in results.
     * @return - List of values depends on select extractor
     * @throws CodeAssistantException
     */
    public <T> List<T> executeQuery(ContentExtractor<T> select, IndexType from, LuceneSearchConstraint where, int limit,
                                    int offset) throws CodeAssistantException {
        if (limit < 0) {
            throw new CodeAssistantException(500, "Negative limit " + limit + " is not allowed");
        }

        if (offset < 0) {
            throw new CodeAssistantException(500, "Negative offset " + offset + " is not allowed");
        }
        try {
            IndexSearcher searcher = infoStorage.getTypeInfoIndexSearcher();

            Query contentQuery = from.getQuery();
            if (!where.matchAll()) {
                BooleanQuery booleanQuery = new BooleanQuery();
                booleanQuery.add(contentQuery, BooleanClause.Occur.MUST);
                booleanQuery.add(where.getQuery(), BooleanClause.Occur.MUST);
                contentQuery = booleanQuery;
            }

            TopDocs docs = searcher.search(contentQuery, limit + offset);

            List<T> result = new ArrayList<T>(Math.max(0, docs.totalHits - offset));

            for (int i = offset; i < docs.scoreDocs.length; i++) {
                result.add(select.getValue(searcher.getIndexReader(), docs.scoreDocs[i].doc));
            }
            return result;

        } catch (IOException e) {
            throw new CodeAssistantException(404, e.getLocalizedMessage());
        }

    }

}
