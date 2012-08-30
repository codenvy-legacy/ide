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
package org.exoplatform.ide.vfs.server.observation;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages list of EventListener.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EventListenerList
{
   private static final ErrorHandler DEFAULT_ERROR_HANDLER = new LogErrorHandler();

   private final CopyOnWriteArrayList<ListenerHolder> listeners;
   private final ErrorHandler errorHandler;

   public EventListenerList(ErrorHandler errorHandler)
   {
      if (errorHandler == null)
      {
         throw new IllegalArgumentException("ErrorHandler may not be null. ");
      }
      this.errorHandler = errorHandler;
      listeners = new CopyOnWriteArrayList<ListenerHolder>();
   }

   public EventListenerList()
   {
      this(DEFAULT_ERROR_HANDLER);
   }

   /**
    * Add new EventListener to the list if the same combination of listener and filter is not present.
    * Here is example how to add new EventListener that get events about updating content of all web.xml files in VFS
    * 'my-vfs':
    * <pre>
    *    ...
    *    EventListener listener = new EventListener()
    *    {
    *       public void handleEvent(ChangeEvent event) throws VirtualFileSystemException
    *       {
    *          // do something
    *       }
    *    };
    *    addEventListener(ChangeEventFilter.createAndFilter(
    *       new VfsIDFilter("my-vfs"), // from "my-vfs" only
    *       new TypeFilter(ChangeEvent.ChangeType.CONTENT_UPDATED), // only update of content
    *       new MimeTypeFilter("application/xml"), // media type application/xml
    *       new PathFilter("^(.&#042/)?web\\.xml")), // any web.xml files
    *       listener
    *    );
    * </pre>
    *
    * @param eventFilter
    *    filter for events. Listener get only events matched to the filter. If this parameter is <code>null</code>
    *    listener will get all events
    * @param listener
    *    EventListener
    * @return <code>true</code> if listener was added and <code>false</code> otherwise
    */
   public boolean addEventListener(ChangeEventFilter eventFilter, EventListener listener)
   {
      if (listener == null)
      {
         throw new IllegalArgumentException("EventListener may not be null. ");
      }
      return listeners.addIfAbsent(
         new ListenerHolder(eventFilter == null ? ChangeEventFilter.ANY_FILTER : eventFilter, listener));
   }

   /**
    * Remove listener from the list.
    *
    * @param eventFilter
    *    ChangeEventFilter
    * @param listener
    *    EventListener
    * @return <code>true</code> if listener was removed and <code>false</code> otherwise
    * @see #addEventListener(ChangeEventFilter, EventListener)
    */
   public boolean removeEventListener(ChangeEventFilter eventFilter, EventListener listener)
   {
      if (listener == null)
      {
         throw new IllegalArgumentException("EventListener may not be null. ");
      }
      return listeners.remove(new ListenerHolder(eventFilter == null ? ChangeEventFilter.ANY_FILTER : eventFilter, listener));
   }

   /**
    * Notify all EventListeners subscribed to this type of event.
    *
    * @param event
    *    ChangeEvent
    * @throws VirtualFileSystemException
    *    if any error occurs
    */
   public void notifyListeners(ChangeEvent event) throws VirtualFileSystemException
   {
      try
      {
         for (ListenerHolder listenerHolder : listeners)
         {
            if (listenerHolder.eventFilter.matched(event))
            {
               listenerHolder.listener.handleEvent(event);
            }
         }
      }
      catch (Throwable e)
      {
         errorHandler.onError(event, e);
      }
   }

   private static class ListenerHolder
   {
      final ChangeEventFilter eventFilter;
      final EventListener listener;

      private ListenerHolder(ChangeEventFilter eventFilter, EventListener listener)
      {
         this.eventFilter = eventFilter;
         this.listener = listener;
      }

      @Override
      public boolean equals(Object o)
      {
         if (this == o)
         {
            return true;
         }
         if (!(o instanceof ListenerHolder))
         {
            return false;
         }
         ListenerHolder other = (ListenerHolder)o;
         return eventFilter.equals(other.eventFilter) && listener.equals(other.listener);
      }

      @Override
      public int hashCode()
      {
         int hash = 7;
         hash = 31 * hash + eventFilter.hashCode();
         hash = 31 * hash + listener.hashCode();
         return hash;
      }
   }
}
