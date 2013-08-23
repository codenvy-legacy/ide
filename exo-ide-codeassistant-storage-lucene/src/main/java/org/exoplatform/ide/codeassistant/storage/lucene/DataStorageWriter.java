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

import org.exoplatform.ide.codeassistant.asm.JarParser;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.PackageParser;
import org.exoplatform.ide.codeassistant.storage.QDoxJavaDocExtractor;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneDataWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/** Class for storing classes info and java docs info to lucene storage */
public class DataStorageWriter {

    private final String pathToIndex;

    public DataStorageWriter(String pathToIndex) {
        this.pathToIndex = pathToIndex;
    }

    /**
     * Method add all classes from jars to lucene index
     *
     * @param jars
     * @throws IOException
     * @throws SaveDataIndexException
     */
    public void writeBinaryJarsToIndex(Artifact[] artifacts) throws IOException, SaveDataIndexException {
        writeBinaryJarsToIndex(artifacts, Collections.<String>emptySet());
    }

    /**
     * Method adds all java doc comments from source archives to lucene index
     *
     * @param sourceJars
     * @throws IOException
     * @throws SaveDataIndexException
     */
    public void writeSourceJarsToIndex(Artifact[] artifacts) throws IOException, SaveDataIndexException {
        writeSourceJarsToIndex(artifacts, Collections.<String>emptySet());
    }

    /**
     * Method add all classes from jars to lucene index
     *
     * @param jarFilesList
     * @param ignoredPackages
     */
    public void writeBinaryJarsToIndex(Artifact[] artifacts, Set<String> ignoredPackages) throws IOException, SaveDataIndexException {
        LuceneInfoStorage luceneInfoStorage = null;
        if (artifacts == null)
            return;
        try {
            luceneInfoStorage = new LuceneInfoStorage(pathToIndex);
            LuceneDataWriter writer = new LuceneDataWriter(luceneInfoStorage);

            for (Artifact artifact : artifacts) {
                Set<String> packages = new TreeSet<String>();
                File jarFile = new File(artifact.getPath());
                List<TypeInfo> typeInfos = JarParser.parse(jarFile, ignoredPackages);
                packages.addAll(PackageParser.parse(jarFile, ignoredPackages));
                writer.addTypeInfo(typeInfos, artifact.getArtifactString());
                writer.addPackages(packages, artifact.getArtifactString());
            }
        } finally {
            if (luceneInfoStorage != null) {
                luceneInfoStorage.closeIndexes();
            }
        }
    }

    /**
     * Method adds all java doc comments from source archives to lucene index
     *
     * @param jarFilesList
     * @param ignoredPackages
     */
    public void writeSourceJarsToIndex(Artifact[] artifacts, Set<String> ignoredPackages) throws IOException, SaveDataIndexException {
        LuceneInfoStorage luceneInfoStorage = null;
        if (artifacts == null)
            return;
        try {
            luceneInfoStorage = new LuceneInfoStorage(pathToIndex);
            LuceneDataWriter writer = new LuceneDataWriter(luceneInfoStorage);

            QDoxJavaDocExtractor javaDocExtractor = new QDoxJavaDocExtractor();
            for (Artifact artifact : artifacts) {
                File jarFile = new File(artifact.getPath());
                InputStream zipStream = new FileInputStream(jarFile);
                try {
                    Map<String, String> javaDocs = javaDocExtractor.extractZip(zipStream, ignoredPackages);
                    writer.addJavaDocs(javaDocs, artifact.getArtifactString());
                } finally {
                    zipStream.close();
                }
            }
        } finally {
            if (luceneInfoStorage != null) {
                luceneInfoStorage.closeIndexes();
            }
        }
    }
}
