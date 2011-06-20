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
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.SystemOutHandler;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MavenTask extends FutureTask<InvocationResult>
{
   private final MavenInvoker invoker;
   private final TaskLogger taskLogger;
   private final String taskId;

   MavenTask(MavenInvoker invoker, InvocationRequest request, String taskId)
   {
      this(invoker, request, taskId, new SimpleTaskLogger(new SystemOutHandler(true)));
   }

   MavenTask(final MavenInvoker invoker, final InvocationRequest request, final String taskId,
      final TaskLogger taskLogger)
   {
      super(new Callable<InvocationResult>()
      {
         @Override
         public InvocationResult call() throws Exception
         {
            System.err.println("\n" //
               + "=============== RUN MAVEN BUILD ===============\n" //
               + "GOALS: " //
               + request.getGoals() //
               + "\n" //
               + "PROPERTIES: " //
               + request.getProperties() //
               + "\n" //
               + "===============================================");
            InvocationResult result = invoker.execute(request);
            return result;
         }
      });
      this.invoker = invoker;
      this.taskId = taskId;
      this.taskLogger = taskLogger;
      request.setOutputHandler(taskLogger);
      request.setErrorHandler(taskLogger);
   }

   /**
    * @see java.util.concurrent.FutureTask#cancel(boolean)
    */
   @Override
   public boolean cancel(boolean mayInterruptIfRunning)
   {
      TaskWatcher taskWatcher = invoker.getWatcher();
      if (taskWatcher != null)
         taskWatcher.stop();
      else
         super.cancel(mayInterruptIfRunning);
      return isDone();
   }

   public TaskLogger getTaskLogger()
   {
      return taskLogger;
   }
   
   public String getId()
   {
      return taskId;
   }
}