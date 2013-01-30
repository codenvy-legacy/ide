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

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class FileLockFactoryTest extends TestCase
{
   private final int maxThreads = 3;
   private final Path path = Path.fromString("/a/b/c"); // Path not need to be real path on file system

   private FileLockFactory fileLockFactory;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      fileLockFactory = new FileLockFactory(maxThreads);
   }

   public void testLock() throws Exception
   {
      final AtomicBoolean acquired = new AtomicBoolean(false);
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            fileLockFactory.getLock(path, true).acquire();
            acquired.set(true);
         }
      };
      t.start();
      t.join(1000);
      assertTrue(acquired.get());
   }

   public void testConcurrentExclusiveLocks() throws Throwable
   {
      final AtomicInteger acquired = new AtomicInteger(0);
      final CountDownLatch waiter = new CountDownLatch(3);
      final List<Throwable> errors = new ArrayList<Throwable>(3);
      Runnable task = new Runnable()
      {
         @Override
         public void run()
         {
            FileLockFactory.FileLock exclusiveLock = fileLockFactory.getLock(path, true);
            try
            {
               exclusiveLock.acquire();
               // Only one thread has exclusive access
               assertEquals(0, acquired.getAndIncrement());
               Thread.sleep(100);
            }
            catch (Throwable e)
            {
               errors.add(e);
            }
            finally
            {
               exclusiveLock.release();
               acquired.getAndDecrement();
               waiter.countDown();
            }
         }
      };
      new Thread(task).start();
      new Thread(task).start();
      new Thread(task).start();
      waiter.await();
      assertEquals(0, acquired.get()); // all locks must be released
      if (!errors.isEmpty())
      {
         throw errors.get(0);
      }
   }

   public void testLockTimeout() throws Exception
   {
      final CountDownLatch starter = new CountDownLatch(1);
      Runnable task = new Runnable()
      {
         @Override
         public void run()
         {
            FileLockFactory.FileLock exclusiveLock = fileLockFactory.getLock(path, true);
            try
            {
               exclusiveLock.acquire();
               starter.countDown();
               Thread.sleep(2000); // get lock and sleep
            }
            catch (InterruptedException ignored)
            {
            }
            finally
            {
               exclusiveLock.release();
            }
         }
      };
      Thread t = new Thread(task);
      t.setDaemon(true);
      t.start();
      starter.await(); // wait while child thread acquire exclusive lock
      FileLockFactory.FileLock timeoutExclusiveLock = fileLockFactory.getLock(path, true);
      try
      {
         // Wait lock timeout is much less then sleep time of child thread.
         // Here we must be failed to get exclusive permit.
         timeoutExclusiveLock.acquire(100);
         fail();
      }
      catch (FileLockTimeoutException e)
      {
         // OK
      }
   }

   public void testConcurrentLocks() throws Throwable
   {
      final AtomicInteger acquired = new AtomicInteger(0);
      final CountDownLatch starter = new CountDownLatch(1);
      final CountDownLatch waiter = new CountDownLatch(2);
      Runnable task1 = new Runnable()
      {
         @Override
         public void run()
         {
            FileLockFactory.FileLock lock = fileLockFactory.getLock(path, false);
            try
            {
               lock.acquire();
               acquired.incrementAndGet();
               starter.countDown();
               Thread.sleep(1000);
            }
            catch (InterruptedException ignored)
            {
            }
            finally
            {
               lock.release();
               acquired.getAndDecrement();
               waiter.countDown();
            }
         }
      };
      final List<Throwable> errors = new ArrayList<Throwable>(1);
      Runnable task2 = new Runnable()
      {
         @Override
         public void run()
         {
            FileLockFactory.FileLock exclusiveLock = fileLockFactory.getLock(path, true);
            try
            {
               exclusiveLock.acquire();
               // This thread must be blocked while another thread keeps lock.
               assertEquals(0, acquired.getAndIncrement());
            }
            catch (Throwable e)
            {
               errors.add(e);
            }
            finally
            {
               exclusiveLock.release();
               acquired.getAndDecrement();
               waiter.countDown();
            }
         }
      };
      new Thread(task1).start();
      starter.await();
      new Thread(task2).start();
      waiter.await();
      assertEquals(0, acquired.get()); // all locks must be released

      if (!errors.isEmpty())
      {
         throw errors.get(0);
      }
   }
}
