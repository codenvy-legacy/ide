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
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.MavenCommandLineBuilder;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.StreamPumper;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MavenInvoker.java 16504 2011-02-16 09:27:51Z andrew00x $
 */
public final class MavenInvoker extends DefaultInvoker
{
   public static final int INDEFINITE_EXIT_CODE = Integer.MIN_VALUE;

   private TaskWatcher watcher;

   /**
    * @see org.apache.maven.shared.invoker.DefaultInvoker#execute(org.apache.maven.shared.invoker.InvocationRequest)
    */
   @Override
   public InvocationResult execute(InvocationRequest request) throws MavenInvocationException
   {
      MavenCommandLineBuilder clBuilder = new MavenCommandLineBuilder();

      if (getLogger() != null)
         clBuilder.setLogger(getLogger());

      if (getLocalRepositoryDirectory() != null)
         clBuilder.setLocalRepositoryDirectory(getLocalRepositoryDirectory());

      if (getMavenHome() != null)
         clBuilder.setMavenHome(getMavenHome());

      if (getWorkingDirectory() != null)
         clBuilder.setWorkingDirectory(getWorkingDirectory());

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
      int exitCode = INDEFINITE_EXIT_CODE;
      try
      {
         InvocationOutputHandler out = request.getOutputHandler(null);
         InvocationOutputHandler err = request.getErrorHandler(null);
         exitCode = executeCommandLine(cl, out, err);
      }
      catch (CommandLineException e)
      {
         cle = e;
      }

      InvocationResultImpl result = new InvocationResultImpl(cle, exitCode);
      return result;
   }

   private int executeCommandLine(Commandline cl, StreamConsumer out, StreamConsumer err) throws CommandLineException
   {
      Process process = cl.execute();

      StreamPumper outPipe = new StreamPumper(process.getInputStream(), out);
      StreamPumper errPipe = new StreamPumper(process.getErrorStream(), err);

      int exitCode = INDEFINITE_EXIT_CODE;

      try
      {
         outPipe.start();
         errPipe.start();

         if (getWatcher() != null)
            getWatcher().start(process);

         try
         {
            exitCode = process.waitFor();

            synchronized (outPipe)
            {
               while (!outPipe.isDone())
                  outPipe.wait();
            }

            synchronized (errPipe)
            {
               while (!errPipe.isDone())
                  errPipe.wait();
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

         if (getWatcher() != null)
            getWatcher().stop();
      }

      return exitCode;
   }

   public TaskWatcher getWatcher()
   {
      return watcher;
   }

   public MavenInvoker setWatcher(TaskWatcher watcher)
   {
      this.watcher = watcher;
      return this;
   }
}
