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

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.RAMDirectory;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static org.exoplatform.ide.codeassistant.asm.ClassParser.getClassFile;
import static org.exoplatform.ide.codeassistant.asm.ClassParser.parse;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: LuceneTypeInfoRemoveTest.java Oct 24, 2012 vetal $
 */
public class LuceneRemoveInfoTest {

    private LuceneDataWriter writer;

    private LuceneInfoStorage luceneInfoStorage;

    private IndexWriter indexWriter;

    RAMDirectory indexDirectory;

    @Before
    public void createIndex() throws Exception {
        indexDirectory = new RAMDirectory();
        luceneInfoStorage = new LuceneInfoStorage(indexDirectory);
        writer = new LuceneDataWriter(luceneInfoStorage);
        indexWriter = new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
    }

    @Test
    public void removeTypeInfoTest() throws Exception {
        //full index
        List<TypeInfo> typeInfos = Arrays.asList(new TypeInfo[]{parse(getClassFile(Object.class))});
        DataIndexer indexer = new DataIndexer();
        for (TypeInfo typeInfo : typeInfos) {
            indexWriter.addDocument(indexer.createTypeInfoDocument(typeInfo, "rt"));
        }
        indexWriter.commit();
        indexWriter.close();

        //insure that document add
        IndexReader reader = IndexReader.open(indexDirectory, true);
        assertEquals(1, reader.numDocs());

        writer.removeTypeInfo("rt");

        reader = IndexReader.open(indexDirectory, true);
        assertEquals(0, reader.numDocs());

    }


    @Test
    public void removePackageTest() throws Exception {
        //full index
        DataIndexer indexer = new DataIndexer();
        TreeSet<String> packages = new TreeSet<String>(Arrays.asList("java", "java.lang", "org", "org.exoplatform", "org.exoplatform.ide"));
        for (String pack : packages) {
            indexWriter.addDocument(indexer.createPackageDocument(pack, "rt"));
        }
        indexWriter.commit();
        indexWriter.close();

        //insure that document add
        IndexReader reader = IndexReader.open(indexDirectory, true);
        assertEquals(5, reader.numDocs());

        writer.removePackages("rt");

        reader = IndexReader.open(indexDirectory, true);
        assertEquals(0, reader.numDocs());

    }

}
