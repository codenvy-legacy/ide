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
package org.exoplatform.ide.vfs.server.impl.memory;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.vfs.server.URLHandlerFactorySetup;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFileSystemContext;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.exoplatform.ide.commons.ContainerUtils.readValuesParam;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class MemoryFileSystemInitializer implements Startable
{
   private static final Log LOG = ExoLogger.getExoLogger(MemoryFileSystemInitializer.class);

   private final VirtualFileSystemRegistry registry;
   private final Set<String> vfsIds;
   private final EventListenerList listeners;

   public MemoryFileSystemInitializer(InitParams initParams,
                                      VirtualFileSystemRegistry registry,
                                      EventListenerList listeners)
   {
      this(readValuesParam(initParams, "ids"), registry, listeners);
   }

   public MemoryFileSystemInitializer(Collection<String> vfsIds,
                                      VirtualFileSystemRegistry registry,
                                      EventListenerList listeners)
   {
      this.vfsIds = new HashSet<String>(vfsIds);
      this.registry = registry;
      this.listeners = listeners;
   }

   @Override
   public void start()
   {
      URLHandlerFactorySetup.setup(registry, listeners);
      for (String id : vfsIds)
      {
         try
         {
            registry.registerProvider(id, new MemoryFileSystemProvider(id, new MemoryFileSystemContext()));
         }
         catch (VirtualFileSystemException e)
         {
            LOG.error(e.getMessage(), e);
         }
      }
   }

   @Override
   public void stop()
   {
      for (String id : vfsIds)
      {
         try
         {
            registry.unregisterProvider(id);
         }
         catch (VirtualFileSystemException e)
         {
            LOG.error(e.getMessage(), e);
         }
      }
   }
}
