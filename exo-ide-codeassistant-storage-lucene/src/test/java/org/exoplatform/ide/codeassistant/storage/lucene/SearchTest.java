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
package org.exoplatform.ide.codeassistant.storage.lucene;

import test.ClassManager;

import org.apache.lucene.store.RAMDirectory;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.DataIndexer;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneDataWriter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Test Searching in Lucene TypeInfo Storage */
public class SearchTest {

    private static LuceneCodeAssistantStorage storage;

    private static LuceneDataWriter writer;

    private static LuceneInfoStorage luceneInfoStorage;

    @BeforeClass
    public static void createIndex() throws Exception {
        // luceneInfoStorage = new LuceneInfoStorage(NIOFSDirectory.open(new File("/tmp/SearchTest")));
        luceneInfoStorage = new LuceneInfoStorage(new RAMDirectory());
        writer = new LuceneDataWriter(luceneInfoStorage);
        storage = new LuceneCodeAssistantStorage(luceneInfoStorage);

        ClassManager.createIndexForClass(writer, ClassManager.getAllTestClasses());
        //add parent
        ClassManager.createIndexForClass(writer, Object.class);

        // add java doc index
        ClassManager.createIndexForSources(writer, "src/test/java/test/javadoc/JavaDocClass.java");
    }

    @Test
    public void testSearchAllAnnotations() throws Exception {
        List<ShortTypeInfo> typeInfos = storage.getAnnotations("");

        assertEquals(2, typeInfos.size());
    }

    @Test
    public void testSearchAllInterfaces() throws Exception {
        List<ShortTypeInfo> typeInfos = storage.getInterfaces("");

        assertEquals(3, typeInfos.size());
    }

    @Test
    public void testSearchAnnotationsStartsWithC() throws Exception {
        List<ShortTypeInfo> typeInfos = storage.getAnnotations("C");

        assertEquals(1, typeInfos.size());
    }

    @Test
    public void testSearchByFqnPrefix() throws Exception {
        List<ShortTypeInfo> typeInfos = storage.getTypesByFqnPrefix("test.classes");

        assertEquals(6, typeInfos.size());
        for (ShortTypeInfo shortTypeInfo : typeInfos) {
            assertTrue(shortTypeInfo.getName().startsWith("test.classes"));
        }
    }

    @Test
    public void testSearchByNamePrefix() throws Exception {
        List<ShortTypeInfo> typeInfos = storage.getTypesByNamePrefix("ATest");

        assertEquals(2, typeInfos.size());

        ShortTypeInfo info1 = typeInfos.get(0);
        ShortTypeInfo info2 = typeInfos.get(1);
        assertTrue(DataIndexer.simpleName(info1.getName()).startsWith("ATest"));
        assertTrue(DataIndexer.simpleName(info2.getName()).startsWith("ATest"));
    }

    @Test
    public void testSearchClassesStartsWithA() throws Exception {
        List<ShortTypeInfo> typeInfos = storage.getClasses("A");

        assertEquals(2, typeInfos.size());

        ShortTypeInfo info1 = typeInfos.get(0);
        assertTrue(DataIndexer.simpleName(info1.getName()).startsWith("A"));
        assertEquals(info1.getType(), "CLASS");

        ShortTypeInfo info2 = typeInfos.get(1);
        assertTrue(DataIndexer.simpleName(info2.getName()).startsWith("A"));
        assertEquals(info2.getType(), "CLASS");
    }

    @Test
    public void testSearchClassesStartsWithB() throws Exception {
        List<ShortTypeInfo> typeInfos = storage.getClasses("B");

        assertEquals(1, typeInfos.size());

        ShortTypeInfo info = typeInfos.get(0);
        assertTrue(DataIndexer.simpleName(info.getName()).startsWith("B"));
    }

    @Test
    public void testSearchTypeInfoByName() throws Exception {

        TypeInfo typeInfo = storage.getTypeByFqn("test.classes.ATestClass");

        assertEquals("test.classes.ATestClass", typeInfo.getName());
        assertEquals(2, typeInfo.getFields().size());

        assertEquals(3, typeInfo.getMethods().size());

        assertEquals("java.lang.Object", typeInfo.getSuperClass());
    }


    @Test
    public void testSearchUnexistanceClasses() throws Exception {
        List<ShortTypeInfo> typeInfos = storage.getClasses("W");

        assertEquals(0, typeInfos.size());
    }

    @Test
    public void testSearchUnexistanceClasses2() throws Exception {
        List<ShortTypeInfo> typeInfos = storage.getTypesByFqnPrefix("exo");

        assertEquals(0, typeInfos.size());
    }

    @Test
    public void testSearchClassJavaDoc() throws CodeAssistantException {
        String javaDoc = storage.getClassJavaDoc("test.javadoc.ClassWithGenerics");
        assertEquals("Class with generics", javaDoc);
    }

    @Test
    public void testSearchInnerClassJavaDoc() throws CodeAssistantException {
        String javaDoc = storage.getClassJavaDoc("test.javadoc.JavaDocClass$PrivateClass");
        assertEquals("Private class with java doc", javaDoc);
    }

    @Test(expected = CodeAssistantException.class)
    public void testSearchUncommentedClassJavaDoc() throws CodeAssistantException {
        storage.getClassJavaDoc("test.javadoc.JavaDocClass$ClassWithoutJavadoc");
    }

    @Test
    public void testSearchFieldJavaDoc() throws CodeAssistantException {
        String javaDoc = storage.getClassJavaDoc("test.javadoc.JavaDocClass#field");
        assertEquals("Field java doc\n@author Test field doclets", javaDoc);
    }

    @Test
    public void testSearchMethodJavaDoc() throws CodeAssistantException {
        String javaDoc = storage.getClassJavaDoc("test.javadoc.JavaDocClass#method@(ILjava/lang/Double;)V");
        assertEquals("Method with primitive and object params", javaDoc);
    }

    @Test
    public void testSearchConstructorJavaDoc() throws CodeAssistantException {
        String javaDoc = storage.getClassJavaDoc("test.javadoc.JavaDocClass@(ILjava/lang/Integer;)V");
        assertEquals("Constructor java doc with parameters", javaDoc);
    }

    @Test
    public void testSearchClassesByNamePrefix() throws Exception {
        List<TypeInfo> typeInfos = storage.getTypesInfoByNamePrefix("A");

        assertEquals(2, typeInfos.size());
    }
}
