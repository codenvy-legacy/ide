/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.fs;

/**
 * Advisory file lock. It does not prevent access to the file from other programs.
 * <p/>
 * Usage:
 * <pre>
 *      FileLockFactory lockFactory = ...
 *
 *      public void doSomething(Path path)
 *      {
 *         FileLock exclusiveLock = lockFactory.getLock(path, true).acquire(30000);
 *         try
 *         {
 *            ... // do something
 *         }
 *         finally
 *         {
 *            exclusiveLock.release();
 *         }
 *      }
 * </pre>
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;

import java.util.HashMap;
import java.util.Map;

/**
 * Advisory file locks. It does not prevent access to the file from other programs.
 * <p/>
 * Usage:
 * <pre>
 *      FileLockFactory lockFactory = ...
 *
 *      public void doSomething(Path path)
 *      {
 *         FileLock exclusiveLock = lockFactory.getLock(path, true).acquire(30000);
 *         try
 *         {
 *            ... // do something
 *         }
 *         finally
 *         {
 *            exclusiveLock.release();
 *         }
 *      }
 * </pre>
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class FileLockFactory
{
   /** Max number of threads allowed to access file. */
   private final int maxThreads;
   private final Map<Path, Integer> threads;

   /**
    * @param maxThreads
    *    the max number of threads are allowed to access one file. Typically this parameter should be big enough to
    *    avoid blocking threads that need to obtain NOT exclusive lock.
    */
   FileLockFactory(int maxThreads)
   {
      if (maxThreads < 1)
      {
         throw new IllegalArgumentException();
      }
      this.maxThreads = maxThreads;
      threads = new HashMap<Path, Integer>();
   }

   FileLock getLock(Path path, boolean exclusive)
   {
      return new FileLock(path, exclusive ? maxThreads : 1);
   }

   private synchronized void acquire(Path path, int permits)
   {
      int permittedThreads;
      while ((permittedThreads = getPermittedThreads(path)) < permits)
      {
         try
         {
            wait();
         }
         catch (InterruptedException e)
         {
            throw new VirtualFileSystemRuntimeException(e);
         }
      }
      threads.put(path, permittedThreads - permits);
   }

   private synchronized void acquire(Path path, int permits, long timeoutMilliseconds)
   {
      final long endTime = System.currentTimeMillis() + timeoutMilliseconds;
      long waitTime = timeoutMilliseconds;
      int permittedThreads;
      while ((permittedThreads = getPermittedThreads(path)) < permits)
      {
         try
         {
            wait(waitTime);
         }
         catch (InterruptedException e)
         {
            throw new VirtualFileSystemRuntimeException(e);
         }
         long now = System.currentTimeMillis();
         if (now >= endTime)
         {
            throw new FileLockTimeoutException(String.format("Get lock timeout for '%s'. ", path));
         }
         waitTime = endTime - now;
      }
      threads.put(path, permittedThreads - permits);
   }

   private synchronized void release(Path path, int permits)
   {
      int permittedThreads = getPermittedThreads(path) + permits;
      if (permittedThreads >= maxThreads)
      {
         threads.remove(path);
      }
      else
      {
         threads.put(path, permittedThreads);
      }
      notifyAll();
   }

   private int getPermittedThreads(Path path)
   {
      Integer permittedThreads = threads.get(path);
      if (permittedThreads == null)
      {
         permittedThreads = maxThreads;
      }
      Path parent;
      while ((parent = path.getParent()) != null)
      {
         Integer parentPermittedThreads = threads.get(parent);
         if (parentPermittedThreads != null)
            permittedThreads -= parentPermittedThreads;
         path = parent;
      }
      return permittedThreads;// == null ? maxThreads : permittedThreads;
   }

   /* =============================================== */

   class FileLock
   {
      private final Path path;
      private final int permits;

      private FileLock(Path path, int permits)
      {
         //To change body of created methods use File | Settings | File Templates.
         this.path = path;
         this.permits = permits;
      }

      /**
       * Acquire permit for file. Method is blocked until permit available.
       *
       * @return this FileLock instance
       */
      FileLock acquire()
      {
         FileLockFactory.this.acquire(path, permits);
         return this;
      }

      /**
       * Acquire permit for file if it becomes available within the given timeout. It is the same as method {@link
       * #acquire()} but with waiting timeout. If waiting timeout reached then FileLockTimeoutException thrown.
       *
       * @param timeoutMilliseconds
       *    maximum time (in milliseconds) to wait for access permit
       * @return this FileLock instance
       * @throws FileLockTimeoutException
       *    if waiting timeout reached
       */
      FileLock acquire(long timeoutMilliseconds)
      {
         FileLockFactory.this.acquire(path, permits, timeoutMilliseconds);
         return this;
      }

      /** Release file permit. */
      void release()
      {
         FileLockFactory.this.release(path, permits);
      }
   }
}
