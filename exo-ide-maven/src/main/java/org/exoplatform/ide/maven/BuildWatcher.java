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

import java.util.concurrent.TimeUnit;

/**
 * Watch for build process and terminate it if timeout of build process is reached.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BuildWatcher.java 16504 2011-02-16 09:27:51Z andrew00x $
 */
public final class BuildWatcher implements Runnable
{
   private final long timeout;

   private Process process;
   private boolean stopFlag;
   private boolean stopped;

   /**
    * @param timeout timeout after which process will be terminated
    * @param unit the TimeUnit for <code>timeout</code> value
    */
   public BuildWatcher(long timeout, TimeUnit unit)
   {
      if (timeout < 1)
      {
         throw new IllegalArgumentException("Timeout must be greater than 1. ");
      }
      this.timeout = unit.toMillis(timeout);
   }

   /** @see java.lang.Runnable#run() */
   @Override
   public synchronized void run()
   {
      final long end = System.currentTimeMillis() + timeout;
      long now;
      while (!stopFlag && (end > (now = System.currentTimeMillis())))
      {
         try
         {
            wait(end - now);
         }
         catch (InterruptedException ignored)
         {
         }
      }
      if (!stopped)
      {
         if (isProcessAlive())
         {
            process.destroy();
         }
         stopped = true;
      }
   }

   public synchronized boolean isProcessAlive()
   {
      if (process == null)
      {
         throw new IllegalStateException("Process is not initialized yet. ");
      }
      try
      {
         return false;
      }
      catch (IllegalThreadStateException e)
      {
         return true;
      }
   }

   /**
    * Start watching for Process.
    *
    * @param process Process
    */
   public synchronized void start(Process process)
   {
      if (process == null)
      {
         throw new IllegalArgumentException("Watched process may not be null. ");
      }
      this.process = process;
      this.stopped = false;
      this.stopFlag = false;
      Thread t = new Thread(this);
      t.setDaemon(true);
      t.start();
   }

   /** Stop build process. */
   public synchronized void stop()
   {
      stopFlag = true;
      notifyAll();
   }
}
