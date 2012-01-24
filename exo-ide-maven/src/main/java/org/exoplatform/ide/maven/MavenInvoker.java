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

import org.apache.maven.shared.invoker.CommandLineConfigurationException;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.MavenCommandLineBuilder;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.StreamPumper;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MavenInvoker.java 16504 2011-02-16 09:27:51Z andrew00x $
 */
public class MavenInvoker extends DefaultInvoker
{
   private final BuildWatcher watcher;
   private final Queue<Runnable> preBuildTasks;
   private final Queue<Runnable> postBuildTasks;

   public MavenInvoker(BuildWatcher watcher)
   {
      if (watcher == null)
      {
         throw new IllegalArgumentException("Parameter 'watcher' may not be null. ");
      }
      this.watcher = watcher;
      this.preBuildTasks = new LinkedList<Runnable>();
      this.postBuildTasks = new LinkedList<Runnable>();
   }

   /** @see org.apache.maven.shared.invoker.DefaultInvoker#execute(org.apache.maven.shared.invoker.InvocationRequest) */
   @Override
   public InvocationResultImpl execute(InvocationRequest request) throws MavenInvocationException
   {
      MavenCommandLineBuilder clBuilder = new MavenCommandLineBuilder();

      if (getLogger() != null)
      {
         clBuilder.setLogger(getLogger());
      }

      if (getLocalRepositoryDirectory() != null)
      {
         clBuilder.setLocalRepositoryDirectory(getLocalRepositoryDirectory());
      }

      if (getMavenHome() != null)
      {
         clBuilder.setMavenHome(getMavenHome());
      }

      if (getWorkingDirectory() != null)
      {
         clBuilder.setWorkingDirectory(getWorkingDirectory());
      }

      Commandline cl;
      try
      {
         cl = clBuilder.build(request);
      }
      catch (CommandLineConfigurationException e)
      {
         throw new MavenInvocationException("Error configuring command-line. Reason: " + e.getMessage(), e);
      }

      CommandLineException cle = null;
      int exitCode = Integer.MIN_VALUE;
      InvocationOutputHandler out = request.getOutputHandler(null);
      InvocationOutputHandler err = request.getErrorHandler(null);

      while (!preBuildTasks.isEmpty())
      {
         preBuildTasks.poll().run();
      }

      try
      {
         exitCode = executeCommandLine(cl, out, err);
      }
      catch (CommandLineException e)
      {
         cle = e;
      }

      while (!postBuildTasks.isEmpty())
      {
         postBuildTasks.poll().run();
      }

      File[] result = null;
      if (0 == exitCode)
      {
         final File target = new File(request.getBaseDirectory(), "target");
         result = target.listFiles(new FilenameFilter()
         {
            public boolean accept(File parent, String name)
            {
               return name.endsWith(".war");
            }
         });
      }

      return new InvocationResultImpl(exitCode, cle, result);
   }

   private int executeCommandLine(Commandline cl, StreamConsumer out, StreamConsumer err) throws CommandLineException
   {
      Process process = cl.execute();

      watcher.start(process);

      StreamPumper outPipe = new StreamPumper(process.getInputStream(), out);
      StreamPumper errPipe = new StreamPumper(process.getErrorStream(), err);

      int exitCode = Integer.MIN_VALUE;

      try
      {
         outPipe.start();
         errPipe.start();

         try
         {
            exitCode = process.waitFor();

            synchronized (outPipe)
            {
               while (!outPipe.isDone())
               {
                  outPipe.wait();
               }
            }

            synchronized (errPipe)
            {
               while (!errPipe.isDone())
               {
                  errPipe.wait();
               }
            }
         }
         catch (InterruptedException e)
         {
            process.destroy();
         }
         finally
         {
            Thread.interrupted();
         }
      }
      finally
      {
         outPipe.close();
         errPipe.close();

         if (out instanceof TaskLogger)
         {
            ((TaskLogger)out).close();
         }

         if (err instanceof TaskLogger)
         {
            ((TaskLogger)err).close();
         }

         watcher.stop();
      }

      return exitCode;
   }

   /** Stop underlying maven build. */
   public void stop()
   {
      watcher.stop();
   }

   public MavenInvoker addPreBuildTask(Runnable task)
   {
      preBuildTasks.add(task);
      return this;
   }

   public MavenInvoker addPostBuildTask(Runnable task)
   {
      postBuildTasks.add(task);
      return this;
   }
}
