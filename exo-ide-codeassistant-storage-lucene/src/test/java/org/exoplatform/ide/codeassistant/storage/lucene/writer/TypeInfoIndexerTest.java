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

import test.annotations.Bar;
import test.classes.CTestClass;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.IndexReader;
import org.exoplatform.ide.codeassistant.asm.ClassParser;
import org.exoplatform.ide.codeassistant.jvm.bean.TypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.shared.*;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneCodeAssistantStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.search.ShortTypeInfoExtractor;
import org.exoplatform.ide.codeassistant.storage.lucene.search.TypeInfoExtractor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TypeInfoIndexerTest {
    private final DataIndexer indexer = new DataIndexer();

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private TypeInfoBean typeInfo;

    @Mock
    private IndexReader reader;

    @Mock
    private LuceneCodeAssistantStorage luceneCodeAssistantStorage;

    @Test
    public void shouldCallPredefinedSetOfFields() throws Exception {
        indexer.createTypeInfoDocument(typeInfo, "rt");
        // one for fields + one for externalization
        verify(typeInfo, times(2)).getName();
        verify(typeInfo, times(2)).getModifiers();
        verify(typeInfo, times(2)).getType();
        verify(typeInfo, times(2)).getInterfaces();
        verify(typeInfo, times(2)).getSuperClass();

        //only externalization
        verify(typeInfo).getFields();
        verify(typeInfo).getMethods();
        verify(typeInfo, times(2)).getSignature();
        verify(typeInfo).getNestedTypes();
        verifyNoMoreInteractions(typeInfo);
    }

    @Test
    public void shouldBeAbleToRestoreShortTypeInfo() throws Exception {
        TypeInfo expected = ClassParser.parse(ClassParser.getClassFile(CTestClass.class));
        Document document = indexer.createTypeInfoDocument(expected, "rt");
        when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(document);

        ShortTypeInfo actual = new ShortTypeInfoExtractor().getValue(reader, 5);

        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getModifiers(), actual.getModifiers());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    public void shouldBeAbleToRestoreTypeInfo() throws Exception {
        TypeInfo expected = ClassParser.parse(ClassParser.getClassFile(CTestClass.class));
        Document document = indexer.createTypeInfoDocument(expected, "rt");
        when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(document);

        TypeInfo actual = new TypeInfoExtractor().getValue(reader, 5);

        assertTypeInfoEquals(expected, actual);
    }

    @Test
    public void shouldBeAbleToRestoreTypeInfoWithAnnotationDefaultValues() throws Exception {
        TypeInfo expected = ClassParser.parse(ClassParser.getClassFile(Bar.class));
        Document document = indexer.createTypeInfoDocument(expected, "rt");
        when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(document);

        TypeInfo actual = new TypeInfoExtractor().getValue(reader, 5);

        assertTypeInfoEquals(expected, actual);
    }

    public static void assertFieldsEqual(List<FieldInfo> expected, List<FieldInfo> actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            //member
            assertEquals(expected.get(i).getName(), actual.get(i).getName());
            assertEquals(expected.get(i).getModifiers(), actual.get(i).getModifiers());
            //fieldInfo
            assertEquals(expected.get(i).getDeclaringClass(), actual.get(i).getDeclaringClass());
            assertEquals(expected.get(i).getType(), actual.get(i).getType());

        }
    }

    public static void assertMethodsEqual(List<MethodInfo> expected, List<MethodInfo> actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            //member
            assertEquals(expected.get(i).getName(), actual.get(i).getName());
            assertEquals(expected.get(i).getModifiers(), actual.get(i).getModifiers());
            //methodInfo
            assertEquals(expected.get(i).getDeclaringClass(), actual.get(i).getDeclaringClass());
            assertEquals(expected.get(i).isConstructor(), actual.get(i).isConstructor());
            assertEquals(expected.get(i).getReturnType(), actual.get(i).getReturnType());
            assertArrayEquals(expected.get(i).getExceptionTypes().toArray(), actual.get(i).getExceptionTypes().toArray());
            assertArrayEquals(expected.get(i).getParameterNames().toArray(), actual.get(i).getParameterNames().toArray());
            assertArrayEquals(expected.get(i).getParameterTypes().toArray(), actual.get(i).getParameterTypes().toArray());
            assertAnnotationDefaultEquals(expected.get(i).getAnnotationDefault(), actual.get(i).getAnnotationDefault());
        }
    }

    /**
     * @param expected
     * @param actual
     */
    private static void assertAnnotationDefaultEquals(AnnotationValue expected, AnnotationValue actual) {
        assertEquals(actual == null, expected == null);
        if (actual == null)
            return;
        assertArrayEquals(expected.getPrimitiveType(), actual.getPrimitiveType());
        assertArrayEquals(expected.getArrayType(), actual.getArrayType());
        assertArrayEquals(expected.getEnumConstant(), actual.getEnumConstant());
        assertAnnotationEquals(expected.getAnnotation(), actual.getAnnotation());
    }

    /**
     * @param expected
     * @param actual
     */
    private static void assertAnnotationEquals(Annotation expected, Annotation actual) {
        assertEquals(actual == null, expected == null);
        if (actual == null)
            return;
        assertEquals(expected.getTypeName(), actual.getTypeName());
    }

    public static void assertTypeInfoEquals(TypeInfo expected, TypeInfo actual) {
        //Member
        assertEquals(expected.getModifiers(), actual.getModifiers());
        assertEquals(expected.getName(), actual.getName());
        //Short type info
        assertEquals(expected.getType(), actual.getType());
        //TypeInfo
        assertEquals(expected.getSuperClass(), actual.getSuperClass());
        assertFieldsEqual(expected.getFields(), actual.getFields());
        assertMethodsEqual(expected.getMethods(), actual.getMethods());

    }
}
