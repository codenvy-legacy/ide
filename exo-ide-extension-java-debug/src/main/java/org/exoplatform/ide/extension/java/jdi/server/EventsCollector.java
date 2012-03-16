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
package org.exoplatform.ide.extension.java.jdi.server;

import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.event.EventQueue;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
final class EventsCollector implements Runnable
{
   private final EventsHandler handler;
   private final EventQueue queue;

   private final Thread thread;
   private volatile boolean running;

   EventsCollector(EventQueue queue, EventsHandler handler)
   {
      this.queue = queue;
      this.handler = handler;

      thread = new Thread(this);
      running = true;
      thread.start();
   }

   @Override
   public void run()
   {
      while (running)
      {
         try
         {
            handler.handleEvents(queue.remove());
         }
         catch (DebuggerException e)
         {
            e.printStackTrace();
            break;    // TODO
         }
         catch (VMDisconnectedException e)
         {
            break;
         }
         catch (InterruptedException e)
         {
            // Thread interrupted with method stop().
         }
      }
   }

   void stop()
   {
      running = false;
      thread.interrupt();
   }
}
