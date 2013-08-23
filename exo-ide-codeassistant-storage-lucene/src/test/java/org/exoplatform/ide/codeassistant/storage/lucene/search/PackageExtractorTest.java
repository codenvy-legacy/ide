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
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  11:02:46 AM Mar 6, 2012 evgen $
 */
@RunWith(MockitoJUnitRunner.class)
public class PackageExtractorTest {
    @Mock
    private IndexReader reader;

    private PackageExtractor extractor = new PackageExtractor();

    @Test
    public void shouldReconstructPackage() throws Exception {
        String pack = "org.exoplatform";
        Document luceneDocument = new DataIndexer().createPackageDocument(pack, "rt");
        when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(luceneDocument);
        String value = extractor.getValue(reader, 3);
        assertEquals(pack, value);
    }

}
