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

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.storage.api.InfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class StorageBootstrap implements ServletContextListener {

    private static final Logger LOG                       = LoggerFactory.getLogger(StorageBootstrap.class);

    public static final String  STORAGE_PATH_NAME         = "storage-path";

    public static final String  SYSPROP_STORAGE_PATH_NAME = "codeassitant.storage-path";

    private LuceneInfoStorage   luceneStorageWriter;

    private String              storagePath;

    private LuceneInfoStorage   luceneStorageReader;

    /** @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent) */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Map<String, Object> options = new HashMap<String, Object>();
        ServletContext ctx = sce.getServletContext();
        storagePath = (System.getProperty(SYSPROP_STORAGE_PATH_NAME) != null) ?
                       System.getProperty(SYSPROP_STORAGE_PATH_NAME) :
                       ctx.getInitParameter(STORAGE_PATH_NAME);

        if (storagePath == null)
            storagePath = System.getProperty("java.io.tmpdir") + "/" + "ide-codeassistant-lucene-index";

        options.put(UpdateStorageService.UPDATE_TIMEOUT,
                    getNumber(ctx.getInitParameter(UpdateStorageService.UPDATE_TIMEOUT)));

        options.put(UpdateStorageService.UPDATE_WORKERS_NUMBER,
                    getNumber(ctx.getInitParameter(UpdateStorageService.UPDATE_WORKERS_NUMBER)));

        options.put(UpdateStorageService.UPDATE_QUEUE_SIZE,
                    getNumber(ctx.getInitParameter(UpdateStorageService.UPDATE_QUEUE_SIZE)));
        try {
            luceneStorageWriter = new LuceneInfoStorage(storagePath);
            luceneStorageReader = new LuceneInfoStorage(storagePath);

            InfoStorage infoStorage = new LocalInfoStorage(luceneStorageWriter);

            options.put(UpdateStorageService.INFO_STORAGE, infoStorage);

        } catch (IOException e) {
            LOG.error("Can't find path to lucene index", e);
        }

        UpdateStorageService updateService = new UpdateStorageService(options);
        CodeAssistantStorage storage = new StorageService(luceneStorageReader);
        ctx.setAttribute(UpdateStorageService.class.getName(), updateService);
        ctx.setAttribute(CodeAssistantStorage.class.getName(), storage);

    }

    private Integer getNumber(String value) {
        if (value != null) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    /** @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent) */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        UpdateStorageService updateStorageService =
                                                    (UpdateStorageService)sce.getServletContext()
                                                                             .getAttribute(UpdateStorageService.class.getName());
        if (updateStorageService != null)
            updateStorageService.shutdown();
        if (luceneStorageWriter != null)
            luceneStorageWriter.closeIndexes();
        if (luceneStorageReader != null)
            luceneStorageReader.closeIndexes();
    }

}
