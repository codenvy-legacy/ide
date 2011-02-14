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
package org.exoplatform.ide.build;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class BuildTaskPool
{
   private static int counter = 1000000;

   protected final ExecutorService threadPool;
   protected final Map<String, BuildTask<?>> tasks;

   public BuildTaskPool(int poolSize)
   {
      threadPool = new ThreadPoolExecutor(poolSize, //
         poolSize, //
         0L, //
         TimeUnit.MILLISECONDS, //
         new LinkedBlockingQueue<Runnable>());
      tasks = Collections.synchronizedMap(new HashMap<String, BuildTask<?>>());
   }

   public String add(BuildTask<?> task)
   {
      threadPool.execute(task);
      String key = generateKey(task);
      tasks.put(key, task);
      return key;
   }

   public BuildTask<?> get(String key)
   {
      return tasks.get(key);
   }

   public BuildTask<?> remove(String key)
   {
      BuildTask<?> task = tasks.remove(key);
      if (task != null)
         task.cancel(true);
      return task;
   }

   public void shutdown()
   {
      // Stop all running tasks.
      threadPool.shutdownNow();
   }

   protected synchronized String generateKey(BuildTask<?> task)
   {
      counter++;
      return Integer.toString(counter);
   }
}
