/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.maven;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.exoplatform.ide.maven.BuildHelper.delete;
import static org.exoplatform.ide.maven.BuildHelper.makeBuilderFilesFilter;

/**
 * Build manager.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BuildService.java 16504 2011-02-16 09:27:51Z andrew00x $
 */
public class BuildService
{
   /**
    * Name of configuration parameter that points to the directory where all builds stored.
    * Is such parameter is not specified then 'java.io.tmpdir' used.
    */
   public static final String BUILDER_REPOSITORY = "builder.repository";

   /**
    * Name of configuration parameter that provides maven build goals.
    *
    * @see #DEFAULT_BUILDER_MAVEN_GOALS
    */
   public static final String BUILDER_MAVEN_GOALS = "builder.maven.goals";

   /**
    * Name of configuration parameter that provides build timeout is seconds. After this time build may be terminated.
    *
    * @see #DEFAULT_BUILDER_TIMEOUT
    */
   public static final String BUILDER_TIMEOUT = "builder.timeout";

   /**
    * Name of configuration parameter that sets the number of build workers. In other words it set the number of build
    * process that can be run at the same time. If this parameter is not set then the number of available processors
    * used, e.g. <code>Runtime.getRuntime().availableProcessors();</code>
    */
   public static final String BUILDER_WORKERS_NUMBER = "builder.workers.number";

   /**
    * Name of configuration parameter that sets time of keeping the results (artifact and logs) of build. After this
    * time the results of build may be removed.
    *
    * @see #DEFAULT_BUILDER_CLEAN_RESULT_DELAY_TIME
    */
   public static final String BUILDER_CLEAN_RESULT_DELAY_TIME = "builder.clean.result.delay.time";

   /**
    * Name of parameter that set the max size of build queue. The number of build task in queue may not be greater than
    * provided by this parameter.
    *
    * @see #DEFAULT_BUILDER_QUEUE_SIZE
    */
   public static final String BUILDER_QUEUE_SIZE = "builder.queue.size";

   /** Default build timeout in seconds (120). After this time build may be terminated. */
   public static final int DEFAULT_BUILDER_TIMEOUT = 120;

   /** Default max size of build queue (100). */
   public static final int DEFAULT_BUILDER_QUEUE_SIZE = 100;

   /**
    * Default time of keeping the results of build in minutes (60). After this time the results of build (artifact and
    * logs) may be removed.
    */
   public static final int DEFAULT_BUILDER_CLEAN_RESULT_DELAY_TIME = 60;

   /** Default maven build goals 'test package'. */
   public static final String[] DEFAULT_BUILDER_MAVEN_GOALS = new String[]{"test", "package"};

   /** Build task ID generator. */
   private static final AtomicLong idGenerator = new AtomicLong(1);

   private static String nextTaskID()
   {
      return Long.toString(idGenerator.getAndIncrement());
   }

   //
   private final ExecutorService pool;

   private final ConcurrentMap<String, CacheElement> map;
   private final Queue<CacheElement> queue;

   private final ScheduledExecutorService cleaner;
   private final Queue<File> cleanerQueue;

   private final File repository;
   private final String[] goals;
   private final long timeoutMillis;
   private final long cleanBuildResultDelayMillis;

   public BuildService(Map<String, Object> config)
   {
      this(
         (String)getOption(config, BUILDER_REPOSITORY, System.getProperty("java.io.tmpdir")),
         (String[])getOption(config, BUILDER_MAVEN_GOALS, DEFAULT_BUILDER_MAVEN_GOALS),
         (Integer)getOption(config, BUILDER_TIMEOUT, DEFAULT_BUILDER_TIMEOUT),
         (Integer)getOption(config, BUILDER_WORKERS_NUMBER, Runtime.getRuntime().availableProcessors()),
         (Integer)getOption(config, BUILDER_QUEUE_SIZE, DEFAULT_BUILDER_QUEUE_SIZE),
         (Integer)getOption(config, BUILDER_CLEAN_RESULT_DELAY_TIME, DEFAULT_BUILDER_CLEAN_RESULT_DELAY_TIME)
      );
   }

   /**
    * @param repository the repository for build
    * @param goals the maven build goals
    * @param timeout the build timeout in seconds
    * @param workerNumber the number of build workers
    * @param buildQueueSize the max size of build queue. If this number reached then all new build request rejected
    * @param cleanBuildResultDelay the time of keeping the results of build in minutes. After this time result of build
    * (both artifact and logs) may be removed.
    */
   protected BuildService(
      String repository,
      String[] goals,
      int timeout,
      int workerNumber,
      int buildQueueSize,
      int cleanBuildResultDelay)
   {
      if (repository == null || repository.isEmpty())
      {
         throw new IllegalArgumentException("Build repository may not be null or empty string. ");
      }
      if (goals == null || goals.length == 0)
      {
         throw new IllegalArgumentException("Maven build goals may not be null or empty. ");
      }
      if (workerNumber <= 0)
      {
         throw new IllegalArgumentException("Number of build workers may not be equals or less than 0. ");
      }
      if (buildQueueSize <= 0)
      {
         throw new IllegalArgumentException("Size of build queue may not be equals or less than 0. ");
      }
      if (cleanBuildResultDelay <= 0)
      {
         throw new IllegalArgumentException("Delay time of cleaning build results may not be equals or less than 0. ");
      }

      this.repository = new File(repository);
      this.goals = goals;
      this.timeoutMillis = timeout * 1000; // to milliseconds
      this.cleanBuildResultDelayMillis = cleanBuildResultDelay * 60 * 1000; // to milliseconds

      //
      this.map = new ConcurrentHashMap<String, CacheElement>();
      this.queue = new ConcurrentLinkedQueue<CacheElement>();

      //
      this.cleaner = Executors.newSingleThreadScheduledExecutor();
      this.cleanerQueue = new ConcurrentLinkedQueue<File>();
      cleaner.scheduleAtFixedRate(new CleanTask(), cleanBuildResultDelay, cleanBuildResultDelay, TimeUnit.MINUTES);

      //
      this.pool = new ThreadPoolExecutor(
         workerNumber,
         workerNumber,
         0L,
         TimeUnit.MILLISECONDS,
         new LinkedBlockingQueue<Runnable>(buildQueueSize),
         new ManyBuildTasksPolicy(new ThreadPoolExecutor.AbortPolicy()));
   }

   private static Object getOption(Map<String, Object> config, String option, Object defaultValue)
   {
      if (config != null)
      {
         Object value = config.get(option);
         return value != null ? value : defaultValue;
      }
      return defaultValue;
   }

   /**
    * Start new build.
    *
    * @param gitURI the GIT location of source code for build
    * @return build task
    */
   public MavenBuildTask add(final String gitURI)
   {
      if (gitURI == null || gitURI.isEmpty())
      {
         throw new IllegalArgumentException("Parameter 'gituri' may not be null or empty. ");
      }

      List<String> theGoals = new ArrayList<String>(goals.length);
      Collections.addAll(theGoals, goals);

      final File projectDirectory = BuildHelper.makeProjectDirectory(repository);

      final MavenInvoker invoker = new MavenInvoker()
         .addPreBuildTask(
            new Runnable()
            {
               public void run()
               {
                  Git.cloneRepository()
                     .setDirectory(projectDirectory)
                     .setURI(gitURI)
                     .call();
               }
            }
         ).setTimeout(timeoutMillis);

      File logFile = new File(projectDirectory.getParentFile(), projectDirectory.getName() + ".log");
      TaskLogger taskLogger = new TaskLogger(logFile/*, new SystemOutHandler()*/);

      final InvocationRequest request = new DefaultInvocationRequest()
         .setBaseDirectory(projectDirectory)
         .setGoals(theGoals)
         .setOutputHandler(taskLogger)
         .setErrorHandler(taskLogger);

      Future<InvocationResultImpl> f = pool.submit(new Callable<InvocationResultImpl>()
      {
         @Override
         public InvocationResultImpl call() throws MavenInvocationException
         {
            return invoker.execute(request);
         }
      });

      final String id = nextTaskID();
      MavenBuildTask task = new MavenBuildTask(id, f, projectDirectory, taskLogger);
      add(id, task, System.currentTimeMillis() + cleanBuildResultDelayMillis);

      return task;
   }

   private void add(String id, MavenBuildTask task, long expirationTime)
   {
      CacheElement newElement = new CacheElement(id, task, expirationTime);
      CacheElement prevElement = map.put(id, newElement);
      if (prevElement != null)
      {
         queue.remove(prevElement);
      }

      queue.add(newElement);

      CacheElement current;
      while ((current = queue.peek()) != null && current.isExpired())
      {
         // Task must be already stopped. MavenInvoker controls build process and terminated it if build time exceeds
         // the limit (DEFAULT_BUILDER_TIMEOUT).
         queue.remove(current);
         map.remove(current.id);
         cleanerQueue.offer(current.task.getProjectDirectory());
         cleanerQueue.offer(current.task.getLogger().getFile());
      }
   }

   /**
    * Get the build task by ID.
    *
    * @param id the build ID
    * @return build task or <code>null</code> if there is no build with specified ID
    */
   public MavenBuildTask get(String id)
   {
      CacheElement e = map.get(id);
      return e != null ? e.task : null;
   }

   /**
    * Cancel build.
    *
    * @param id the ID of build to cancel
    * @return canceled build task or <code>null</code> if there is no build with specified ID
    */
   public MavenBuildTask cancel(String id)
   {
      MavenBuildTask task = get(id);
      if (task != null)
      {
         task.cancel();
      }
      return task;
   }

   /** Shutdown current BuildService. */
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
      finally
      {
         cleaner.shutdownNow();

         // Remove all build results.
         // Not need to keep any artifacts of logs since they are inaccessible after stopping BuildService.
         for (File f : repository.listFiles(makeBuilderFilesFilter()))
         {
            delete(f);
         }
      }
   }

   private class CleanTask implements Runnable
   {
      public void run()
      {
         //System.err.println("CLEAN " + new Date() + " " + cleanerQueue.size());
         Set<File> failToDelete = new LinkedHashSet<File>();
         File f;
         while ((f = cleanerQueue.poll()) != null)
         {
            if (!delete(f))
            {
               failToDelete.add(f);
            }
         }
         if (!failToDelete.isEmpty())
         {
            cleanerQueue.addAll(failToDelete);
         }
      }
   }

   private static final class CacheElement
   {
      private final long expirationTime;
      private final int hash;

      final String id;
      final MavenBuildTask task;

      CacheElement(String id, MavenBuildTask task, long expirationTime)
      {
         this.id = id;
         this.task = task;
         this.expirationTime = expirationTime;
         this.hash = 7 * 31 + id.hashCode();
      }

      @Override
      public boolean equals(Object o)
      {
         if (this == o)
         {
            return true;
         }
         if (o == null || getClass() != o.getClass())
         {
            return false;
         }
         CacheElement e = (CacheElement)o;
         return id.equals(e.id);
      }

      @Override
      public int hashCode()
      {
         return hash;
      }

      boolean isExpired()
      {
         return expirationTime < System.currentTimeMillis();
      }
   }
}
