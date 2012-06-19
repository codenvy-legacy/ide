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
package org.exoplatform.ide.extension.java.jdi.client.events;

import org.exoplatform.ide.extension.java.jdi.client.EditorBreakPoint;

import java.util.Map;
import java.util.Set;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 3:42:30 PM Mar 28, 2012 evgen $
 * 
 */
public class BreakPointsUpdatedEvent extends GwtEvent<BreakPointsUpdatedHandler>
{

   public static final GwtEvent.Type<BreakPointsUpdatedHandler> TYPE = new Type<BreakPointsUpdatedHandler>();

   private Map<String, Set<EditorBreakPoint>> breakPoints;

   /**
    * @param breakPoints
    */
   public BreakPointsUpdatedEvent(Map<String, Set<EditorBreakPoint>> breakPoints)
   {
      super();
      this.breakPoints = breakPoints;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<BreakPointsUpdatedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(BreakPointsUpdatedHandler handler)
   {
      handler.onBreakPointsUpdated(this);
   }

   /**
    * @return the breakPoints
    */
   public Map<String, Set<EditorBreakPoint>> getBreakPoints()
   {
      return breakPoints;
   }

}
