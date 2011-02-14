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



import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.SystemOutHandler;
import org.exoplatform.ide.build.BuildTask;
import org.exoplatform.ide.build.BuildWatcher;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

class MavenBuildTask extends BuildTask<InvocationResult>
{
   // Simply store log messages in string buffer.
   class LogOutputHandler implements InvocationOutputHandler
   {
      private StringBuilder logBuf;
      private InvocationOutputHandler delegate;

      public LogOutputHandler(InvocationOutputHandler delegate)
      {
         this.logBuf = new StringBuilder();
         this.delegate = delegate;
      }

      public void consumeLine(String line)
      {
         synchronized (logBuf)
         {
            if (line == null)
               logBuf.append('\n');
            else
               logBuf.append('\n').append(line);
         }
         if (delegate != null)
            delegate.consumeLine(line);
      }

      public String lastLog()
      {
         synchronized (logBuf)
         {
            String log = logBuf.toString();
            logBuf.setLength(0);
            return log;
         }
      }
   }

   //
   private final ExtendedInvoker invoker;
   //private final InvocationRequest request;
   private final LogOutputHandler logHandler;

   public MavenBuildTask(final ExtendedInvoker invoker, final InvocationRequest request)
   {
      super(new Callable<InvocationResult>() {
         @Override
         public InvocationResult call() throws Exception
         {
            return invoker.execute(request);
         }
      });
      this.invoker = invoker;
      //this.request = request;
      logHandler = new LogOutputHandler(new SystemOutHandler(true));
      request.setOutputHandler(logHandler);
      request.setErrorHandler(logHandler);
   }

   /**
    * @see org.exoplatform.ide.build.BuildTask#getLog()
    */
   @Override
   public StreamingOutput getLog()
   {
      final String log = logHandler.lastLog();
      return new StreamingOutput() {
         @Override
         public void write(OutputStream output) throws IOException, WebApplicationException
         {
            output.write(log.getBytes("UTF-8"));
         }
      };
   }

   @Override
   public boolean cancel(boolean mayInterruptIfRunning)
   {
      BuildWatcher buildWatcher = invoker.getWatcher();
      if (buildWatcher != null)
         buildWatcher.stop();
      else
         super.cancel(mayInterruptIfRunning);
      return isDone();
   }
}