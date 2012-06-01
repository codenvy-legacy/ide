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

import org.exoplatform.ide.codeassistant.storage.api.InfoStorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class UpdateStorageService
{

   public static final String INFO_STORAGE = "update.info.storage";

   /**
    * Name of configuration parameter that provides build timeout is seconds. After this time build may be terminated.
    *
    * @see #DEFAULT_UPDATE_TIMEOUT
    */
   public static final String UPDATE_TIMEOUT = "update.timeout";

   /**
    * Name of configuration parameter that sets the number of update workers. In other words it set the number of build
    * process that can be run at the same time. If this parameter is not set then the number of available processors
    * used, e.g. <code>Runtime.getRuntime().availableProcessors();</code>
    */
   public static final String UPDATE_WORKERS_NUMBER = "update.workers.number";

   /**
    * Name of parameter that set the max size of build queue. The number of build task in queue may not be greater than
    * provided by this parameter.
    *
    * @see #DEFAULT_UPDATE_QUEUE_SIZE
    */
   public static final String UPDATE_QUEUE_SIZE = "update.queue.size";

   /**
    * Name of configuration parameter that points to the directory where all jars stored.
    * Is such parameter is not specified then 'java.io.tmpdir' used.
    */
   public static final String UPDATE_FOLDER = "update.folder";

   /** Default build timeout in seconds (120). After this time build may be terminated. */
   public static final int DEFAULT_UPDATE_TIMEOUT = 180;

   /** Default max size of build queue (200). */
   public static final int DEFAULT_UPDATE_QUEUE_SIZE = 200;

   private final ExecutorService pool;

   private final File tempFolder;

   private final int timeoutMillis;

   private final InfoStorage infoStorage;

   private static <O> O getOption(Map<String, Object> config, String option, Class<O> type, O defaultValue)
   {
      if (config != null)
      {
         Object value = config.get(option);
         return value != null ? type.cast(value) : defaultValue;
      }
      return defaultValue;
   }

   /**
    * 
    */
   public UpdateStorageService(Map<String, Object> options)
   {
      this(//
         getOption(options, INFO_STORAGE, InfoStorage.class, null),//
         getOption(options, UPDATE_FOLDER, String.class, System.getProperty("java.io.tmpdir")), //
         getOption(options, UPDATE_TIMEOUT, Integer.class, DEFAULT_UPDATE_TIMEOUT),//
         getOption(options, UPDATE_WORKERS_NUMBER, Integer.class, Runtime.getRuntime().availableProcessors()),//
         getOption(options, UPDATE_QUEUE_SIZE, Integer.class, DEFAULT_UPDATE_QUEUE_SIZE)//
      );
   }

   /**
    * 
    */
   public UpdateStorageService(InfoStorage infoStorage, String tempFolder, int timeout, int workerNumber,
      int updateQueueSize)
   {
      this.infoStorage = infoStorage;
      this.tempFolder = new File(tempFolder);
      this.timeoutMillis = timeout * 1000; // to milliseconds
      //
      this.pool =
         new ThreadPoolExecutor(workerNumber, workerNumber, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(updateQueueSize), new ThreadPoolExecutor.AbortPolicy());
   }

   public void updateTypeIndex(List<Dependency> dependencies, InputStream in) throws IOException
   {
      addTask(new TypeUpdateInvoker(infoStorage, dependencies, createDependencys(in)));
   }
   
   public void updateDockIndex(List<Dependency> dependencies, InputStream in) throws IOException
   {
      addTask(new DockUpdateInvoker(infoStorage, dependencies, createDependencys(in)));
   }

   public void shutdown()
   {
      pool.shutdown();
      try
      {
         if (!pool.awaitTermination(30, TimeUnit.SECONDS))
         {
            pool.shutdownNow();
         }
      }
      catch (InterruptedException e)
      {
         pool.shutdownNow();
         Thread.currentThread().interrupt();
      }
   }

   /**
    * @param createDependencys
    * @param dependencies
    */
   private void addTask(final UpdateInvoker invoker)
   {
      pool.submit(new Runnable()
      {

         @Override
         public void run()
         {
            TimeOutThread t = new TimeOutThread(timeoutMillis, Thread.currentThread());
            t.start();
            invoker.execute();
            t.kill();
         }
      });
   }

   /**
    * Timeout Thread. Kill the main task if necessary.
    * 
    * @author el
    * 
    */
   public static class TimeOutThread extends Thread
   {
      final long timeout;

      final Thread controlledObj;

      TimeOutThread(long timeout, Thread controlledObj)
      {
         setDaemon(true);
         this.timeout = timeout;
         this.controlledObj = controlledObj;
      }

      boolean isRunning = true;

      /**
       * If we done need the {@link TimeOutThread} thread, we may kill it.
       */
      public void kill()
      {
         isRunning = false;
         synchronized (this)
         {
            notify();
         }
      }

      /**
       * 
       */
      @Override
      public void run()
      {
         long deltaT = 0l;
         try
         {
            long start = System.currentTimeMillis();
            while (isRunning && deltaT < timeout)
            {
               synchronized (this)
               {
                  wait(Math.max(100, timeout - deltaT));
               }
               deltaT = System.currentTimeMillis() - start;
            }
         }
         catch (InterruptedException e)
         {
            // If the thread is interrupted,
            // you may not want to kill the main thread,
            // but probably yes.
         }
         finally
         {
            isRunning = false;
         }
         controlledObj.interrupt();
      }
   }

   /**
    * @param in
    * @return
    * @throws IOException 
    */
   private File createDependencys(InputStream in) throws IOException
   {
      File depFolder = UpdateUtil.makeProjectDirectory(tempFolder);
      UpdateUtil.unzip(in, depFolder);
      return depFolder;
   }

}
