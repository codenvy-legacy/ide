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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.ExternalizationTools;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;
import org.exoplatform.ide.codeassistant.storage.lucene.IndexType;

import java.io.IOException;

/** Create Lucene Document for JavaDoc or TypeInfo. */
public class DataIndexer {
    /**
     * Create simple name from "Fully qualified name"
     *
     * @param fqn
     *         - Fully qualified name of the class
     * @return - name of the class without package.
     */
    public static String simpleName(String fqn) {
        return fqn.substring(fqn.lastIndexOf('.') + 1);
    }

    /**
     * Creates lucene document for member's javaDoc.
     *
     * @param fqn
     *         members fqn (class, field, constructor, method, etc)
     * @param doc
     *         member's javaDoc
     * @return created document
     * @throws IOException
     */
    public Document createJavaDocDocument(String fqn, String javaDoc, String artifact) throws IOException {
        Document javaDocDocument = new Document();

        javaDocDocument.add(new Field(IndexType.DOC.getIndexFieldName(), IndexType.DOC.getIndexFieldValue(), Store.YES,
                                      Index.NOT_ANALYZED));
        javaDocDocument.add(new Field(DataIndexFields.FQN, fqn, Store.YES, Index.NOT_ANALYZED));
        javaDocDocument.add(new Field(DataIndexFields.ARTIFACT, artifact, Store.YES, Index.NOT_ANALYZED));
        javaDocDocument.add(new Field(DataIndexFields.JAVA_DOC, javaDoc, Store.YES, Index.NOT_ANALYZED_NO_NORMS));
        return javaDocDocument;
    }

    /**
     * Create lucene document from typeInfo;
     *
     * @param typeInfo
     * @return
     * @throws IOException
     */
    public Document createTypeInfoDocument(TypeInfo typeInfo, String artifact) throws IOException {
        Document typeInfoDocument = new Document();

        typeInfoDocument.add(new Field(IndexType.JAVA.getIndexFieldName(), IndexType.JAVA.getIndexFieldValue(),
                                       Store.YES, Index.NOT_ANALYZED));

        String fqn = typeInfo.getName();

        typeInfoDocument.add(new Field(DataIndexFields.CLASS_NAME, simpleName(fqn), Store.YES, Index.NOT_ANALYZED));

        typeInfoDocument.add(new Field(DataIndexFields.ARTIFACT, artifact, Store.YES, Index.NOT_ANALYZED));

        typeInfoDocument.add(new Field(DataIndexFields.MODIFIERS, Integer.toString(typeInfo.getModifiers()), Store.YES,
                                       Index.NOT_ANALYZED));

        typeInfoDocument.add(new Field(DataIndexFields.FQN, fqn, Store.YES, Index.NOT_ANALYZED));
        typeInfoDocument.add(new Field(DataIndexFields.ENTITY_TYPE, typeInfo.getType(), Store.YES, Index.NOT_ANALYZED));
        typeInfoDocument.add(new Field(DataIndexFields.SUPERCLASS, typeInfo.getSuperClass(), Store.YES,
                                       Index.NOT_ANALYZED));
        typeInfoDocument
                .add(new Field(DataIndexFields.SIGNATURE, typeInfo.getSignature(), Store.YES, Index.NOT_ANALYZED));

        for (String string : typeInfo.getInterfaces()) {
            typeInfoDocument.add(new Field(DataIndexFields.INTERFACES, string, Store.YES, Index.NOT_ANALYZED));
        }

        typeInfoDocument.add(new Field(DataIndexFields.TYPE_INFO, ExternalizationTools.externalize(typeInfo), Store.YES));
        return typeInfoDocument;

    }

    /**
     * Create lucene document for package
     *
     * @param pack
     *         package name
     * @return
     */
    public Document createPackageDocument(String pack, String artifact) {
        Document packageDocument = new Document();
        packageDocument.add(new Field(IndexType.PACKAGE.getIndexFieldName(), IndexType.PACKAGE.getIndexFieldValue(),
                                      Store.YES, Index.NOT_ANALYZED));
        packageDocument.add(new Field(DataIndexFields.PACKAGE, pack, Store.YES, Index.NOT_ANALYZED));
        packageDocument.add(new Field(DataIndexFields.ARTIFACT, artifact, Store.YES, Index.NOT_ANALYZED));
        return packageDocument;
    }

}
