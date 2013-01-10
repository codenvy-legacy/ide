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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Read/Write locking for files. It is possible to have many readers for the same file but only one writer. {@link
 * java.nio.channels.FileLock} does not work for this (at least under linux). The main problem is if we are failed to
 * get lock for file we cannot know file is locked for reading or writing.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class ReadWriteFileLock
{
   /** Max number of threads allowed to access file. */
   private final int maxThreads;
   private final Map<Path, Integer> threads;

   /**
    * New read-write file lock.
    *
    * @param maxThreads
    *    max allowed threads (read and write) allowed to access one file. Typically this parameter should be big enough
    *    to avoid locking read operation.
    */
   ReadWriteFileLock(int maxThreads)
   {
      this.maxThreads = maxThreads;
      threads = new HashMap<Path, Integer>();
   }

   /**
    * Acquire read permit for file with <code>path</code>, blocking until read permit is available. Thread may be
    * blocked until one of next things happens:
    * <ul>
    * <li>One of readers invokes method {@link #releaseRead(Path)} and current thread is next to get read permit</li>
    * <li>Writer which has acquired all available threads invokes method {@link #releaseRead(Path)}</li>
    * <li>Current thread is interrupted</li>
    * </ul>
    *
    * @param path
    *    path to file
    * @throws InterruptedException
    *    if the current thread is interrupted
    */
   synchronized void acquireRead(Path path) throws InterruptedException
   {
      int activeThreads;
      while ((activeThreads = getActiveThreads(path)) >= maxThreads)
      {
         wait();
      }
      // Increment number of active readers. All writers are blocked if at least one reader is active.
      threads.put(path, activeThreads + 1);
   }

   /**
    * Acquire write permit for file with <code>path</code>, blocking until write permit is available. Only one thread
    * may have write access to the file at the same time. Thread may be blocked until one of next things happens:
    * <ul>
    * <li>Last reader invokes method {@link #releaseRead(Path)} and current thread is next to get write permit</li>
    * <li>Writer which has acquired all available threads invokes method {@link #releaseRead(Path)}</li>
    * <li>Current thread is interrupted</li>
    * </ul>
    *
    * @param path
    *    path to file.
    * @throws InterruptedException
    *    if the current thread is interrupted.
    */
   synchronized void acquireWrite(Path path) throws InterruptedException
   {
      while (getActiveThreads(path) > 0)
      {
         wait();
      }
      // Put max allowed threads value. Current thread get exclusive access to file.
      threads.put(path, maxThreads);
   }

   /**
    * Acquire read permit for file with <code>path</code> if it becomes available within the given timeout. It is the
    * same as method {@link #acquireRead(Path)} but with waiting timeout. If waiting timeout reached then
    * TimeoutException thrown.
    *
    * @param timeoutMilliseconds
    *    maximum time (in milliseconds) to wait for read permit.
    * @param path
    *    path to file.
    * @throws InterruptedException
    *    if the current thread is interrupted.
    * @throws TimeoutException
    *    if waiting timeout reached
    * @see #acquireRead(Path)
    */
   synchronized void acquireRead(long timeoutMilliseconds, Path path) throws InterruptedException, TimeoutException
   {
      final long endTime = System.currentTimeMillis() + timeoutMilliseconds;
      long waitTime = timeoutMilliseconds;
      int activeThreads;
      while ((activeThreads = getActiveThreads(path)) >= maxThreads)
      {
         wait(waitTime);
         long now = System.currentTimeMillis();
         if (now > endTime)
         {
            throw new TimeoutException();
         }
         waitTime = endTime - now;
      }
      threads.put(path, activeThreads + 1);
   }

   /**
    * Acquire write permit for file with <code>path</code> if it becomes available within the given timeout. It is the
    * same as method {@link #acquireWrite(Path)} but with waiting timeout. If waiting timeout reached then
    * TimeoutException thrown.
    *
    * @param timeoutMilliseconds
    *    maximum time (in milliseconds) to wait for write permit.
    * @param path
    *    path to file.
    * @throws InterruptedException
    *    if the current thread is interrupted.
    * @throws TimeoutException
    *    if waiting timeout reached
    * @see #acquireWrite(Path)
    */
   synchronized void acquireWrite(long timeoutMilliseconds, Path path) throws InterruptedException, TimeoutException
   {
      final long endTime = System.currentTimeMillis() + timeoutMilliseconds;
      long waitTime = timeoutMilliseconds;
      while (getActiveThreads(path) > 0)
      {
         wait(waitTime);
         long now = System.currentTimeMillis();
         if (now > endTime)
         {
            throw new TimeoutException();
         }
         waitTime = endTime - now;
      }
      threads.put(path, maxThreads);
   }

   /**
    * Releases a read permit and return it to the lock. Increases the number of read permits. If there is no more active
    * readers then file may be accessed for write.
    *
    * @param path
    *    path to file.
    */
   synchronized void releaseRead(Path path)
   {
      int activeThreads = getActiveThreads(path);
      if (activeThreads == 1)
      {
         // last reader
         threads.remove(path);
      }
      else
      {
         threads.put(path, activeThreads - 1);
      }
      notifyAll();
   }

   /**
    * Releases a write permit and return it to the lock. File may be accessed for read or write.
    *
    * @param path
    *    path to file.
    */
   synchronized void releaseWrite(Path path)
   {
      threads.remove(path);
      notifyAll();
   }

   private int getActiveThreads(Path path)
   {
      Integer activeThread = threads.get(path);
      return activeThread == null ? 0 : activeThread;
   }
}
