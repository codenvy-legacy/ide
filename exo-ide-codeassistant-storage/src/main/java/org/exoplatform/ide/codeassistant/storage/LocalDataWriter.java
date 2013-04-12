/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
