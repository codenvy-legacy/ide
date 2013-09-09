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
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.exoplatform.ide.codeassistant.asm.ClassParser.getClassFile;
import static org.exoplatform.ide.codeassistant.asm.ClassParser.parse;
import static org.junit.Assert.assertEquals;
import static test.ClassManager.createIndexForClass;
import static test.ClassManager.getAllTestClasses;

/**
 *
 */
public class LuceneTypeInfoWriterTest {
    private LuceneDataWriter writer;

    private LuceneInfoStorage luceneInfoStorage;

    @Before
    public void createIndex() throws Exception {
        luceneInfoStorage = new LuceneInfoStorage(new RAMDirectory());
        writer = new LuceneDataWriter(luceneInfoStorage);
    }

    @Test
    public void shouldIndexAllClasses() throws Exception {
        createIndexForClass(writer, getAllTestClasses());
        IndexReader reader = luceneInfoStorage.getTypeInfoIndexSearcher().getIndexReader();
        assertEquals(getAllTestClasses().length, reader.numDocs());
        reader.close();
    }

    @Test
    public void shouldBeAbleToAddTwice() throws Exception {
        writer.addTypeInfo(Arrays.asList(new TypeInfo[]{parse(getClassFile(Object.class))}), "rt");
        IndexReader reader = luceneInfoStorage.getTypeInfoIndexSearcher().getIndexReader();
        assertEquals(1, reader.numDocs());
        writer.addTypeInfo(Arrays.asList(new TypeInfo[]{parse(getClassFile(List.class))}), "rt");
        reader = luceneInfoStorage.getTypeInfoIndexSearcher().getIndexReader();
        assertEquals(2, reader.numDocs());

    }
}
