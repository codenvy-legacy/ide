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

import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.exoplatform.ide.codeassistant.jvm.shared.JavaType;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;
import org.junit.Test;

import static org.exoplatform.ide.codeassistant.storage.lucene.search.SearchByFieldConstraint.eq;
import static org.exoplatform.ide.codeassistant.storage.lucene.search.SearchByFieldConstraint.eqJavaType;
import static org.junit.Assert.*;

/**
 *
 */
public class SearchByFieldConstraintTest {
    @Test
    public void shouldCreateInstanceFromStaticEqMethod() throws Exception {
        LuceneSearchConstraint constraint = eq("test", "test");
        assertTrue(constraint instanceof SearchByFieldConstraint);
    }

    @Test
    public void shouldCreateInstanceFromStaticEqJavaTypeMethod() throws Exception {
        LuceneSearchConstraint constraint = eqJavaType(JavaType.ANNOTATION);
        assertTrue(constraint instanceof SearchByFieldConstraint);
    }

    @Test
    public void shouldConstructTermQuery() throws Exception {
        LuceneSearchConstraint constraint = eq("test", "somePrefix");
        Query query = constraint.getQuery();
        assertTrue(query instanceof TermQuery);
    }

    @Test
    public void termQueryShouldMachArguments() throws Exception {
        LuceneSearchConstraint constraint = eq("test", "somePrefix");
        TermQuery query = (TermQuery)constraint.getQuery();
        assertEquals("test", query.getTerm().field());
        assertEquals("somePrefix", query.getTerm().text());
    }

    @Test
    public void termQueryFromJavaTypeShouldMachArguments() throws Exception {
        LuceneSearchConstraint constraint = eqJavaType(JavaType.ANNOTATION);
        TermQuery query = (TermQuery)constraint.getQuery();
        assertEquals(DataIndexFields.ENTITY_TYPE, query.getTerm().field());
        assertEquals(JavaType.ANNOTATION.toString(), query.getTerm().text());
    }

    @Test
    public void shouldNotMatchAll() throws Exception {
        LuceneSearchConstraint constraint = eqJavaType(JavaType.ANNOTATION);
        assertFalse(constraint.matchAll());
    }
}
