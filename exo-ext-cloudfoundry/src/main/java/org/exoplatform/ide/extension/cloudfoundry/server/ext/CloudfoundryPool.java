/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudfoundry.server.ext;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.extension.cloudfoundry.server.Cloudfoundry;
import org.exoplatform.ide.extension.cloudfoundry.server.SimpleAuthenticator;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Pool of pre-configured clients to Cloud Foundry servers.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryPool
{
   private volatile Balancer balancer;

   private final CopyOnWriteArrayList<CloudfoundryServerConfiguration> configs;
   private final ReentrantLock lock = new ReentrantLock();

   public CloudfoundryPool(InitParams initParams)
   {
      this(getConfigurations(initParams));
   }

   private static List<CloudfoundryServerConfiguration> getConfigurations(InitParams params)
   {
      List<CloudfoundryServerConfiguration> configs = new ArrayList<CloudfoundryServerConfiguration>();
      if (params != null)
      {
         List<CloudfoundryServerConfiguration> objectParams = params.getObjectParamValues(CloudfoundryServerConfiguration.class);
         if (!(objectParams == null || objectParams.isEmpty()))
         {
            configs.addAll(objectParams);
         }
      }
      return configs;
   }

   protected CloudfoundryPool(List<CloudfoundryServerConfiguration> configs)
   {
      this.configs = new CopyOnWriteArrayList<CloudfoundryServerConfiguration>(configs);
      init();
   }

   public Cloudfoundry next()
   {
      return balancer.next();
   }

   public Cloudfoundry byTargetName(String target)
   {
      for (Cloudfoundry cf : balancer.available())
      {
         String t = null;
         try
         {
            t = cf.getTarget();
         }
         catch (VirtualFileSystemException ignored)
         {
            // Never happen since we do not read target value from anywhere.
         }
         catch (IOException ignored)
         {
            // Never happen since we do not read target value from anywhere.
         }

         if (target.equals(t))
         {
            return cf;
         }
      }
      return null;
   }

   public void addConfiguration(CloudfoundryServerConfiguration config)
   {
      if (configs.addIfAbsent(config))
      {
         init();
      }
   }

   public void removeConfiguration(CloudfoundryServerConfiguration config)
   {
      if (configs.remove(config))
      {
         init();
      }
   }

   public CloudfoundryServerConfiguration[] getConfigurations()
   {
      return configs.toArray(new CloudfoundryServerConfiguration[configs.size()]);
   }

   private void init()
   {
      lock.lock();
      try
      {
         List<Cloudfoundry> list = new ArrayList<Cloudfoundry>(configs.size());
         for (CloudfoundryServerConfiguration config : configs)
         {
            list.add(new Cloudfoundry(new SimpleAuthenticator(config.getTarget(), config.getUser(), config.getPassword())));
         }
         balancer = new Balancer(list);
      }
      finally
      {
         lock.unlock();
      }
   }

   private static class Balancer
   {
      private final List<Cloudfoundry> queue;
      private final AtomicInteger position = new AtomicInteger();

      Balancer(List<Cloudfoundry> coll)
      {
         if (coll == null || coll.isEmpty())
         {
            throw new IllegalArgumentException("At least one server must be configured. ");
         }
         this.queue = Collections.unmodifiableList(coll);
      }

      Cloudfoundry next()
      {
         return queue.get(nextIndex());
      }

      List<Cloudfoundry> available()
      {
         return queue;
      }

      private int nextIndex()
      {
         for (; ; )
         {
            int current = position.get();
            int next = current + 1;
            if (next >= queue.size())
            {
               next = 0;
            }
            if (position.compareAndSet(current, next))
            {
               return current;
            }
         }
      }
   }
}
