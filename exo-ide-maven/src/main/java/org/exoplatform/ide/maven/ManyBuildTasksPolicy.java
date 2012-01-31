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
package org.exoplatform.ide.maven;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Handler that rejects new build if there is too many builds in progress.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ManyBuildTasksPolicy implements RejectedExecutionHandler
{
   private final RejectedExecutionHandler delegate;

   public ManyBuildTasksPolicy(RejectedExecutionHandler delegate)
   {
      this.delegate = delegate;
   }

   /**
    * @see java.util.concurrent.RejectedExecutionHandler#rejectedExecution(Runnable,
    *      java.util.concurrent.ThreadPoolExecutor)
    */
   @Override
   public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
   {
      if (executor.getPoolSize() >= executor.getCorePoolSize())
      {
         throw new RejectedExecutionException("Too many builds in progress ");
      }
      delegate.rejectedExecution(r, executor);
   }
}
