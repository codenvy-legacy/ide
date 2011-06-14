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
package org.exoplatform.ide.extension.logreader.client.event;

import java.util.Date;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class LogReaderSettingsChangedEvent extends GwtEvent<LogReaderSettingsChangedHandler>
{

   public static GwtEvent.Type<LogReaderSettingsChangedHandler> TYPE = new Type<LogReaderSettingsChangedHandler>();

   private Date date;

   private int limit;

   private int offset;

   /**
    * @param date
    * @param limit
    * @param offset
    */
   public LogReaderSettingsChangedEvent(Date date, int limit, int offset)
   {
      super();
      this.date = date;
      this.limit = limit;
      this.offset = offset;
   }

   /**
    * @return the date
    */
   public Date getDate()
   {
      return date;
   }

   /**
    * @return the limit
    */
   public int getLimit()
   {
      return limit;
   }

   /**
    * @return the offset
    */
   public int getOffset()
   {
      return offset;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<LogReaderSettingsChangedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(LogReaderSettingsChangedHandler handler)
   {
      handler.onLogRederSettingsChanged(this);
   }

}
