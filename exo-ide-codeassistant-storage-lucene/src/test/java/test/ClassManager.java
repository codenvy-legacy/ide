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
package test;

import test.annotations.CTestAnnotation;
import test.annotations.DTestAnnotation;
import test.classes.*;
import test.classes2.ITestClass;
import test.interfaces.DTestInterface;
import test.interfaces.ETestInterface;
import test.interfaces.ETestInterface2;

import org.exoplatform.ide.codeassistant.asm.ClassParser;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.QDoxJavaDocExtractor;
import org.exoplatform.ide.codeassistant.storage.lucene.SaveDataIndexException;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneDataWriter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Test classes enumerator */
public class ClassManager {
    //disable instance creation
    private ClassManager() {
    }

    /** @return array of all test classes. */
    public static Class<?>[] getAllTestClasses() {
        return new Class[]{CTestAnnotation.class, DTestAnnotation.class, ATestClass.class, ATestClass2.class,
                           BTestClass.class, ITestClass.class, DTestInterface.class, ETestInterface.class, ETestInterface2.class,
                           CTestClass.class, DTestClass.class};
    }

    /**
     * @param className
     *         TODO
     * @throws IOException
     * @throws SaveDataIndexException
     */
    public static void createIndexForClass(LuceneDataWriter typeWriter, Class<?>... classesToIndex) throws IOException,
                                                                                                           SaveDataIndexException {

        List<TypeInfo> typeInfos = new ArrayList<TypeInfo>();

        for (Class<?> classToIndex : classesToIndex) {
            typeInfos.add(ClassParser.parse(ClassParser.getClassFile(classToIndex)));
        }

        typeWriter.addTypeInfo(typeInfos, "rt");
    }

    public static void createIndexForSources(LuceneDataWriter dataWriter, String... sources) throws IOException,
                                                                                                    SaveDataIndexException {
        Map<String, String> javaDocs = new HashMap<String, String>();
        for (String source : sources) {
            QDoxJavaDocExtractor javaDocExtractor = new QDoxJavaDocExtractor();
            javaDocs.putAll(javaDocExtractor.extractSource(new FileInputStream(source)));
        }

        dataWriter.addJavaDocs(javaDocs, "rt");
    }

}
