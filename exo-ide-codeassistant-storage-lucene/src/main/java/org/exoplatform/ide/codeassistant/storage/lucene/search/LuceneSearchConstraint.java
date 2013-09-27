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
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;

/**
 * Capability to search some functionality according to the specific structure.
 * <p/>
 * Implementation should provide necessary document changes and assist in the
 * query creation
 */
public interface LuceneSearchConstraint {
    /**
     * @return - lucene query
     * @throws CodeAssistantException
     */
    public Query getQuery() throws CodeAssistantException;

    /** @return - true if this constraint match all documents. */
    public boolean matchAll();
}
