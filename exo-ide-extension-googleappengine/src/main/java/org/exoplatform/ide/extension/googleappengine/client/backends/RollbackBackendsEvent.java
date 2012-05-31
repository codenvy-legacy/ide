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
package org.exoplatform.ide.extension.googleappengine.client.backends;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to rollback backend(s).
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 29, 2012 5:22:37 PM anya $
 * 
 */
public class RollbackBackendsEvent extends GwtEvent<RollbackBackendsHandler>
{
   /**
    * Type, used to register the event.
    */
   public static final GwtEvent.Type<RollbackBackendsHandler> TYPE = new GwtEvent.Type<RollbackBackendsHandler>();

   /**
    * Rollback all backends.
    */
   private boolean all;

   /**
    * Name of backend to rollback.
    */
   private String backendName;

   /**
    * @param all rollback all backends
    */
   public RollbackBackendsEvent(boolean all)
   {
      this.all = all;
      backendName = null;
   }

   /**
    * @param backendName name of backend to rollback
    */
   public RollbackBackendsEvent(String backendName)
   {
      this.backendName = backendName;
      this.all = false;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<RollbackBackendsHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(RollbackBackendsHandler handler)
   {
      handler.onRollbackBackend(this);
   }

   /**
    * @return the all
    */
   public boolean isAll()
   {
      return all;
   }

   /**
    * @return the backendName
    */
   public String getBackendName()
   {
      return backendName;
   }
}
