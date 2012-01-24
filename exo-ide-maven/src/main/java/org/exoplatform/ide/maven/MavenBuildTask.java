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

import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.MavenInvocationException;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Maven build task.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MavenBuildTask extends FutureTask<InvocationResultImpl>
{
   private final String id;
   private final MavenInvoker invoker;
   private final TaskLogger logger;

   MavenBuildTask(String id, InvocationRequest request, MavenInvoker invoker, TaskLogger logger)
   {
      super(callable(request, invoker));
      this.id = id;
      this.invoker = invoker;
      this.logger = logger;
   }

   private static Callable<InvocationResultImpl> callable(final InvocationRequest request, final MavenInvoker invoker)
   {
      return new Callable<InvocationResultImpl>()
      {
         @Override
         public InvocationResultImpl call() throws MavenInvocationException
         {
            return invoker.execute(request);
         }
      };
   }

   public String getId()
   {
      return id;
   }

   public TaskLogger getLogger()
   {
      return logger;
   }

   MavenInvoker getInvoker()
   {
      return invoker;
   }
}
