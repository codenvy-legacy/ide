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
import org.exoplatform.ide.codeassistant.jvm.shared.JavaType;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.exoplatform.ide.codeassistant.storage.lucene.search.AndLuceneSearchConstraint.and;
import static org.exoplatform.ide.codeassistant.storage.lucene.search.FieldPrefixSearchConstraint.prefix;
import static org.exoplatform.ide.codeassistant.storage.lucene.search.SearchByFieldConstraint.eq;
import static org.exoplatform.ide.codeassistant.storage.lucene.search.SearchByFieldConstraint.eqJavaType;

/** Implementation of CodeAssistantStorage based on Lucene */
public class LuceneCodeAssistantStorage {
    private final int DEFAULT_RESULT_LIMIT = 200;

    private static final Logger LOG = LoggerFactory.getLogger(LuceneCodeAssistantStorage.class);

    private final LuceneQueryExecutor queryExecutor;

    public LuceneCodeAssistantStorage(LuceneInfoStorage infoStorage) {

        this.queryExecutor = new LuceneQueryExecutor(infoStorage);
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getAnnotations(java.lang.String) */
    public List<ShortTypeInfo> getAnnotations(String prefix) throws CodeAssistantException {

        return queryExecutor.executeQuery(new ShortTypeInfoExtractor(), IndexType.JAVA,
                                          and(eqJavaType(JavaType.ANNOTATION), prefix(DataIndexFields.CLASS_NAME, prefix)),
                                          DEFAULT_RESULT_LIMIT, 0);

    }

    /** @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getClasses(java.lang.String) */
    public List<ShortTypeInfo> getClasses(String prefix) throws CodeAssistantException {
        return queryExecutor.executeQuery(new ShortTypeInfoExtractor(), IndexType.JAVA,
                                          and(eqJavaType(JavaType.CLASS), prefix(DataIndexFields.CLASS_NAME, prefix)), DEFAULT_RESULT_LIMIT,
                                          0);
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getClassJavaDoc(java.lang.String) */
    public String getClassJavaDoc(String fqn) throws CodeAssistantException {
        return getMemberJavaDoc(fqn);
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getInterfaces(java.lang.String) */
    public List<ShortTypeInfo> getInterfaces(String prefix) throws CodeAssistantException {
        return queryExecutor.executeQuery(new ShortTypeInfoExtractor(), IndexType.JAVA,
                                          and(eqJavaType(JavaType.INTERFACE), prefix(DataIndexFields.CLASS_NAME, prefix)),
                                          DEFAULT_RESULT_LIMIT, 0);

    }

    /** @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getMemberJavaDoc(java.lang.String) */
    public String getMemberJavaDoc(String fqn) throws CodeAssistantException {
        List<String> searchResult =
                queryExecutor.executeQuery(new JavaDocExtractor(), IndexType.DOC, eq(DataIndexFields.FQN, fqn), 1, 0);
        if (searchResult.isEmpty()) {
            throw new CodeAssistantException(404, "Not found");
        }
        return searchResult.get(0);
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypeByFqn(java.lang.String) */
    public TypeInfo getTypeByFqn(String fqn) throws CodeAssistantException {

        List<TypeInfo> searchResult =
                queryExecutor.executeQuery(new TypeInfoExtractor(), IndexType.JAVA, eq(DataIndexFields.FQN, fqn), 1, 0);
        return searchResult.size() == 1 ? searchResult.get(0) : null;

    }

    /** @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypesByFqnPrefix(java.lang.String) */
    public List<ShortTypeInfo> getTypesByFqnPrefix(String fqnPrefix) throws CodeAssistantException {
        return queryExecutor.executeQuery(new ShortTypeInfoExtractor(), IndexType.JAVA,
                                          prefix(DataIndexFields.FQN, fqnPrefix), DEFAULT_RESULT_LIMIT, 0);
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypesByFqnPrefix(java.lang.String) */
    public List<ShortTypeInfo> getTypesByNamePrefix(String namePrefix) throws CodeAssistantException {

        return queryExecutor.executeQuery(new ShortTypeInfoExtractor(), IndexType.JAVA,
                                          prefix(DataIndexFields.CLASS_NAME, namePrefix), DEFAULT_RESULT_LIMIT, 0);
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypesInfoByNamePrefix(java.lang.String) */
    public List<TypeInfo> getTypesInfoByNamePrefix(String namePrefix) throws CodeAssistantException {
        return queryExecutor.executeQuery(new TypeInfoExtractor(), IndexType.JAVA,
                                          prefix(DataIndexFields.CLASS_NAME, namePrefix), DEFAULT_RESULT_LIMIT, 0);
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getPackages(java.lang.String) */
    public List<String> getPackages(String packagePrefix) throws CodeAssistantException {
        return queryExecutor.executeQuery(new PackageExtractor(), IndexType.PACKAGE,
                                          prefix(DataIndexFields.PACKAGE, packagePrefix), DEFAULT_RESULT_LIMIT, 0);
    }

}
