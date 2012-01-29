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
import org.apache.maven.shared.invoker.SystemOutHandler;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

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
   public static final String BUILD_REPOSITORY = "build.repository";

   /**
    * Name of configuration parameter that provides maven build goals.
    * @see #DEFAULT_BUILD_MAVEN_GOALS
    */
   public static final String BUILD_MAVEN_GOALS = "build.maven.goals";

   /**
    * Name of configuration parameter that provides build timeout is seconds. After this time build may be terminated.
    * @see #DEFAULT_TIMEOUT
    */
   public static final String BUILD_TIMEOUT = "build.timeout";

   /**
    * Name of configuration parameter that set number of build workers in other words it set the number of build process
    * that can be run at the same time. If this parameter is not set then the number of available processors used, e.g.
    * <code>Runtime.getRuntime().availableProcessors();</code>
    */
   public static final String BUILD_WORKERS_NUMBER = "build.workers.number";

   /**
    * Name of parameter that set the max size of build queue. The number of build task in queue may not be greater than
    * provided by this parameter.
    * @see #DEFAULT_BUILD_QUEUE_SIZE
    */
   public static final String BUILD_QUEUE_SIZE = "build.queue.size";

   /** Default build timeout in seconds (120). After this time build may be terminated. */
   public static final int DEFAULT_TIMEOUT = 120;

   /** Default max size of build queue (100). */
   public static final int DEFAULT_BUILD_QUEUE_SIZE = 100;

   /** Default maven build goals 'test package'. */
   public static final String[] DEFAULT_BUILD_MAVEN_GOALS = new String[]{"test", "package"};


   //
   protected static class ManyTasksPolicy implements RejectedExecutionHandler
   {
      private final RejectedExecutionHandler delegate;

      public ManyTasksPolicy(RejectedExecutionHandler delegate)
      {
         this.delegate = delegate;
      }

      /**
       * @see java.util.concurrent.RejectedExecutionHandler#rejectedExecution(java.lang.Runnable,
       *      java.util.concurrent.ThreadPoolExecutor)
       */
      @Override
      public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
      {
         if (executor.getPoolSize() >= executor.getCorePoolSize())
         {
            throw new RejectedExecutionException("Too many tasks. ");
         }
         delegate.rejectedExecution(r, executor);
      }
   }


   /** Build task ID generator. */
   private static final AtomicLong idGenerator = new AtomicLong(1);

   protected final ExecutorService pool;
   protected final ConcurrentMap<String, MavenBuildTask> tasks;

   protected final String repository;
   protected final String[] goals;
   protected final Integer timeout;

   public BuildService(Map<String, Object> config)
   {
      this(
         (String)getOption(config, BUILD_REPOSITORY, System.getProperty("java.io.tmpdir")),
         (String[])getOption(config, BUILD_MAVEN_GOALS, DEFAULT_BUILD_MAVEN_GOALS),
         (Integer)getOption(config, BUILD_TIMEOUT, DEFAULT_TIMEOUT),
         (Integer)getOption(config, BUILD_WORKERS_NUMBER, Runtime.getRuntime().availableProcessors()),
         (Integer)getOption(config, BUILD_QUEUE_SIZE, DEFAULT_BUILD_QUEUE_SIZE)
      );
   }

   protected BuildService(String repository, String[] goals, int timeout, int workerNumber, int queueSize)
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
      if (queueSize <= 0)
      {
         throw new IllegalArgumentException("Size of build queue may not be equals or less than 0. ");
      }

      this.repository = repository;
      this.goals = goals;
      this.timeout = timeout;
      this.tasks = new ConcurrentHashMap<String, MavenBuildTask>();

      //
      this.pool = new ThreadPoolExecutor(
         workerNumber,
         workerNumber,
         0L,
         TimeUnit.MILLISECONDS,
         new LinkedBlockingQueue<Runnable>(queueSize),
         new ManyTasksPolicy(new ThreadPoolExecutor.AbortPolicy()));
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
    * @param remoteURI the GIT location of source code for build
    * @return build task
    */
   public MavenBuildTask add(final String remoteURI)
   {
      if (remoteURI == null || remoteURI.isEmpty())
      {
         throw new IllegalArgumentException("Parameter 'remoteURI' may not be null or empty. ");
      }

      List<String> g = new ArrayList<String>(goals.length);
      Collections.addAll(g, goals);

      final File projectDirectory = BuildHelper.makeProjectDirectory(repository);

      final MavenInvoker invoker = new MavenInvoker()
         .addPreBuildTask(
            new Runnable()
            {
               public void run()
               {
                  Git.cloneRepository()
                     .setDirectory(projectDirectory)
                     .setURI(remoteURI)
                     .call();
               }
            }
         ).setTimeout(timeout);

      TaskLogger taskLogger =
         new FileTaskLogger(new File(projectDirectory.getParentFile(), projectDirectory.getName() + ".log"),
            new SystemOutHandler());

      final InvocationRequest request = new DefaultInvocationRequest()
         .setBaseDirectory(projectDirectory)
         .setGoals(g)
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

      final String id = Long.toString(idGenerator.getAndIncrement());
      MavenBuildTask task = new MavenBuildTask(id, f, taskLogger);
      tasks.put(id, task);

      return task;
   }

   /**
    * Get the build task by ID.
    *
    * @param id the build ID
    * @return build task or <code>null</code> if there is no build with specified ID
    */
   public MavenBuildTask get(String id)
   {
      return tasks.get(id);
   }

   /**
    * Cancel build.
    *
    * @param id the ID of build to cancel
    * @return canceled build task or <code>null</code> if there is no build with specified ID
    */
   public MavenBuildTask cancel(String id)
   {
      MavenBuildTask task = tasks.remove(id);
      if (task != null)
      {
         task.cancel();
      }
      return task;
   }

   /** Shutdown current BuildService. */
   public void shutdown()
   {
      pool.shutdownNow();
   }
}
