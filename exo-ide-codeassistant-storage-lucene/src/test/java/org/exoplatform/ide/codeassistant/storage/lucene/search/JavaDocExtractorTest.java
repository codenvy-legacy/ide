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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.IndexReader;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.DataIndexer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class JavaDocExtractorTest {
    @Mock
    private IndexReader reader;

    private final JavaDocExtractor extractor = new JavaDocExtractor();

    @Test
    public void shouldCallReaderGetDocumentWithSameIdAndFieldSelector() throws Exception {

        Document luceneDocument = new Document();
        when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(luceneDocument);

        extractor.getValue(reader, 5);

        verify(reader).document(eq(5), (FieldSelector)anyObject());
        verifyNoMoreInteractions(reader);
    }

    @Test
    public void shouldReconstructJavaDocInfo() throws Exception {
        final String fqn = "org.exoplatform.ide.codeassistant.test";
        final String doc = "test javadoc";

        Document luceneDocument = new DataIndexer().createJavaDocDocument(fqn, doc, "rt");

        when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(luceneDocument);

        String actualJavaDoc = extractor.getValue(reader, 5);

        assertEquals(doc, actualJavaDoc);
    }

}
