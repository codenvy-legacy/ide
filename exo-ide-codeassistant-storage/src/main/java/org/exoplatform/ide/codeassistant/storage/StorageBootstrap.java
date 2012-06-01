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

import org.exoplatform.ide.codeassistant.asm.JarParser;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.api.DataWriter;
import org.exoplatform.ide.codeassistant.storage.api.InfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class StorageBootstrap implements ServletContextListener
{

   private static final Logger LOG = LoggerFactory.getLogger(StorageBootstrap.class);

   public static final String STORAGE_PATH_NAME = "storage-path";

   private LuceneInfoStorage luceneStorage;

   private String storagePath;

   /**
    * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextInitialized(ServletContextEvent sce)
   {
      Map<String, Object> options = new HashMap<String, Object>();
      ServletContext ctx = sce.getServletContext();
      storagePath = ctx.getInitParameter(STORAGE_PATH_NAME);
      if (storagePath == null)
         storagePath = System.getProperty("java.io.tmpdir") + "/" + "ide-codeassistant-lucene-index";

      options.put(UpdateStorageService.UPDATE_TIMEOUT,
         getNumber(ctx.getInitParameter(UpdateStorageService.UPDATE_TIMEOUT)));

      options.put(UpdateStorageService.UPDATE_WORKERS_NUMBER,
         getNumber(ctx.getInitParameter(UpdateStorageService.UPDATE_WORKERS_NUMBER)));

      options.put(UpdateStorageService.UPDATE_QUEUE_SIZE,
         getNumber(ctx.getInitParameter(UpdateStorageService.UPDATE_QUEUE_SIZE)));
      try
      {
         luceneStorage = new LuceneInfoStorage(storagePath);
         final InfoStorage infoStorage = new LocalInfoStorage(luceneStorage);
         options.put(UpdateStorageService.INFO_STORAGE, infoStorage);
         Thread t = new Thread(new Runnable()
         {

            @Override
            public void run()
            {
               try
               {
                  LOG.info("Indexing rt.jar");
                  DataWriter writer = infoStorage.getWriter();
                  Set<String> packages = new TreeSet<String>();
                  File jarFile = new File(System.getProperty("java.home") + "/lib/rt.jar");
                  List<TypeInfo> typeInfos = JarParser.parse(jarFile);
                  packages.addAll(PackageParser.parse(jarFile));
                  writer.addTypeInfo(typeInfos, "rt");
                  writer.addPackages(packages, "rt");
                  LOG.info("rt.jar indexed");
               }
               catch (IOException e)
               {
                  LOG.error("Can't read rt.jar", e);
               }
            }
         });
         t.start();
      }
      catch (IOException e)
      {
         LOG.error("Can't find path to lucene index", e);
      }

      UpdateStorageService updateService = new UpdateStorageService(options);
      ctx.setAttribute(UpdateStorageService.class.getName(), updateService);

   }

   private Integer getNumber(String value)
   {
      if (value != null)
      {
         try
         {
            return Integer.valueOf(value);
         }
         catch (NumberFormatException ignored)
         {
         }
      }
      return null;
   }

   /**
    * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextDestroyed(ServletContextEvent sce)
   {
      UpdateStorageService updateStorageService =
         (UpdateStorageService)sce.getServletContext().getAttribute(UpdateStorageService.class.getName());
      if (updateStorageService != null)
         updateStorageService.shutdown();
      if (luceneStorage != null)
         luceneStorage.closeIndexes();
      if (storagePath != null)
         UpdateUtil.delete(new File(storagePath));
   }

}
