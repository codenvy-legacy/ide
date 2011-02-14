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
package org.exoplatform.ide.build.maven;


import org.apache.maven.shared.invoker.CommandLineConfigurationException;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.InvokerLogger;
import org.apache.maven.shared.invoker.MavenCommandLineBuilder;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.StreamFeeder;
import org.codehaus.plexus.util.cli.StreamPumper;
import org.exoplatform.ide.build.BuildWatcher;

import java.io.File;
import java.io.InputStream;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ExtendedInvoker extends DefaultInvoker
{
   public static final int INDEFINITE_EXIT_CODE = Integer.MIN_VALUE;

   private BuildWatcher watcher;

   @Override
   public InvocationResult execute(InvocationRequest request) throws MavenInvocationException
   {
      MavenCommandLineBuilder cliBuilder = new MavenCommandLineBuilder();
      InvokerLogger logger = getLogger();
      if (logger != null)
         cliBuilder.setLogger(getLogger());

      File localRepo = getLocalRepositoryDirectory();
      if (localRepo != null)
         cliBuilder.setLocalRepositoryDirectory(getLocalRepositoryDirectory());

      File mavenHome = getMavenHome();
      if (mavenHome != null)
         cliBuilder.setMavenHome(getMavenHome());

      File workingDirectory = getWorkingDirectory();
      if (workingDirectory != null)
         cliBuilder.setWorkingDirectory(getWorkingDirectory());

      Commandline cli;
      try
      {
         cli = cliBuilder.build(request);
      }
      catch (CommandLineConfigurationException e)
      {
         throw new MavenInvocationException("Error configuring command-line. Reason: " + e.getMessage(), e);
      }

      CommandLineException err = null;
      int exitCode = INDEFINITE_EXIT_CODE;
      try
      {
         // Do not use interactive mode - not need input stream.
         InputStream inputStream = null;
         InvocationOutputHandler outputHandler = request.getOutputHandler(null);
         InvocationOutputHandler errorHandler = request.getErrorHandler(null);
         exitCode = executeCommandLine(cli, inputStream, outputHandler, errorHandler);
      }
      catch (CommandLineException e)
      {
         err = e;
      }

      InvocationResultImpl result = new InvocationResultImpl(err, exitCode);
      return result;
   }

   private int executeCommandLine(Commandline cl, InputStream systemIn, StreamConsumer systemOut,
      StreamConsumer systemErr) throws CommandLineException
   {
      if (cl == null)
         throw new IllegalArgumentException("Commandline argument may not be null. ");

      Process process = cl.execute();

      StreamFeeder inputFeeder = systemIn != null ? new StreamFeeder(systemIn, process.getOutputStream()) : null;
      StreamPumper outputPumper = new StreamPumper(process.getInputStream(), systemOut);
      StreamPumper errorPumper = new StreamPumper(process.getErrorStream(), systemErr);

      int returnValue = INDEFINITE_EXIT_CODE;

      try
      {
         if (inputFeeder != null)
            inputFeeder.start();
         outputPumper.start();
         errorPumper.start();

         if (watcher != null)
            watcher.start(process);

         try
         {
            returnValue = process.waitFor();
            if (inputFeeder != null)
            {
               synchronized (inputFeeder)
               {
                  while (!inputFeeder.isDone())
                     inputFeeder.wait();
               }
            }

            synchronized (outputPumper)
            {
               while (!outputPumper.isDone())
                  outputPumper.wait();
            }

            synchronized (errorPumper)
            {
               while (!errorPumper.isDone())
                  errorPumper.wait();
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
         if (inputFeeder != null)
            inputFeeder.close();
         outputPumper.close();
         errorPumper.close();

         if (watcher != null)
            watcher.stop();
      }
      return returnValue;
   }

   public BuildWatcher getWatcher()
   {
      return watcher;
   }

   public void setWatcher(BuildWatcher watcher)
   {
      this.watcher = watcher;
   }
}
