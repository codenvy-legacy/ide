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


/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public final class BuildWatcher implements Runnable
{
   private final BuildDestroyer destroyer;
   private final long timeout;

   private Process process;
   private boolean stopFlag;
   private boolean stopped;

   public BuildWatcher(BuildDestroyer destroyer, long timeout)
   {
      if (destroyer == null)
         throw new NullPointerException("BuildDestroyer argument may not be null. ");
      if (timeout < 1)
         throw new IllegalArgumentException("Timeout must be greater than 1. ");
      this.destroyer = destroyer;
      this.timeout = timeout;
   }

   public BuildWatcher(long timeout)
   {
      this(BuildDestroyer.DEFAULT_DESTROYER, timeout);
   }

   /**
    * @see java.lang.Runnable#run()
    */
   @Override
   public synchronized void run()
   {
      final long end = System.currentTimeMillis() + timeout;
      long now;
      while (!stopFlag && end > (now = System.currentTimeMillis()))
      {
         try
         {
            wait(end - now);
         }
         catch (InterruptedException e)
         {
            // ignored
         }
      }
      if (!stopped)
      {
         destroyer.destroy(process);
         stopped = true;
      }
   }
   
   public synchronized boolean isProcessAlive()
   {
      if (process == null)
         throw new IllegalStateException("Process is not initialized yet. ");
      try
      {
         process.exitValue();
         return false;
      }
      catch (IllegalThreadStateException e)
      {
      }
      return true;
   }

   public synchronized boolean isProcessStopped()
   {
      return stopped;
   }

   public synchronized void start(Process process)
   {
      if (process == null)
         throw new NullPointerException("Watched process may not be null. ");
      this.process = process;
      this.stopped = false;
      this.stopFlag = false;
      Thread t = new Thread(this);
      t.setDaemon(true);
      t.start();
   }

   public synchronized void stop()
   {
      stopFlag = true;
      notifyAll();
   }
}
