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
package org.exoplatform.ide.extension.groovy.server;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.defaults.AbstractPicoVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Cleaner of expired resources.
 * Check keys of container components and if key is instance of GroovyComponentKey and has attribute
 * 'ide.expiration.date' then check value of such attribute. If value of attribute is less than current time then
 * remove such component from container.
 * <p/>
 * After creation instance must be started by method {@link
 * org.exoplatform.ide.extension.groovy.server.ComponentCleaner#start()}. To stop cleaner need to call method
 * {@link org.exoplatform.ide.extension.groovy.server.ComponentCleaner#stop()}.
 * <p/>
 * Example:
 * <pre>
 * ...
 * // Check components one time per 10 minutes.
 * ComponentCleaner cleaner = new ComponentCleaner(restfulContainer, 10, TimeUnit.MINUTES);
 * // Container already started! So need to start cleaner manually.
 * cleaner.start();
 * // Register in container. Cleaner will be stopped automatically when container stopped.
 * container.registerComponentInstance("cleaner", cleaner);
 * ...
 * </pre>
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ComponentCleaner implements Startable
{
   private final MutablePicoContainer container;
   private final long cleanerDelay;

   /** Stop flag for cleaner. */
   private boolean cleanerStop;
   /** Cleaner thread. */
   private Thread thread;

   /**
    * Create new instance of ComponentCleaner.
    *
    * @param container the container which should be checked for expired resources.
    * @param cleanerDelay the delay time of cleaning.
    * @param unit the time unit for <code>cleanerDelay</code>.
    */
   public ComponentCleaner(MutablePicoContainer container, long cleanerDelay, TimeUnit unit)
   {
      this.container = container;
      this.cleanerDelay = unit.toMillis(cleanerDelay);
   }

   @Override
   public void start()
   {
      if (null == thread && cleanerDelay > 0)
      {
         thread = new Thread(new Cleaner());
         thread.setDaemon(true);
         thread.start();
      }
   }

   @Override
   public void stop()
   {
      if (null != thread)
      {
         cleanerStop = true;
         thread.interrupt(); // if sleep
         try
         {
            thread.join();
         }
         catch (InterruptedException ignored)
         {
         }
         thread = null;
      }
   }

   private class Cleaner extends AbstractPicoVisitor implements Runnable
   {
      private final List<ComponentAdapter> components = new ArrayList<ComponentAdapter>();

      @Override
      public Object traverse(Object node)
      {
         components.clear();
         final long currentTime = System.currentTimeMillis();
         try
         {
            super.traverse(node);
            for (ComponentAdapter adapter : components)
            {
               Object key = adapter.getComponentKey();
               if (key instanceof GroovyComponentKey)
               {
                  Long expiration = (Long)((GroovyComponentKey)key).getAttribute("ide.expiration.date");
                  if (expiration != null && expiration < currentTime)
                  {
                     ((MutablePicoContainer)node).unregisterComponent(key);
                  }
               }
            }
         }
         finally
         {
            components.clear();
         }
         return null;
      }

      @Override
      public void visitContainer(PicoContainer container)
      {
         checkTraversal();
         components.addAll(container.getComponentAdapters());
      }

      @Override
      public void visitComponentAdapter(ComponentAdapter componentAdapter)
      {
      }

      @Override
      public void visitParameter(Parameter parameter)
      {
      }

      public void run()
      {
         while (!cleanerStop)
         {
            try
            {
               Thread.sleep(cleanerDelay);
            }
            catch (InterruptedException e)
            {
            }
            if (!cleanerStop)
            {
               traverse(container);
            }
         }
      }
   }
}
