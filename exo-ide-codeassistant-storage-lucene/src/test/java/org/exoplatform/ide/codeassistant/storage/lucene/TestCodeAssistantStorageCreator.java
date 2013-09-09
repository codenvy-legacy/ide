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

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 *
 */
@Ignore
public class TestCodeAssistantStorageCreator {
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static final String PATH_TO_RT_JAR = System.getProperty("java.home") + FILE_SEPARATOR + "lib"
                                                 + FILE_SEPARATOR + "rt.jar";

    @Test
    public void testClassStorageCreation() throws IOException, CodeAssistantException, SaveDataIndexException {
        List<String> jars = new ArrayList<String>();
        jars.add(PATH_TO_RT_JAR);
        String pathToIndex = "target/index";

        DataStorageWriter storageWriter = new DataStorageWriter(pathToIndex);
//      storageWriter.writeBinaryJarsToIndex(jars);

        LuceneInfoStorage luceneInfoStorage = new LuceneInfoStorage(pathToIndex);
        LuceneCodeAssistantStorage storage = new LuceneCodeAssistantStorage(luceneInfoStorage);

        assertTrue(storage.getClasses("").size() > 0);
    }
}
