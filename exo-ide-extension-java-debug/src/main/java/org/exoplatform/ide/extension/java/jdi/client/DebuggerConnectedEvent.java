/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class DebuggerConnectedEvent extends GwtEvent<DebuggerConnectedHandler>
{

   /**
    * Type used to register event.
    */
   public static final GwtEvent.Type<DebuggerConnectedHandler> TYPE = new GwtEvent.Type<DebuggerConnectedHandler>();

   /**
    * VFS id.
    */
   private DebuggerInfo debuggerInfo;

   /**
    * @param debuggerInfo
    */
   public DebuggerConnectedEvent(DebuggerInfo debuggerInfo)
   {
      this.debuggerInfo = debuggerInfo;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<DebuggerConnectedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(DebuggerConnectedHandler handler)
   {
      handler.onDebuggerConnected(this);
   }

   public DebuggerInfo getDebuggerInfo()
   {
      return debuggerInfo;
   }

}
