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
   private static final int MAX_RECURSIVE_LOCKS = (1 << 10) - 1;
   /** Max number of threads allowed to access file. */
   private final int maxThreads;
   // Tail of the "lock table".
   private final Node tail = new Node(null, 0, null);

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
   }

   FileLock getLock(Path path, boolean exclusive)
   {
      return new FileLock(path, exclusive ? maxThreads : 1);
   }

   private synchronized void acquire(Path path, int permits)
   {
      while (!tryAcquire(path, permits))
      {
         try
         {
            wait();
         }
         catch (InterruptedException e)
         {
            notify();
            throw new VirtualFileSystemRuntimeException(e);
         }
      }
   }

   private synchronized void acquire(Path path, int permits, long timeoutMilliseconds)
   {
      final long endTime = System.currentTimeMillis() + timeoutMilliseconds;
      long waitTime = timeoutMilliseconds;
      while (!tryAcquire(path, permits))
      {
         try
         {
            wait(waitTime);
         }
         catch (InterruptedException e)
         {
            notify();
            throw new VirtualFileSystemRuntimeException(e);
         }
         long now = System.currentTimeMillis();
         if (now >= endTime)
         {
            throw new FileLockTimeoutException(String.format("Get lock timeout for '%s'. ", path));
         }
         waitTime = endTime - now;
      }
   }

   private synchronized void release(Path path, int permits)
   {
      Node node = tail;
      while (node != null)
      {
         Node prev = node.prev;
         if (prev == null)
         {
            break;
         }
         if (prev.path.equals(path))
         {
            if (prev.threadDeep == 1)
            {
               // If last recursive lock.
               prev.permits += permits;
               if (prev.permits >= maxThreads)
               {
                  // remove
                  node.prev = prev.prev;
                  prev.prev = null;
               }
            }
            else
            {
               --prev.threadDeep;
            }
         }
         node = node.prev;
      }
      notifyAll();
   }

   private boolean tryAcquire(Path path, int permits)
   {
      Node node = tail.prev;
      final Thread current = Thread.currentThread();
      while (node != null)
      {
         if (node.path.equals(path))
         {
            if (node.threadId == current.getId())
            {
               // Current thread already has direct lock for this path
               if (node.threadDeep > MAX_RECURSIVE_LOCKS)
               {
                  throw new Error("Max number of recursive locks exceeded. ");
               }
               ++node.threadDeep;
               return true;
            }
            if (node.permits > permits)
            {
               // Lock already exists and current thread is not owner of this lock,
               // but lock is not exclusive and we can "share" it for other thread.
               node.permits -= permits; // decrement number of allowed concurrent threads
               return true;
            }
            // Lock is exclusive or max number of allowed concurrent thread is reached.
            return false;
         }
         else if ((node.path.isChild(path) || path.isChild(node.path)) && node.permits <= permits)
         {
            // Found some path which already has lock that prevents us to get required permits.
            // There is two possibilities:
            // 1. Parent of the path we try to lock already locked
            // 2. Child of the path we try to lock already locked
            // Need to check is such lock obtained by current thread or not.
            // If such lock obtained by other thread stop here immediately there is no reasons to continue.
            if (node.threadId != current.getId())
            {
               return false;
            }
         }
         node = node.prev;
      }
      // If we are here there is no lock for path yet.
      tail.prev = new Node(path, maxThreads - permits, tail.prev);
      return true;
   }

   /* =============================================== */

   private static class Node
   {
      final Path path;
      final long threadId = Thread.currentThread().getId();
      int permits;
      int threadDeep;
      Node prev;

      Node(Path path, int permits, Node prev)
      {
         this.path = path;
         this.permits = permits;
         this.prev = prev;
         threadDeep = 1;
      }

      @Override
      public String toString()
      {
         return "Node{" +
            "path=" + path +
            ", threadId=" + threadId +
            ", permits=" + permits +
            ", prev=" + prev +
            '}';
      }
   }

   class FileLock
   {
      private final Path path;
      private final int permits;

      private FileLock(Path path, int permits)
      {
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
       * @throws org.exoplatform.ide.vfs.impl.fs.FileLockTimeoutException
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

      /** Returns <code>true</code> if this lock is exclusive and <code>false</code> otherwise. */
      boolean isExclusive()
      {
         return permits == FileLockFactory.this.maxThreads;
      }
   }
}
