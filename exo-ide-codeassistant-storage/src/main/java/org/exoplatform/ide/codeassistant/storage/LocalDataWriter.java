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
package org.exoplatform.ide.codeassistant.storage;

import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.api.DataWriter;
import org.exoplatform.ide.codeassistant.storage.lucene.SaveDataIndexException;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneDataWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link DataWriter}, uses {@link LuceneDataWriter}
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */

public class LocalDataWriter implements DataWriter {

    private static final Logger LOG = LoggerFactory.getLogger(LocalDataWriter.class);

    private LuceneDataWriter dataWriter;

    /** @param dataWriter */
    public LocalDataWriter(LuceneDataWriter dataWriter) {
        this.dataWriter = dataWriter;
    }

    /** @see org.exoplatform.ide.codeassistant.storage.api.DataWriter#addTypeInfo(java.util.List, java.lang.String) */
    @Override
    public void addTypeInfo(List<TypeInfo> typeInfos, String artifact) {
        try {
            dataWriter.addTypeInfo(typeInfos, artifact);
        } catch (SaveDataIndexException e) {
            LOG.error("Can't save type info for artifact:" + artifact, e);
        }
    }

    /** @see org.exoplatform.ide.codeassistant.storage.api.DataWriter#addPackages(java.util.Set, java.lang.String) */
    @Override
    public void addPackages(Set<String> packages, String artifact) {
        try {
            dataWriter.addPackages(packages, artifact);
        } catch (SaveDataIndexException e) {
            LOG.error("Can't save packages for artifact:" + artifact, e);
        }
    }

    /** @see org.exoplatform.ide.codeassistant.storage.api.DataWriter#addJavaDocs(java.util.Map, java.lang.String) */
    @Override
    public void addJavaDocs(Map<String, String> javaDocs, String artifact) {
        try {
            dataWriter.addJavaDocs(javaDocs, artifact);
        } catch (SaveDataIndexException e) {
            LOG.error("Can't save javadock for artifact:" + artifact, e);
        }
    }

    @Override
    public void removeTypeInfo(String artifact) {
        try {
            dataWriter.removeTypeInfo(artifact);
        } catch (SaveDataIndexException e) {
            LOG.error("Can't save type info for artifact:" + artifact, e);
        }

    }

    @Override
    public void removePackages(String artifact) {
        try {
            dataWriter.removePackages(artifact);
        } catch (SaveDataIndexException e) {
            LOG.error("Can't save packages for artifact:" + artifact, e);
        }

    }

    @Override
    public void removeJavaDocs(String artifact) {
        try {
            dataWriter.removeJavaDocs(artifact);
        } catch (SaveDataIndexException e) {
            LOG.error("Can't save javadock for artifact:" + artifact, e);
        }

    }

}
