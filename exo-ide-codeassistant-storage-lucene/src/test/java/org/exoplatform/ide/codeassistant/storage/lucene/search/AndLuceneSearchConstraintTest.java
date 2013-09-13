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
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.junit.Test;

import static org.exoplatform.ide.codeassistant.storage.lucene.search.AndLuceneSearchConstraint.and;
import static org.exoplatform.ide.codeassistant.storage.lucene.search.FieldPrefixSearchConstraint.prefix;
import static org.exoplatform.ide.codeassistant.storage.lucene.search.SearchByFieldConstraint.eq;
import static org.junit.Assert.*;

/**
 *
 */
public class AndLuceneSearchConstraintTest {

    @Test
    public void shouldCreateInstanceFromStaticMethodIfBothDoestMatchAll() throws Exception {
        LuceneSearchConstraint constraint = and(prefix("test", "test"), eq("test", "somePrefix"));
        assertTrue(constraint instanceof AndLuceneSearchConstraint);
    }

    @Test
    public void shouldReturnLeftIfRightMatchAll() throws Exception {
        SearchByFieldConstraint left = eq("test", "somePrefix");
        LuceneSearchConstraint right = prefix("test", "");
        assertTrue(right.matchAll());
        assertFalse(left.matchAll());

        LuceneSearchConstraint constraint = and(left, right);

        assertEquals(left, constraint);
    }

    @Test
    public void shouldReturnRightIfLeftMatchAll() throws Exception {
        SearchByFieldConstraint right = eq("test", "somePrefix");
        LuceneSearchConstraint left = prefix("test", "");

        assertFalse(right.matchAll());
        assertTrue(left.matchAll());

        LuceneSearchConstraint constraint = and(left, right);

        assertEquals(right, constraint);
    }

    @Test
    public void shouldCreateReturnBooleanQuery() throws Exception {
        LuceneSearchConstraint constraint = and(prefix("test", "test"), eq("test", "somePrefix"));
        assertTrue(constraint.getQuery() instanceof BooleanQuery);
    }

    @Test
    public void boolenaQueryShouldContainsTwoClauses() throws Exception {
        LuceneSearchConstraint constraint = and(prefix("test", "test"), eq("test", "somePrefix"));
        BooleanQuery query = (BooleanQuery)constraint.getQuery();
        assertEquals(2, query.getClauses().length);
    }

    @Test
    public void boolenaQueryShouldMatchBothConstrains() throws Exception {
        LuceneSearchConstraint left = prefix("test", "test");
        SearchByFieldConstraint right = eq("test", "somePrefix");
        LuceneSearchConstraint constraint = and(left, right);
        BooleanQuery query = (BooleanQuery)constraint.getQuery();
        BooleanClause[] clauses = query.getClauses();

        assertEquals(left.getQuery(), clauses[0].getQuery());
        assertEquals(right.getQuery(), clauses[1].getQuery());
    }

    @Test
    public void occurShouldBeBust() throws Exception {
        LuceneSearchConstraint left = prefix("test", "test");
        SearchByFieldConstraint right = eq("test", "somePrefix");
        LuceneSearchConstraint constraint = and(left, right);
        BooleanQuery query = (BooleanQuery)constraint.getQuery();
        BooleanClause[] clauses = query.getClauses();

        assertEquals(Occur.MUST, clauses[0].getOccur());
        assertEquals(Occur.MUST, clauses[1].getOccur());
    }

    @Test
    public void shouldNotMachAll() throws Exception {
        LuceneSearchConstraint constraint = and(prefix("test", "test"), eq("test", "somePrefix"));
        assertFalse(constraint.matchAll());

    }
}
