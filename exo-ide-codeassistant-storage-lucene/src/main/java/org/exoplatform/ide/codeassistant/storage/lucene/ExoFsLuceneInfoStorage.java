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

import org.exoplatform.container.configuration.ConfigurationException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/** FsLuceneInfoStorage to be able to configure over eXo configuration */
public class ExoFsLuceneInfoStorage extends LuceneInfoStorage implements Startable {

    private static final Logger LOG = LoggerFactory.getLogger(ExoFsLuceneInfoStorage.class);

    public static final String STORAGE_PATH_NAME = "storage-path";

    /**
     * Extract configuration parameter from InitParams
     *
     * @param initParams
     * @return
     * @throws ConfigurationException
     */
    private static String extractStoragePath(InitParams initParams) throws ConfigurationException {
        ValueParam storagePathParamValue = initParams.getValueParam(STORAGE_PATH_NAME);
        if (storagePathParamValue == null) {
            LOG.error("Configuration parameter {} not found", STORAGE_PATH_NAME);
            throw new ConfigurationException("Configuration parameter " + STORAGE_PATH_NAME + " not found");
        }
        return storagePathParamValue.getValue();
    }

    public ExoFsLuceneInfoStorage(InitParams params) throws IOException, ConfigurationException {
        super(extractStoragePath(params));
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        this.closeIndexes();
    }

}
