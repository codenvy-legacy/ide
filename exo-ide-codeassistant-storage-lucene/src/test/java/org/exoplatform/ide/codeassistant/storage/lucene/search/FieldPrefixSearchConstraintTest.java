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

import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.junit.Test;

import static org.exoplatform.ide.codeassistant.storage.lucene.search.FieldPrefixSearchConstraint.prefix;
import static org.junit.Assert.*;

/**
 *
 */
public class FieldPrefixSearchConstraintTest {
    @Test
    public void shouldCreateInstanceFromStaticMethod() throws Exception {
        LuceneSearchConstraint constraint = prefix("test", "test");
        assertTrue(constraint instanceof FieldPrefixSearchConstraint);
    }

    @Test
    public void shouldMatchAllIfPrefixNull() throws Exception {
        LuceneSearchConstraint constraint = prefix("test", null);
        assertTrue(constraint.matchAll());
    }

    @Test
    public void shouldMatchAllIfPrefixEmptyString() throws Exception {
        LuceneSearchConstraint constraint = prefix("test", "");
        assertTrue(constraint.matchAll());
    }

    @Test
    public void shouldNotMatchAllIfPrefixNotEmptyString() throws Exception {
        LuceneSearchConstraint constraint = prefix("test", "somePrefix");
        assertFalse(constraint.matchAll());
    }

    @Test
    public void shouldConstructPrefixedQuery() throws Exception {
        LuceneSearchConstraint constraint = prefix("test", "somePrefix");
        Query query = constraint.getQuery();
        assertTrue(query instanceof PrefixQuery);
    }

    @Test
    public void prefixQueryShouldMachArguments() throws Exception {
        LuceneSearchConstraint constraint = prefix("test", "somePrefix");
        PrefixQuery query = (PrefixQuery)constraint.getQuery();
        assertEquals("test", query.getPrefix().field());
        assertEquals("somePrefix", query.getPrefix().text());
    }

}
