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

import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.File;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Maven build task.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MavenBuildTask
{
   private final String id;
   private final Future<InvocationResultImpl> f;
   private final File projectDirectory;
   private final TaskLogger logger;

   public MavenBuildTask(String id, Future<InvocationResultImpl> f, File projectDirectory, TaskLogger logger)
   {
      this.id = id;
      this.f = f;
      this.projectDirectory = projectDirectory;
      this.logger = logger;
   }

   /**
    * Get build unique ID.
    *
    * @return build ID
    */
   public String getId()
   {
      return id;
   }

   /**
    * Get build logger.
    *
    * @return build logger
    */
   public TaskLogger getLogger()
   {
      return logger;
   }

   /**
    * Check is build done or not. Note build may be successful or failed.
    *
    * @return <code>true</code> if build is done and <code>false</code> otherwise
    */
   public boolean isDone()
   {
      return f.isDone();
   }

   /** Cancel maven build. */
   public void cancel()
   {
      f.cancel(true);
   }

   /**
    * Get result of maven build.
    *
    * @return result of maven build. <b>NOTE</b> If build is not finished yet this method returns <code>null</code>
    * @throws MavenInvocationException
    *    if maven task cannot be run because to incorrect input parameters
    */
   public InvocationResultImpl getInvocationResult() throws MavenInvocationException
   {
      if (f.isDone())
      {
         try
         {
            return f.get();
         }
         catch (InterruptedException e)
         {
            // Should not happen since we checked is task done or not.
            Thread.currentThread().interrupt();
         }
         catch (ExecutionException e)
         {
            final Throwable cause = e.getCause();
            if (cause instanceof Error)
            {
               throw (Error)cause;
            }
            if (cause instanceof RuntimeException)
            {
               throw (RuntimeException)cause;
            }
            throw (MavenInvocationException)cause;
         }
         catch (CancellationException ce)
         {
            throw new MavenInvocationException("Job " + id + " was cancelled. ");
         }
      }
      return null;
   }

   /**
    * Get the maven project directory.
    *
    * @return the maven project directory
    */
   public File getProjectDirectory()
   {
      return projectDirectory;
   }
}
