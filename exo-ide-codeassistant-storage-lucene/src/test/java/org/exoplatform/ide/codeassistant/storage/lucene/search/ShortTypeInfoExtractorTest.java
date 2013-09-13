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

import test.classes.CTestClass;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.index.IndexReader;
import org.exoplatform.ide.codeassistant.asm.ClassParser;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.DataIndexer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
public class ShortTypeInfoExtractorTest {
    @Mock
    private IndexReader reader;

    private final ShortTypeInfoExtractor extractor = new ShortTypeInfoExtractor();

    @Test
    public void shouldCallReaderGetDocumentWithSameIdAndFieldSelector() throws Exception {

        Document luceneDocument = new Document();
        //add minimal set of field to be able to call method  extractor.getValue
        luceneDocument.add(new Field(DataIndexFields.MODIFIERS, "1", Store.YES, Index.NO));
        when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(luceneDocument);

        extractor.getValue(reader, 5);

        verify(reader).document(eq(5), (FieldSelector)anyObject());
        verifyNoMoreInteractions(reader);
    }

    @org.junit.Ignore
    @Test
    public void shouldReconstructShortTypeInfo() throws Exception {
        TypeInfo expected = ClassParser.parse(ClassParser.getClassFile(CTestClass.class));
        Document luceneDocument = new DataIndexer().createTypeInfoDocument(expected, "rt");

        when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(luceneDocument);

        ShortTypeInfo actual = extractor.getValue(reader, 5);

        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getModifiers(), actual.getModifiers());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    public void shouldGetDocumentFromReaderWithPredefinedSetOfFields() throws Exception {
        Document luceneDocument = new Document();
        //add minimal set of field to be able to call method  extractor.getValue
        luceneDocument.add(new Field(DataIndexFields.MODIFIERS, "1", Store.YES, Index.NO));
        when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(luceneDocument);

        extractor.getValue(reader, 5);

        ArgumentCaptor<FieldSelector> model = ArgumentCaptor.forClass(FieldSelector.class);
        verify(reader).document(eq(5), model.capture());

        assertEquals(FieldSelectorResult.LOAD, model.getValue().accept(DataIndexFields.MODIFIERS));
        assertEquals(FieldSelectorResult.LOAD, model.getValue().accept(DataIndexFields.FQN));
        assertEquals(FieldSelectorResult.LOAD, model.getValue().accept(DataIndexFields.ENTITY_TYPE));
        assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(DataIndexFields.SUPERCLASS));
        assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(DataIndexFields.INTERFACES));
        assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(DataIndexFields.INTERFACES));

    }

}
