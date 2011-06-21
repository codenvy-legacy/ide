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
package org.exoplatform.ide.extension.maven;

import org.apache.maven.shared.invoker.InvocationRequest;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.picocontainer.Startable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TaskService.java 16504 2011-02-16 09:27:51Z andrew00x $
 */
public class TaskService implements Startable
{
   protected static class ToManyTaskPolicy implements RejectedExecutionHandler
   {
      private final RejectedExecutionHandler delegate;

      public ToManyTaskPolicy(RejectedExecutionHandler delegate)
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
            throw new RejectedExecutionException("Too many tasks. ");
         delegate.rejectedExecution(r, executor);
      }
   }

   private static int counter = 1000000;

   protected final ExecutorService threadPool;
   protected final Map<String, MavenTask> tasks;

   public TaskService(InitParams params)
   {
      this(getPoolSize(params, "pool-size"));
   }

   private static int getPoolSize(InitParams initParams, String paramName)
   {
      if (initParams != null)
      {
         ValueParam vp = initParams.getValueParam(paramName);
         if (vp != null)
            return Integer.parseInt(vp.getValue());
      }
      return 1; // single thread pool
   }

   protected TaskService(int poolSize)
   {
      this( //
         new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, //
            // TODO : Configurable the number of tasks in queue or disable using queue at all. If using queue is disable
            // then tasks must be rejected. Limit queue for adding tasks. It will rise RejectedExecutionHandler by
            // ToManyTaskPolicy.
            new LinkedBlockingQueue<Runnable>(10),
            // Default rejected handler of ThreadPoolExecutor is AbortPolicy.
            new ToManyTaskPolicy(new ThreadPoolExecutor.AbortPolicy())) //
      );
   }

   protected TaskService(ThreadPoolExecutor threadPool)
   {
      this.threadPool = threadPool;
      this.tasks = Collections.synchronizedMap(new HashMap<String, MavenTask>());
   }

   public MavenTask add(InvocationRequest request, TaskWatcher watcher)
   {
      String taskId = generateId(request);
      MavenInvoker invoker = new MavenInvoker();
      if (watcher != null)
         invoker.setWatcher(watcher);
      MavenTask task = new MavenTask(invoker, request, taskId);
      threadPool.execute(task);
      tasks.put(taskId, task);
      return task;
   }

   public MavenTask get(String key)
   {
      return tasks.get(key);
   }

   public MavenTask remove(String key)
   {
      MavenTask task = tasks.remove(key);
      if (task != null)
         task.cancel(true);
      return task;
   }

   public void shutdown()
   {
      threadPool.shutdownNow();
   }

   protected synchronized String generateId(InvocationRequest request)
   {
      counter++;
      return Integer.toString(counter);
   }

   /**
    * @see org.picocontainer.Startable#start()
    */
   @Override
   public void start()
   {
   }

   /**
    * @see org.picocontainer.Startable#stop()
    */
   @Override
   public void stop()
   {
      shutdown();
   }
}
