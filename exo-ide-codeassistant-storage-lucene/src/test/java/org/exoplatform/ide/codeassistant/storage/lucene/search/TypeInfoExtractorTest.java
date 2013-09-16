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

import test.classes.ATestClass2;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.index.IndexReader;
import org.exoplatform.ide.codeassistant.asm.ClassParser;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneCodeAssistantStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.DataIndexer;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TypeInfoExtractorTest {
    @Mock
    private IndexReader reader;

    @Mock
    private LuceneCodeAssistantStorage luceneCodeAssistantStorage;

    @InjectMocks
    private TypeInfoExtractor extractor;

    @Ignore
    @Test
    public void shouldReconstructTypeInfo() throws Exception {
        TypeInfo expected = ClassParser.parse(ClassParser.getClassFile(ATestClass2.class));
        Document luceneDocument = new DataIndexer().createTypeInfoDocument(expected, "rt");

        when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(luceneDocument);

        TypeInfo actual = extractor.getValue(reader, 5);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetDocumentFromReaderWithPredefinedSetOfFields() throws Exception {
        TypeInfo expected = ClassParser.parse(ClassParser.getClassFile(ATestClass2.class));
        Document luceneDocument = new DataIndexer().createTypeInfoDocument(expected, "rt");
        when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(luceneDocument);

        extractor.getValue(reader, 5);

        ArgumentCaptor<FieldSelector> model = ArgumentCaptor.forClass(FieldSelector.class);
        verify(reader).document(eq(5), model.capture());

        assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(DataIndexFields.MODIFIERS));
        assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(DataIndexFields.CLASS_NAME));
        assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(DataIndexFields.FQN));
        assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(DataIndexFields.ENTITY_TYPE));
        assertEquals(FieldSelectorResult.LOAD, model.getValue().accept(DataIndexFields.TYPE_INFO));

        assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(DataIndexFields.SUPERCLASS));
        assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(DataIndexFields.INTERFACES));
        assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(DataIndexFields.INTERFACES));

    }

}
