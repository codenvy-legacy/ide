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
package org.exoplatform.ide.codeassistant.storage.lucene.writer;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.RAMDirectory;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  4:26:14 PM Mar 5, 2012 evgen $
 */
public class LucenePackageWriterTest {
    private LuceneDataWriter writer;

    private LuceneInfoStorage luceneInfoStorage;

    @Before
    public void createIndex() throws Exception {
        luceneInfoStorage = new LuceneInfoStorage(new RAMDirectory());
        writer = new LuceneDataWriter(luceneInfoStorage);
    }

    @Test
    public void shouldAddPackage() throws Exception {
        writer.addPackages(new TreeSet<String>(Arrays.asList("java", "java.lang", "org", "org.exoplatform", "org.exoplatform.ide")), "rt");
        IndexReader reader = luceneInfoStorage.getTypeInfoIndexSearcher().getIndexReader();
        assertEquals(5, reader.numDocs());
        reader.close();
    }
}
